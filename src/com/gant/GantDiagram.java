package com.gant;

import com.analyze.AnalyzeManager;
import com.analyze.Task;
import com.com.grapheditor.SystemGraph;
import com.com.grapheditor.TaskGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

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
//        int minNumberOfNodes = minNumberOfNodes(tasks);
//        if(minNumberOfNodes == 0) {
//            System.out.println("No tasks to process!");
//            return;
//        }
//        if(minNumberOfNodes > tasks.size()){
//            System.out.println(minNumberOfNodes + " nodes needed, but only " + tasks.size() + " exists!");
//            return;
//        }
        System.out.println(tasks);
        RoutingModel routingModel = new RoutingModel(SystemGraph.graph);
        TaskModel tasksModel = new TaskModel(TaskGraph.graph, tasks);
        TaskPlanner planner = new TaskPlanner(routingModel, tasksModel, this);
        planner.assignTasksToNodes();
        planner.trimModel();
        System.out.println(planner.getModelOutput());
        initControls();
        drawModel(planner, true);
        int i = 1;
    }

    public void initControls(){
        final DefaultComboBoxModel IOProcessorPresence = new DefaultComboBoxModel();
        IOProcessorPresence.addElement("+IO");
        IOProcessorPresence.addElement("-IO");
        final JComboBox IOProcessorCombo = new JComboBox(IOProcessorPresence);
        IOProcessorCombo.setSelectedIndex(0);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 400));
        panel.setBackground(Color.blue);
        panel.add(IOProcessorCombo);
        JButton showButton = new JButton("Show");
        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String data = "";
                if (IOProcessorCombo.getSelectedIndex() != -1) {
                    if(IOProcessorCombo.getSelectedIndex() == 0){
                        Config.isIO = true;
                    } else {
                        Config.isIO = false;
                    }
                }
            }
        });
        add(panel);
        add(showButton);
    }

    public void drawModel(TaskPlanner planner, boolean toFrame){
        int lineHeight = VC.lineHeight;
        int nodeNamesDataWidth = VC.nodeNamesDataWidth;
        int ticWidth = VC.ticWidth;
        int sm = VC.space1;
        int md = VC.space2;

        BufferedImage img = new BufferedImage(planner.size()*ticWidth + ticWidth,
                planner.getModel().values().size()*lineHeight + lineHeight, BufferedImage.TYPE_INT_RGB);
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
//        for(int i=0; i<maxstep; i++){
//            int x = (int) (processor.Config.HEADWIDTH + (i + 0.5)*processor.Config.STEPWIDTH);
//            int y = index + processor.Config.HEIGHT/2;
//
//            FontMetrics fm = g.getFontMetrics ();
//            GlyphVector gv = g.getFont ().createGlyphVector(fm.getFontRenderContext (), Integer.toString(i + 1));
//            g.drawString(Integer.toString(i + 1), (int) (x - gv.getVisualBounds().getWidth()/2), (int) (y + gv.getVisualBounds().getHeight()/2));
//        }
        JPanel planpanel = new JPanel();
//        planpanel.setSize(new Dimension(img.getWidth(), index + processor.Config.HEIGHT));
//        planpanel.setMinimumSize(new Dimension(img.getWidth(), index + processor.Config.HEIGHT));
//        planpanel.setMaximumSize(new Dimension(img.getWidth(), index + processor.Config.HEIGHT));
//        planpanel.setPreferredSize(new Dimension(img.getWidth(), index + processor.Config.HEIGHT));

        int height = planner.getModel().values().size() + 1000;

        planpanel.setSize(new Dimension(img.getWidth(), height));
        planpanel.setMinimumSize(new Dimension(img.getWidth(), height));
        planpanel.setMaximumSize(new Dimension(img.getWidth(), height));
        planpanel.setPreferredSize(new Dimension(img.getWidth(), height));

        ImageIcon icon = new ImageIcon(img);
        JLabel label = new JLabel(icon);
        planpanel.add(label);
        try {
            remove(0);
        } catch (Exception e){}
        if(!toFrame){
            add(label);
        } else {
            moveToFrame(planpanel);
        }
    }

    public void moveToFrame(Component component){
        JFrame frame = new JFrame("Діаграма Ганта");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 500));
        frame.pack();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(component);
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.setContentPane(panel);
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
