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
import com.gant.planner.DrawGraph;
import com.gant.planner.NodeWorkflow;
import com.gant.planner.TaskPlanner;
import com.gant.planner.VC;
import com.modeling.DrawTable;
import com.modeling.GatherDataModel;
import com.modeling.ModelingResult;
import com.modeling.PlanningType;
import com.mxgraph.view.mxGraph;

/**
 * Created by Andrii Mozharovskyi on 28.05.2015.
 */
public class GantDiagram extends JPanel {
    static Map<String, ArrayList<NodeAction>> model;
    private JTabbedPane tabPane;

    private int min;
    private int max;
    private int count;
    private double connMin;
    private double connMax;
    private double connStep;
    private int repeats;
    private String factor;

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

        JButton modellingButton = new JButton("Modelling");
        modellingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initModellingControls();
            }
        });
        add(modellingButton);
    }

    public void init(){
        drawModel(generate(TaskGraph.graph, SystemGraph.graph), true);
    }

    public static TaskPlanner generate(mxGraph taskGraph, mxGraph systemGraph){
        List<Task> tasks = new ArrayList<>();
        switch(Config.queueType) {
            case CRITICAL:
                tasks = AnalyzeManager.getCriticalPathOrderQueue(taskGraph);
                break;
            case NORMAL_CRITICAL:
                tasks = AnalyzeManager.getCriticalPathNormalizationOrderQueue(taskGraph);
                break;
            case RANDOM:
                tasks = AnalyzeManager.getRandomOrderQueue(taskGraph);
                break;
            default:
                tasks = AnalyzeManager.getRandomOrderQueue(taskGraph);
        }
        RoutingModel routingModel = new RoutingModel(systemGraph);
        TaskModel tasksModel = new TaskModel(taskGraph, tasks);
        TaskPlanner planner = new TaskPlanner(routingModel, tasksModel);
        return planner;
    }

    public void initModellingControls(){

        JSpinner minTaskWeight = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JLabel minTaskWeightLabel = new JLabel("min", JLabel.TRAILING);
        minTaskWeightLabel.setLabelFor(minTaskWeight);

        JSpinner maxTaskWeight = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JLabel maxTaskWeightLabel = new JLabel("max", JLabel.TRAILING);
        maxTaskWeightLabel.setLabelFor(maxTaskWeight);

        JSpinner tskCountSpinner = new JSpinner(new SpinnerNumberModel(7, 7, 35, 7));
        JLabel tskCountSpinnerLabel = new JLabel("кількість", JLabel.TRAILING);
        tskCountSpinnerLabel.setLabelFor(tskCountSpinner);

        JSpinner minConn = new JSpinner(new SpinnerNumberModel(0.1, 0.1, 1.0, 0.1));
        JLabel minConnLabel = new JLabel("min", JLabel.TRAILING);
        minConnLabel.setLabelFor(minConn);

        JSpinner maxConn = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 1.0, 0.1));
        JLabel maxConnLabel = new JLabel("max", JLabel.TRAILING);
        maxConnLabel.setLabelFor(maxConn);

        JSpinner stepConn = new JSpinner(new SpinnerNumberModel(0.3, 0.1, 0.5, 0.1));
        JLabel stepConnLabel = new JLabel("крок", JLabel.TRAILING);
        stepConnLabel.setLabelFor(stepConn);

        JSpinner numberOfRepeats = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        JLabel numberOfRepeatsLabel = new JLabel("к-сть прогонів", JLabel.TRAILING);
        numberOfRepeatsLabel.setLabelFor(numberOfRepeats);

        String[] factorStrings = { "Speedup", "Effectiveness", "Time", "All" };
        JComboBox factorList = new JComboBox(factorStrings);
        factorList.setSelectedIndex(2);

        JButton showTableButton = new JButton("Show Table");
        showTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                min = (Integer) minTaskWeight.getValue();
                max = (Integer) maxTaskWeight.getValue();
                count = (Integer) tskCountSpinner.getValue();

                connMin = (Double) minConn.getValue();
                connMax = (Double) maxConn.getValue();
                connStep = (Double) stepConn.getValue();

                repeats = (Integer) numberOfRepeats.getValue();

                factor = (String) factorList.getSelectedItem();

                drawTable(evaluate(min, max, count, connMin, connMax, connStep, repeats));
            }
        });

        JButton showGraphButton = new JButton("Show Graph");
        showGraphButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                min = (Integer) minTaskWeight.getValue();
                max = (Integer) maxTaskWeight.getValue();
                count = (Integer) tskCountSpinner.getValue();

                connMin = (Double) minConn.getValue();
                connMax = (Double) maxConn.getValue();
                connStep = (Double) stepConn.getValue();

                repeats = (Integer) numberOfRepeats.getValue();

                factor = (String) factorList.getSelectedItem();

                drawGraph(evaluate(min, max, count, connMin, connMax, connStep, repeats));
            }
        });

        JButton showBothButton = new JButton("Graph&Table");
        showBothButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                min = (Integer) minTaskWeight.getValue();
                max = (Integer) maxTaskWeight.getValue();
                count = (Integer) tskCountSpinner.getValue();

                connMin = (Double) minConn.getValue();
                connMax = (Double) maxConn.getValue();
                connStep = (Double) stepConn.getValue();

                repeats = (Integer) numberOfRepeats.getValue();

                factor = (String) factorList.getSelectedItem();

                Map<Object, Map<Object, ModelingResult>> map = evaluate(min, max, count, connMin, connMax, connStep, repeats);
                drawGraph(map);
                drawTable(map);
            }
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setPreferredSize(new Dimension(100, 200));

        JPanel tasksPanel = new JPanel();
        tasksPanel.add(new JLabel("Задачі:       "));
        tasksPanel.add(minTaskWeightLabel);
        tasksPanel.add(minTaskWeight);
        tasksPanel.add(maxTaskWeightLabel);
        tasksPanel.add(maxTaskWeight);
        tasksPanel.add(tskCountSpinnerLabel);
        tasksPanel.add(tskCountSpinner);
        panel.add(tasksPanel);

        JPanel connPanel = new JPanel();
        connPanel.add(new JLabel("Звязність: "));
        connPanel.add(minConnLabel);
        connPanel.add(minConn);
        connPanel.add(maxConnLabel);
        connPanel.add(maxConn);
        connPanel.add(stepConnLabel);
        connPanel.add(stepConn);
        panel.add(connPanel);

        JPanel additionalPanel = new JPanel();
        additionalPanel.add(numberOfRepeatsLabel);
        additionalPanel.add(numberOfRepeats);
        additionalPanel.add(new JLabel("Критерій"));
        additionalPanel.add(factorList);
        panel.add(additionalPanel);

        JPanel controlsPanel = new JPanel();
        controlsPanel.add(showGraphButton);
        controlsPanel.add(showTableButton);
        controlsPanel.add(showBothButton);
        panel.add(controlsPanel);

        moveToFrame(panel, "Налаштування для збору статистики", 400, 200);
    }

    public int getFactorId(String str){
        Map<String, Integer> map = new TreeMap<>();
        map.put("Speedup", 0);
        map.put("Effectiveness", 1);
        map.put("Time", 2);
        return map.get(str);
    }

    public Map<Object, Map<Object, ModelingResult>> evaluate(int min, int max, int count, double connMin, double connMax,
                         double connStep, int repeats){
//        Map<Object, Map<Object, ModelingResult>> map = new GatherDataModel()
//                .planAlgorithms(min, max, count, connMin, connMax, connStep, repeats);
//        Map<Object, Map<Object, ModelingResult>> map = new GatherDataModel()
//                .planPhisLinks(min, max, count, connMin, connMax, connStep, repeats);
        Map<Object, Map<Object, ModelingResult>> map = new GatherDataModel()
                .planIO(min, max, count, connMin, connMax, connStep, repeats);
        return map;
    }

    public void drawTable(Map<Object, Map<Object, ModelingResult>> map) {
        int speed = 0;
        int eff = 1;
        int time = 2;

        String header = count+" задач, " + repeats + " прогонів";

        switch(factor) {
            case "Speedup":
                moveToFrame(new DrawTable(toDoublesMap(map, speed), 2, header), "Speedup", 900, 800);
                break;
            case "Effectiveness":
                moveToFrame(new DrawTable(toDoublesMap(map, eff), 1, header), "Effectiveness", 900, 800);
                break;
            case "Time":
                moveToFrame(new DrawTable(toDoublesMap(map, time), 1, header), "Time", 900, 800);
                break;
            case "All":
                moveToFrame(new DrawTable(toDoublesMap(map, speed), 2, header), "Speedup", 900, 800);
                moveToFrame(new DrawTable(toDoublesMap(map, eff), 1, header), "Effectiveness", 900, 800);
                moveToFrame(new DrawTable(toDoublesMap(map, time), 1, header), "Time", 900, 800);
        }
    }

    public void drawGraph(Map<Object, Map<Object, ModelingResult>> map) {
        int speed = 0;
        int eff = 1;
        int time = 2;

        String header = count+" задач, " + repeats + " прогонів";

        switch(factor) {
            case "Speedup":
                moveToFrame(new DrawGraph(toDoublesMap(map, speed), 2, header), "Speedup", 900, 800);
                break;
            case "Effectiveness":
                moveToFrame(new DrawGraph(toDoublesMap(map, eff), 1, header), "Effectiveness", 900, 800);
                break;
            case "Time":
                moveToFrame(new DrawGraph(toDoublesMap(map, time), 1, header), "Time", 900, 800);
                break;
            case "All":
                moveToFrame(new DrawGraph(toDoublesMap(map, speed), 2, header), "Speedup", 900, 800);
                moveToFrame(new DrawGraph(toDoublesMap(map, eff), 1, header), "Effectiveness", 900, 800);
                moveToFrame(new DrawGraph(toDoublesMap(map, time), 1, header), "Time", 900, 800);
        }
    }

    private Map<Object, Map<Object, Double>> toDoublesMap(Map<Object, Map<Object, ModelingResult>> map, int param){
        Map<Object, Map<Object, Double>> newMap = new TreeMap<>();

        for(Map.Entry<Object, Map<Object, ModelingResult>> entry : map.entrySet()){

            Map<Object, Double> innerNewMap = new TreeMap<>();

            for(Map.Entry<Object, ModelingResult> innerEntry : entry.getValue().entrySet()){
                Double value = 0.0;
                switch(param) {
                    case 0:
                        value = innerEntry.getValue().getSpeedup();
                        break;
                    case 1:
                        value = innerEntry.getValue().getEffectiveness();
                        break;
                    case 2:
                        value = innerEntry.getValue().getTime();
                }
                innerNewMap.put(innerEntry.getKey(), value);
            }

            newMap.put(entry.getKey(), innerNewMap);

        }
        return newMap;
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

        moveToFrame(planpanel, "Діаграма Ганта", 800, 600);
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
