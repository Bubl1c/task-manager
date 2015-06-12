package com.com.grapheditor;

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

public class SystemGraph extends JPanel {
    public static mxGraph graph;
    static mxGraphComponent graphPanel;
    static int X;
    static int Y;
    static JPopupMenu popupPanel;

    public SystemGraph(){
        //popupPanel
        popupPanel = new JPopupMenu();
        JMenuItem sysItem;
        JMenuItem openItem;
        JMenuItem saveItem;
        JMenuItem checkItem;
        popupPanel.add(sysItem = new JMenuItem("Додати КС"));
        popupPanel.addSeparator();
        popupPanel.add(openItem = new JMenuItem("Відкрити файл"));
        popupPanel.addSeparator();
        popupPanel.add(saveItem = new JMenuItem("Зберегти в файл"));
        popupPanel.addSeparator();
        popupPanel.add(checkItem = new JMenuItem("Перевірити на зв'язність"));

        checkItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "";
                if(ActionManager.checkConnectivity(graph)) {
                    message += "Граф зв'язний!";
                } else {
                    message += "Граф не зв'язний!";
                }
                JOptionPane.showMessageDialog(null, message);
            }
        });

        sysItem.addActionListener(new ActionListener() {
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
        init(this);
    }

    public  static void init(SystemGraph systemgraph){
        //init graph
        graph = new mxGraph();
        graphPanel = new mxGraphComponent(graph);
        graph.setAllowDanglingEdges(false);
        graphPanel.getGraphControl().addMouseListener(new SystemGraphMouselistener(graphPanel,popupPanel));
        systemgraph.setLayout(new BorderLayout());
        systemgraph.add(graphPanel, BorderLayout.CENTER);

        buildGraphEnvironment();
        ActionManager.openFile(new File("E:\\course_system.mxe"), graph);
    }

    public static void buildGraphEnvironment() {
        try  {
            String nodeXMLSystemNode = mxUtils.readFile(Constants.PATH_TO_SHAPES + "monitor.shape");
            addStencilShape(nodeXMLSystemNode);
        }
        catch(IOException ioe) {
            System.out.println("IOException: " + ioe);
        }
        // stylesheet
        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_RESIZABLE, false);
        style.put(mxConstants.STYLE_SHAPE, "Network - A Workstation Monitor");
        style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        style.put(mxConstants.STYLE_FONTCOLOR, "#FFFFFF");
        style.put(mxConstants.STYLE_FILLCOLOR, "#5882FA");

        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ENDARROW,mxConstants.NONE);
        stylesheet.putCellStyle("SYSTEM_CELL_STYLE", style);
        graph.setStylesheet(stylesheet);
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
            mxCell cell = (mxCell)graph.insertVertex(graph.getDefaultParent(), null, "", X, Y, 70,70,"SYSTEM_CELL_STYLE");
            cell.setValue("["+"1"+"]"+"\n"+"id = "+(Integer.parseInt(cell.getId())-1));
        }
        finally {
            graph.getModel().endUpdate();
        }
    }

    private static class SystemGraphMouselistener extends MouseAdapter {
        private mxGraphComponent graphComponent;
        private JPopupMenu popupPanel;

        SystemGraphMouselistener(mxGraphComponent graphComponent, JPopupMenu popupPanel){
            this.graphComponent = graphComponent;
            this.popupPanel = popupPanel;
        }
        public void mouseClicked(MouseEvent event) {
            ActionManager.mouseClickedAction(event,graph,graphComponent,"Продуктивність системи",false);
        }

        public void mouseReleased(MouseEvent event){
            ActionManager.mouseReleasedAction(event,graph,graphComponent,popupPanel,false);
        }
    }
}
