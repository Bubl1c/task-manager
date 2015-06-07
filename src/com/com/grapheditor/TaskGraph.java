package com.com.grapheditor;

import com.analyze.AnalyzeManager;
import com.analyze.Task;
import com.constants.Constants;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class TaskGraph extends JPanel {
    public static mxGraph graph;
    public static mxGraphComponent graphPanel;
    public static int X;
    public static int Y;
    public static JPopupMenu popupPanel;
    public static final String VERTEX_VALUES_SEPARATOR = "\n\n";

    public static enum OrderMethod {
        CRITICAL_PATH_NORMALIZATION, CRITICAL_PATH, RANDOM
    }

    public TaskGraph(JTabbedPane tabPane){
        //popupPanel
        popupPanel = new JPopupMenu();
        JMenuItem taskItem;
        JMenuItem openItem;
        JMenuItem saveItem;
        JMenuItem hasCycleItem;
        JMenuItem generateItem;
        JMenuItem queueNormalCriticapPathItem;
        JMenuItem queueCriticapPathItem;
        JMenuItem queueRandimItem;
        popupPanel.add(taskItem = new JMenuItem("Додати задачу"));
        popupPanel.addSeparator();
        popupPanel.add(openItem = new JMenuItem("Выдкрити файл"));
        popupPanel.addSeparator();
        popupPanel.add(saveItem = new JMenuItem("Зберегти у файл"));
        popupPanel.addSeparator();
        popupPanel.add(hasCycleItem = new JMenuItem("Пешук циклу"));
        popupPanel.addSeparator();
        popupPanel.add(generateItem = new JMenuItem("Згенерувати граф"));
        popupPanel.addSeparator();
        popupPanel.add(queueNormalCriticapPathItem  = new JMenuItem("Черга 1: критичний шлях з нормалізацією"));
        popupPanel.addSeparator();
        popupPanel.add(queueCriticapPathItem = new JMenuItem("Черга 3: критичний шлях"));
        popupPanel.addSeparator();
        popupPanel.add(queueRandimItem = new JMenuItem("Черга 13: випадково"));

        generateItem.addActionListener(new ActionListener() {
            private JTabbedPane tabPane;
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionManager.generateGraphAction(tabPane);
            }
            public ActionListener setTabPane(JTabbedPane tabPane){
                this.tabPane = tabPane;
                return this;
            }
        }.setTabPane(tabPane));

        taskItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVertex();
            }
        });

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionManager.openFileAction(graph);
            }
        });

        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionManager.saveAsFileAction(graph);
            }
        });

        hasCycleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean hasCycle = ActionManager.hasCycle(graph);
                String message = "";
                if(hasCycle){
                    message += "Граф циклічний!";
                } else {
                    message += "Цикли відсутні!";
                }
                JOptionPane.showMessageDialog(null, message);
            }
        });

        queueNormalCriticapPathItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AnalyzeManager.printTaskQueue(AnalyzeManager.getCriticalPathNormalizationOrderQueue(graph),
                        OrderMethod.CRITICAL_PATH_NORMALIZATION);
            }
        });
        queueCriticapPathItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AnalyzeManager.printTaskQueue(AnalyzeManager.getCriticalPathOrderQueue(graph),
                        OrderMethod.CRITICAL_PATH);
            }
        });
        queueRandimItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AnalyzeManager.printTaskQueue(AnalyzeManager.getRandomOrderQueue(graph),
                        OrderMethod.RANDOM);
            }
        });

        init(this);
    }

    public  static void init( TaskGraph taskGraph ){
        //init graph
        graph = new mxGraph();
        graphPanel = new mxGraphComponent(graph);
        graph.setAllowDanglingEdges(false);
        graphPanel.getGraphControl().addMouseListener(new TaskGraphMouselistener(graphPanel, popupPanel));
        taskGraph.setLayout(new BorderLayout());
        taskGraph.add(graphPanel, BorderLayout.CENTER);

        buildGraphEnvironment();
        ActionManager.openFile(new File("E:\\tasks.mxe"), graph);
    }

    public static void buildGraphEnvironment() {
        try  {
            String nodeXMLTaskNode = mxUtils.readFile(Constants.PATH_TO_SHAPES + "process.shape");
            addStencilShape(nodeXMLTaskNode);
        }
        catch(IOException e) {
            System.out.println("IOException: " + e);
        }
        // stylesheet
        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_RESIZABLE, false);
        style.put(mxConstants.STYLE_SHAPE, "G&S - Process");
        style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ROUNDED, true);
        stylesheet.putCellStyle("TASK_CELL_STYLE", style);
        graph.setAllowLoops(false);
        graph.setAllowDanglingEdges(false);
        graph.setLabelsClipped(true);
        graph.setCellsEditable(false);
        graph.setCellsCloneable(false);
    }

    private static void addStencilShape(String nodeXML){
        int lessthanIndex = nodeXML.indexOf("<");
        nodeXML = nodeXML.substring(lessthanIndex);
        mxStencilShape newShape = new mxStencilShape(nodeXML);
        mxGraphics2DCanvas.putShape(newShape.getName(), newShape);
    }

    public static void addVertex(){
        graph.getModel().beginUpdate();
        try {
            mxCell cell = (mxCell)graph.insertVertex(graph.getDefaultParent(), null, "", X, Y, 50,50,"TASK_CELL_STYLE");
            cell.setValue((Integer.parseInt(cell.getId())-1)+VERTEX_VALUES_SEPARATOR+"1");
        }
        finally {
            graph.getModel().endUpdate();
        }
    }


    private static class TaskGraphMouselistener extends MouseAdapter{

        private mxGraphComponent graphComponent;
        private JPopupMenu popupPanel;

        TaskGraphMouselistener(mxGraphComponent graphComponent, JPopupMenu popupPanel){
            this.graphComponent = graphComponent;
            this.popupPanel = popupPanel;
        }

        public void mouseClicked(MouseEvent event) {
            ActionManager.mouseClickedAction(event,graph,graphComponent,"Обчислювальна складність",true);
        }

        public void mouseReleased(MouseEvent event){
            ActionManager.mouseReleasedAction(event,graph,graphComponent,popupPanel,true);
        }
    }


}
