package com.modeling;

import com.gant.planner.DrawGraph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

/**
 * Created by Andrii on 12.06.2015.
 */
public class DrawTable extends JPanel {

    private double MAX_SCORE = 5;
    private int DATA_POINTS_X = 20;
    private int Y_HATCH_CNT = 20;

    int cols;
    int rows;
    private Map<Object, Map<Object, Double>> scores;
    private String header;
    private String[] columnNames;
    private Object[][] data;

    public DrawTable(Map<Object, Map<Object, Double>> scores, int y_max, String header) {
        this.scores = scores;
        this.header = header;
        for(Map.Entry<Object, Map<Object, Double>> entry : scores.entrySet()){
            DATA_POINTS_X = entry.getValue().keySet().size();
            double max = DrawGraph.max(scores);
            MAX_SCORE = y_max > max ? y_max : max*1.5;
            break;
        }
        cols = ((Map<Object, Double>)scores.values().toArray()[0]).keySet().size();
        rows = scores.keySet().size();
        columnNames = getColNames();
        data = getData();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        JPanel panel = new JPanel();

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        JList rowHeader = new JList(scores.keySet().toArray());
        rowHeader.setFixedCellWidth(50);

        rowHeader.setFixedCellHeight(table.getRowHeight()
                + table.getRowMargin() - 1);

        rowHeader.setCellRenderer(new RowHeaderRenderer(table));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setRowHeaderView(rowHeader);
        add(scroll, BorderLayout.CENTER);
    }

    private Object[][] getData(){
        Object[][] data = new Object[rows][cols];
        int i = 0;
        int j = 0;
        for(Map.Entry<Object, Map<Object, Double>> entry : scores.entrySet()){
            j = 0;
            for(Double d : entry.getValue().values()){
                try {
                    String currentStr = d.toString();
                    currentStr = currentStr.substring(0, currentStr.length() <= 5 ? currentStr.length() : 5);
                    data[i][j] = currentStr;
                } catch (Exception e){
                    e.printStackTrace();
                }
                j++;
            }
            i++;
        }
        return data;
    }

    private String[] getColNames(){
        java.util.List<String> arr = new ArrayList<>();
        for(Map.Entry<Object, Map<Object, Double>> entry : scores.entrySet()){
            for(Object o : entry.getValue().keySet()){
                String currentStr = o.toString();
                currentStr = currentStr.substring(0, currentStr.length() <= 5 ? currentStr.length() : 5);
                arr.add(currentStr);
            }
            break;
        }
        String[] stringArr = new String[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            stringArr[i] = arr.get(i);
        }
        return stringArr;
    }

//    private void drawHeader(Graphics2D g2, String header){
//        Font oldFont = g2.getFont();
//        Color oldColor = g2.getColor();
//
//        g2.setColor(Color.RED);
//        g2.setFont(new Font("TimesRoman", Font.BOLD, 16));
//        g2.drawString(header, getWidth()/2 - header.length()/2, MD + BORDER_GAP);
//
//        g2.setColor(oldColor);
//        g2.setFont(oldFont);
//    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }
}
