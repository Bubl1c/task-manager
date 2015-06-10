package com;

import com.com.grapheditor.SystemGraph;
import com.com.grapheditor.TaskGraph;
import com.gant.GantDiagram;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MCS{
    private JFrame frame;
    private JTabbedPane tabPane;

    private GantDiagram gantDiagram;

    public MCS(){
        frame = new JFrame("Моделювання");
        tabPane = new JTabbedPane();
        tabPane.add(new TaskGraph(tabPane));
        tabPane.add(new SystemGraph());
        gantDiagram = new GantDiagram(tabPane);
        JScrollPane scrollPane = new JScrollPane(gantDiagram);
        tabPane.add(scrollPane);
        tabPane.setTitleAt(0, "Граф задачі");
        tabPane.setTitleAt(1, "Граф КС");
        tabPane.setTitleAt(2, "Розподіл");
        tabPane.setBackground(Color.LIGHT_GRAY);
        tabPane.setTabPlacement(JTabbedPane.LEFT);
        bindTabHandlers(tabPane);
    }
    public void launchFrame() {
        //init frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 500));
        frame.pack();
        frame.add(tabPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void bindTabHandlers(JTabbedPane tabPane){
//        tabPane.addChangeListener(new ChangeListener() { //add the Listener
//
//            public void stateChanged(ChangeEvent e) {
//                if (tabPane.getSelectedIndex() == 2){
//                    //gantDiagram.init();
//                    gantDiagram.initControls();
//                }
//            }
//        });
    }

    public static void main(String[] args) {
        MCS editor = new MCS();
        editor.launchFrame();
    }
}
