package com.gant;

import com.analyze.AnalyzeManager;
import com.analyze.Task;
import com.com.grapheditor.SystemGraph;
import com.com.grapheditor.TaskGraph;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import com.gant.model.ObjectWeight;
import com.gant.model.TaskModel;
import com.gant.model.RoutingModel;
import com.gant.planner.NodeWorkflow;
import com.gant.planner.TaskPlanner;
import com.gant.planner.VC;

/**
 * Created by Andrii Mozharovskyi on 28.05.2015.
 */
public class GantDiagram extends JPanel {
    static Map<String, ArrayList<NodeAction>> model;
    private JTabbedPane tabPane;

    public GantDiagram(JTabbedPane tabPane) {
        model = new HashMap<>();
        this.tabPane = tabPane;

        JButton configButton = new JButton("Config");
        configButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initControls();
            }
        });
        add(configButton);
    }

    public void init(){
        List<Task> tasks = new ArrayList<>();
        switch(Config.queueType) {
            case CRITICAL:
                tasks = AnalyzeManager.getCriticalPathOrderQueue(TaskGraph.graph);
                break;
            case NORMAL_CRITICAL:
                tasks = AnalyzeManager.getCriticalPathNormalizationOrderQueue(TaskGraph.graph);
                break;
            case RANDOM:
                tasks = AnalyzeManager.getRandomOrderQueue(TaskGraph.graph);
                break;
            default:
                tasks = AnalyzeManager.getRandomOrderQueue(TaskGraph.graph);
        }
        System.out.println(tasks);
        RoutingModel routingModel = new RoutingModel(SystemGraph.graph);
        TaskModel tasksModel = new TaskModel(TaskGraph.graph, tasks);
        TaskPlanner planner = new TaskPlanner(routingModel, tasksModel, this);
        planner.assignTasksToNodes();
        planner.trimModel();
        drawModel(planner, true);
    }

    public void initControls(){
        JPanel panel = new JPanel();
        JCheckBox IObox = new JCheckBox("IO Processor");
        IObox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == 1) {
                    Config.isIO = true;
                } else {
                    Config.isIO = false;
                }
            }
        });

        JCheckBox duplexBox = new JCheckBox("Duplex");
        duplexBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == 1) {
                    Config.duplex = true;
                } else {
                    Config.duplex = false;
                }
            }
        });

        final DefaultComboBoxModel queueType = new DefaultComboBoxModel();
        queueType.addElement("Random");
        queueType.addElement("Critical path");
        queueType.addElement("Norm crit path");
        final JComboBox queueTypeCombo = new JComboBox(queueType);
        queueTypeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(queueTypeCombo.getSelectedIndex() == 0) {
                    Config.queueType = Config.QueueType.RANDOM;
                } else if(queueTypeCombo.getSelectedIndex() == 1){
                    Config.queueType = Config.QueueType.CRITICAL;
                } else {
                    Config.queueType = Config.QueueType.NORMAL_CRITICAL;
                }
            }
        });
        queueTypeCombo.setSelectedIndex(2);

        final DefaultComboBoxModel assignmentType = new DefaultComboBoxModel();
        assignmentType.addElement("Random");
        assignmentType.addElement("Neighbor 5");
        final JComboBox assignmentTypeCombo = new JComboBox(assignmentType);
        assignmentTypeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(assignmentTypeCombo.getSelectedIndex() == 0) {
                    Config.assignmentType = Config.AssignmentType.RANDOM;
                } else {
                    Config.assignmentType = Config.AssignmentType.NEIGHBOR_5;
                }
            }
        });
        assignmentTypeCombo.setSelectedIndex(1);


        SpinnerModel model = new SpinnerNumberModel(1, 1, 5, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSpinner js = (JSpinner) e.getSource();
                Config.physLinksNumber = (Integer) js.getValue();
            }
        });
        JLabel phisLinksLabel = new JLabel("Phis links");

        JButton showButton = new JButton("Show");
        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                init();
            }
        });
        panel.add(IObox);
        panel.add(duplexBox);
        panel.add(queueTypeCombo);
        panel.add(assignmentTypeCombo);
        panel.add(spinner);
        panel.add(phisLinksLabel);

        panel.add(showButton);
        moveToFrame(panel, "Налаштування", 700, 80);
    }

    public void drawModel(TaskPlanner planner, boolean toFrame){
        Queue<ObjectWeight> numbersOfLinks = new PriorityQueue<>(ObjectWeight.getReverseComparator());
        for(Integer nodeId : planner.getModel().keySet()){
            numbersOfLinks.add(new ObjectWeight(planner.getRoutingModel().get(nodeId).getLinks().size(), nodeId));
        }
        int maxLogLinks = numbersOfLinks.peek().getWeight();
        int rowHeightInParts = maxLogLinks < Config.physLinksNumber ? maxLogLinks : Config.physLinksNumber;
        VC.lineHeight = VC.recordHeight*rowHeightInParts + VC.recordHeight;

        if(Config.duplex){
            VC.ticWidth = VC.doubleTicWidth;
        }

        int lineHeight = VC.lineHeight;
        int nodeNamesDataWidth = VC.nodeNamesDataWidth;
        int ticWidth = VC.ticWidth;
        int sm = VC.space1;
        int md = VC.space2;

        BufferedImage img = new BufferedImage(planner.size()*ticWidth + ticWidth,
                planner.getModel().values().size()*lineHeight + lineHeight*2, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        int index = 0;

        Collection<NodeWorkflow> workflows = planner.getModel().values();
        for(NodeWorkflow workflow : workflows){
            workflow.draw(g, index, planner.size());
            index++;
        }

        int lastWorkflowBootmLineY = workflows.size() * lineHeight + md;

        for (int i = 0; i < planner.size(); i++) {
            int currentLeftX = sm + nodeNamesDataWidth + i*ticWidth;
            int currentRightX = sm + nodeNamesDataWidth + i*ticWidth + ticWidth;
            int cellCenterX = currentLeftX + (currentRightX - currentLeftX)/2;
            g.drawString((i+1)+"", cellCenterX, lastWorkflowBootmLineY + md*2);
            g.drawLine(currentLeftX, lastWorkflowBootmLineY, currentLeftX, lastWorkflowBootmLineY + lineHeight);
        }

        lastWorkflowBootmLineY += lineHeight + md*2;
        g.drawString(planner.getTaskModel().getDefaultTaskQueue().toString(), md, lastWorkflowBootmLineY);

        JPanel planpanel = new JPanel();
        int height = planner.getModel().values().size() + 1000;
        planpanel.setSize(new Dimension(img.getWidth(), height));
        planpanel.setMinimumSize(new Dimension(img.getWidth(), height));
        planpanel.setMaximumSize(new Dimension(img.getWidth(), height));
        planpanel.setPreferredSize(new Dimension(img.getWidth(), height));

        ImageIcon icon = new ImageIcon(img);
        JLabel label = new JLabel(icon);
        planpanel.add(label);

        moveToFrame(planpanel, "Діаграма Ганта", 800, 500);
    }

    public void moveToFrame(Component component, String frameName, int width, int height){
        JFrame frame = new JFrame(frameName);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(width, height));
        frame.pack();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(component);
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.enableInputMethods(true);
        frame.setVisible(true);
    }

    public int minNumberOfNodes(List<Task> tasks){
        if(tasks.size() == 0){
            return 0;
        }
        Integer criticalPathWeight = tasks.get(0) == null ? 0 : tasks.get(0).getCriticalPathWithVertex();

        int taskWeightsSum = 0;
        for(Task task : tasks){
            taskWeightsSum += task.getWeight();
        }

        return criticalPathWeight == 0 ? 0 : taskWeightsSum/criticalPathWeight;
    }
}
