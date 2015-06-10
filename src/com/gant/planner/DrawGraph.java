package com.gant.planner;

/**
 * Created by Andrii on 10.06.2015.
 */
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;

@SuppressWarnings("serial")
public class DrawGraph extends JPanel {
    private static final int MAX_SCORE = 20;
    private static final int PREF_W = 800;
    private static final int PREF_H = 650;
    private static final int BORDER_GAP = 30;
    private static final Color GRAPH_COLOR = Color.green;
    private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
    private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
    private static final int GRAPH_POINT_WIDTH = 12;
    private static final int Y_HATCH_CNT = 20;

    public static final int SM = 10;
    public static final int MD = 15;

    public static Random random = new Random();

    private List<Integer> scores;

    public DrawGraph(List<Integer> scores) {
        this.scores = scores;
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D)g;
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (scores.size() - 1);
//        double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (MAX_SCORE - 1);
//
//        List<Point> graphPoints = new ArrayList<Point>();
//        for (int i = 0; i < scores.size(); i++) {
//            int x1 = (int) (i * xScale + BORDER_GAP);
//            int y1 = (int) ((MAX_SCORE - scores.get(i)) * yScale + BORDER_GAP);
//            graphPoints.add(new Point(x1, y1));
//        }
//
//        // create x and y axes
//        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
//        g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);
//
//        // create hatch marks for y axis.
//        for (int i = 0; i < Y_HATCH_CNT; i++) {
//            int x0 = BORDER_GAP;
//            int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
//            int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
//            int y1 = y0;
//            g2.drawLine(x0, y0, x1, y1);
//        }
//
//        // and for x axis
//        for (int i = 0; i < scores.size() - 1; i++) {
//            int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (scores.size() - 1) + BORDER_GAP;
//            int x1 = x0;
//            int y0 = getHeight() - BORDER_GAP;
//            int y1 = y0 - GRAPH_POINT_WIDTH;
//            g2.drawLine(x0, y0, x1, y1);
//        }
//
//        Stroke oldStroke = g2.getStroke();
//        g2.setColor(GRAPH_COLOR);
//        g2.setStroke(GRAPH_STROKE);
//        for (int i = 0; i < graphPoints.size() - 1; i++) {
//            int x1 = graphPoints.get(i).x;
//            int y1 = graphPoints.get(i).y;
//            int x2 = graphPoints.get(i + 1).x;
//            int y2 = graphPoints.get(i + 1).y;
//            g2.drawLine(x1, y1, x2, y2);
//        }
//
//        g2.setStroke(oldStroke);
//        g2.setColor(GRAPH_POINT_COLOR);
//        for (int i = 0; i < graphPoints.size(); i++) {
//            int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
//            int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
//            int ovalW = GRAPH_POINT_WIDTH;
//            int ovalH = GRAPH_POINT_WIDTH;
//            g2.fillOval(x, y, ovalW, ovalH);
//        }
//    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (scores.size() - 1);
        double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (MAX_SCORE - 1);

        System.out.println(scores);

        drawAxes(g2);
        drawLine(g2, getData(), xScale, yScale, "line 1", getColor(1), 1);
        drawLine(g2, getData(), xScale, yScale, "line 2", getColor(2), 2);
        drawLine(g2, getData(), xScale, yScale, "line 3", getColor(3), 3);
        drawLine(g2, getData(), xScale, yScale, "line 4", getColor(4), 4);
        drawLine(g2, getData(), xScale, yScale, "line 5", getColor(5), 5);
        drawLine(g2, getData(), xScale, yScale, "line 6", getColor(6), 6);
    }

    private List<Double> getData(){
        int maxDataPoints = 20;
        int maxScore = 20;
        Random random = new Random();
        List<Double> dscores = new ArrayList<>();

        for (int i = 0; i < maxDataPoints ; i++) {
            dscores.add(random.nextDouble()*random.nextInt(maxScore));
        }
        return dscores;
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
        return colors.get(index*77%6);
    }

    private void drawLine(Graphics2D g2, List<Double> scores, double xScale, double yScale, String label, Color color, int index){

        List<Point2D> graphPoints = new ArrayList<Point2D>();
        for (int i = 0; i < scores.size(); i++) {
            double x1 = (i * xScale + BORDER_GAP);
            double y1 = ((MAX_SCORE - scores.get(i)) * yScale /*+ BORDER_GAP*/);
            graphPoints.add(new Point2D.Double(x1, y1));
        }

        Stroke oldStroke = g2.getStroke();
        Color oldColor = g2.getColor();

        Stroke lineStroke = new BasicStroke(1f);

        g2.setColor(color);
        g2.setStroke(lineStroke);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            double x1 = graphPoints.get(i).getX();
            double y1 = graphPoints.get(i).getY();
            double x2 = graphPoints.get(i + 1).getX();
            double y2 = graphPoints.get(i + 1).getY();
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        g2.setFont(new Font("TimesRoman", Font.BOLD, 14));
        g2.drawString(label, BORDER_GAP + SM, index*MD + BORDER_GAP);

        g2.setColor(oldColor);
        g2.setStroke(oldStroke);
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
            int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
            int y1 = y0;
            g2.drawLine(x0, y0, x1, y1);
            g2.drawString(i+1+"", x0 - MD, y0);
        }

        // and for x axis
        for (int i = 0; i < scores.size() - 1; i++) {
            int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (scores.size() - 1) + BORDER_GAP;
            int x1 = x0;
            int y0 = getHeight() - BORDER_GAP;
            int y1 = /*y0 - GRAPH_POINT_WIDTH*/ BORDER_GAP;
            g2.drawLine(x0, y0, x1, y1);
            //g2.drawString(i+1+"", x0, y0 + MD);
        }

        g2.setColor(oldColor);
        g2.setStroke(oldStroke);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    private static void createAndShowGui() {
        List<Integer> scores = new ArrayList<Integer>();

        int maxDataPoints = 20;
        int maxScore = 20;
        for (int i = 0; i < maxDataPoints ; i++) {
            scores.add(random.nextInt(maxScore));
        }
        DrawGraph mainPanel = new DrawGraph(scores);

        JFrame frame = new JFrame("DrawGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }
}
