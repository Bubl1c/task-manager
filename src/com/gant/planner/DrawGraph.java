package com.gant.planner;

/**
 * Created by Andrii on 10.06.2015.
 */
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import javax.swing.*;

@SuppressWarnings("serial")
public class DrawGraph extends JPanel {
    private static final int PREF_W = 800;
    private static final int PREF_H = 650;
    private static final int BORDER_GAP = 40;
    private static final Color GRAPH_POINT_COLOR = Color.black;
    private static final Stroke GRAPH_STROKE = new BasicStroke(1f);
    private static final int GRAPH_POINT_WIDTH = 2;

    private double MAX_SCORE = 5;
    private int DATA_POINTS_X = 20;
    private int Y_HATCH_CNT = 20;

    public static final int SM = 10;
    public static final int MD = 15;

    Map<Object, Map<Object, Double>> scores;
    String header;

    public DrawGraph(Map<Object, Map<Object, Double>> scores, int y_max, String header) {
        this.scores = scores;
        this.header = header;
        for(Map.Entry<Object, Map<Object, Double>> entry : scores.entrySet()){
            DATA_POINTS_X = entry.getValue().keySet().size();
            double max = max(scores);
            MAX_SCORE = y_max > max ? y_max : max*1.5;
            break;
        }
        System.out.println("MAXXXX: " + max(scores));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (DATA_POINTS_X - 1);
        double yScale = ((double) getHeight() - BORDER_GAP) / (MAX_SCORE);

        drawHeader(g2, header);

        int i = 1;
        drawAxes(g2);
        for (Map.Entry<Object, Map<Object, Double>> entry : scores.entrySet()) {
            List list = new ArrayList(entry.getValue().values());
            Color lineColor = getColor(i);
            drawLine(g2, list, xScale, yScale, lineColor);
            drawLineLabel(g2, entry.getKey().toString(), lineColor, i);
            i++;
        }
    }

    private void drawLine(Graphics2D g2, List<Double> scores, double xScale, double yScale, Color color){
        System.out.println(scores);

        List<Point2D> graphPoints = new ArrayList<Point2D>();
        for (int i = 0; i < DATA_POINTS_X; i++) {
            double x1 = (i * xScale + BORDER_GAP);
            double y1 = ((MAX_SCORE - scores.get(i)) * yScale /*+ BORDER_GAP*/);
            graphPoints.add(new Point2D.Double(x1, y1));
        }

        Stroke oldStroke = g2.getStroke();
        Color oldColor = g2.getColor();

        g2.setColor(color);
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            double x1 = graphPoints.get(i).getX();
            double y1 = graphPoints.get(i).getY();
            double x2 = graphPoints.get(i + 1).getX();
            double y2 = graphPoints.get(i + 1).getY();
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        g2.setStroke(oldStroke);
        g2.setColor(GRAPH_POINT_COLOR);
        for (int i = 0; i < graphPoints.size(); i++) {
            double x = graphPoints.get(i).getX() - GRAPH_POINT_WIDTH / 2;
            double y = graphPoints.get(i).getY() - GRAPH_POINT_WIDTH / 2;
            double ovalW = GRAPH_POINT_WIDTH;
            double ovalH = GRAPH_POINT_WIDTH;
            g2.draw(new Ellipse2D.Double(x, y, ovalW, ovalH));
        }

        g2.setColor(oldColor);
        g2.setStroke(oldStroke);
    }

    private void drawLineLabel(Graphics2D g2, String label, Color color, int index){
        Font oldFont = g2.getFont();
        Color oldColor = g2.getColor();

        g2.setColor(color);
        g2.setFont(new Font("TimesRoman", Font.BOLD, 14));
        g2.drawString(label, BORDER_GAP + SM, index*MD + BORDER_GAP);

        g2.setColor(oldColor);
        g2.setFont(oldFont);
    }

    private void drawHeader(Graphics2D g2, String header){
        Font oldFont = g2.getFont();
        Color oldColor = g2.getColor();

        g2.setColor(Color.RED);
        g2.setFont(new Font("TimesRoman", Font.BOLD, 16));
        g2.drawString(header, getWidth()/2 - header.length()/2, MD + BORDER_GAP);

        g2.setColor(oldColor);
        g2.setFont(oldFont);
    }

    private Color getRandomColor(int index){
        Random random = new Random();
        final float hue = random.nextFloat();
        final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
        final float luminance = (0.9f * index) > 1 ? (0.9f * index) / 5 : (0.9f * index); //1.0 for brighter, 0.0 for black
        return Color.getHSBColor(hue, saturation, luminance);
    }

    private Color getColor(int index){
        List<Color> colors = new ArrayList<>();
        colors.add(new Color(7, 185, 44));
        colors.add(new Color(255, 6, 241));
        colors.add(new Color(0, 9, 255));
        colors.add(new Color(184, 148, 0));
        colors.add(new Color(255, 0, 0));
        colors.add(new Color(113, 95, 187));

        colors.add(new Color(86, 168, 172));
        colors.add(new Color(179, 72, 165));
        colors.add(new Color(54, 110, 43));
        colors.add(new Color(187, 104, 60));
        colors.add(new Color(187, 60, 0));
        return colors.get(index*78%11);
    }

    private void drawAxes(Graphics2D g2){
        //        create x and y axes
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

        Stroke oldStroke = g2.getStroke();
        Color oldColor = g2.getColor();

        g2.setColor(Color.gray);
        g2.setStroke(new BasicStroke(0.5f));

        // create hatch marks for y axis.
        for (int i = 0; i < Y_HATCH_CNT; i++) {
            int x0 = BORDER_GAP;
            int x1 = /*GRAPH_POINT_WIDTH + BORDER_GAP*/ getWidth() - BORDER_GAP;
            int y0 = getHeight() - (((i) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
            int y1 = y0;
            g2.drawLine(x0, y0, x1, y1);
            String currentStr = (MAX_SCORE/Y_HATCH_CNT*i+"");
            currentStr = currentStr.substring(0, currentStr.length() <= 4 ? currentStr.length() : 4);
            g2.drawString(currentStr, x0 - MD*2, y0);
        }

        Set keySet2 = getXAxesValues();
        int i = 0;
        for(Object o : keySet2){
            int x0 = (i) * (getWidth() - BORDER_GAP * 2) / (DATA_POINTS_X - 1) + BORDER_GAP;
            int x1 = x0;
            int y0 = getHeight() - BORDER_GAP;
            int y1 = /*y0 - GRAPH_POINT_WIDTH*/ BORDER_GAP;
            g2.drawLine(x0, y0, x1, y1);
            String currentStr = o.toString();
            currentStr = currentStr.substring(0, currentStr.length() <= 3 ? currentStr.length() : 3);
            g2.drawString(currentStr, x0, y0 + MD);
            i++;
        }

        g2.setColor(oldColor);
        g2.setStroke(oldStroke);
    }


    private Set getXAxesValues(){
        for(Map.Entry<Object, Map<Object, Double>> entry : scores.entrySet()){
            Set set = new TreeSet<>(entry.getValue().keySet());
            return set;
        }
        return null;
    }

    private Collection getYAxesValues(){
        for(Map.Entry<Object, Map<Object, Double>> entry : scores.entrySet()){
            Set set = new TreeSet<>(entry.getValue().values());
            return set;
        }
        return null;
    }

    public static Double max(Map<Object, Map<Object, Double>> scores){
        List<Double> maxValues = new ArrayList<>();
        for(Map.Entry<Object, Map<Object, Double>> entry : scores.entrySet()){
            List list = new ArrayList(entry.getValue().values());
            maxValues.add(max(list));
        }
        return max(maxValues);
    }

    public static Double max(List<Double> scores){
        Collections.sort(scores);
        return scores.get(scores.size()-1);
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }
}