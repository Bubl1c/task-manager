package com.gant.planner;

import com.analyze.Task;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii on 01.06.2015.
 */
public class NodeWorkflow implements Serializable{
    private Integer nodeId;
    private ArrayList<Tic> tics;

    public NodeWorkflow(Integer nodeId) {
        this.nodeId = nodeId;
        tics = new ArrayList<>();
    }

    public List<Plannable> processTic(int ticNumber){
        List<Plannable> processed = new ArrayList<>();
        processed.addAll(getTic(ticNumber, true).process());
        return processed;
    }

    public boolean isFree(Plannable work, int startTicNumber){
        boolean free = true;
        for(int i = startTicNumber; i < startTicNumber + work.getWeight(); i++){
            if(!getTic(i, true).isFree(work)){
                free = false;
            }
        }
        return free;
    }

    public boolean assignWork(Plannable work, int startTicNumber){
        if(!isFree(work, startTicNumber)){
            return false;
        }
        int workWeight = work.getWeight();
        int ticNumber = startTicNumber;
        while(workWeight > 0){
            Tic tic = getTic(ticNumber, true);
            tic.setWork(work);
            ticNumber++;
            workWeight--;
        }
        return true;
    }

    private void addTic(Tic tic){
        tic.setSequenceNumber(tics.size());
        tics.add(tic);
    }

    private void setTic(Tic tic, int position){
        while(tics.size() <= position){
            addTic(new Tic(this));
        }
        tic.setSequenceNumber(position);
        tics.set(position, tic);
    }

    public Tic getTic(int ticNumber){
        Tic tic = null;
        try {
            tic = this.tics.get(ticNumber);
        } catch (IndexOutOfBoundsException e){}
        return tic;
    }

    public Tic getTic(int ticNumber, boolean createNew){
        if(createNew){
            Tic tic = getTic(ticNumber);
            if(tic == null) {
                tic = new Tic(this);
                setTic(tic, ticNumber);
            }
            return tic;
        }
        return getTic(ticNumber);
    }

    public List<Transfer> findTransfersFor(Task task){
        List<Transfer> transfers = new ArrayList<>();
        for(Tic tic : tics){
            transfers.addAll(tic.findTransfersFor(task));
        }
        return transfers;
    }

    public List<Tic> getTics() {
        return tics;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void trim(){
        int lastNotEmptyTic = 0;
        for(Tic t : tics){
            if(t.isAnyTransfer() || t.getTask() != null){
                lastNotEmptyTic = t.getSequenceNumber();
            }
        }

        while(lastNotEmptyTic+1 < tics.size()){
            tics.remove(lastNotEmptyTic+1);
        }
    }

    public int size(){
        return tics.size();
    }

    public int draw(Graphics g, int index, int width) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(1.0f));
        g.setFont(new Font("Arial", 1, 12));
        g.setColor(Color.BLACK);

        FontMetrics fm = g.getFontMetrics();
        GlyphVector gv = g.getFont().createGlyphVector(fm.getFontRenderContext(), " Processor");
//        g.drawString(" Processor", (int) ((com.gant.palivo.Config.HEADWIDTH/3 + 2* com.gant.palivo.Config.HEADWIDTH/3)/2 - gv.getVisualBounds().getWidth()/2), (int) (index + com.gant.palivo.Config.HEIGHT/2 + gv.getVisualBounds().getHeight()/2));
//        g.drawLine(com.gant.palivo.Config.HEADWIDTH/3, index + com.gant.palivo.Config.HEIGHT, com.gant.palivo.Config.HEADWIDTH + width* com.gant.palivo.Config.STEPWIDTH, index + com.gant.palivo.Config.HEIGHT);
//        g.drawLine(com.gant.palivo.Config.HEADWIDTH, index, com.gant.palivo.Config.HEADWIDTH, index + com.gant.palivo.Config.HEIGHT);

        int lineHeight = VC.lineHeight;
        int nodeNamesDataWidth = VC.nodeNamesDataWidth;
        int ticWidth = VC.ticWidth;
        int sm = VC.space1;
        int md = VC.space2;
        int topMargin = VC.topMargin;

        int diagramWidth = nodeNamesDataWidth + ticWidth * width;

        int currentTopY = index * lineHeight + topMargin;
        int currentBottomY = lineHeight + index * lineHeight + topMargin;

        g.drawString("Node " + getNodeId() + ":", md, currentBottomY-sm);
        g.drawLine(md, currentBottomY, diagramWidth, currentBottomY);

        int recordHeight = VC.recordHeight;

        for (int i = 0; i < width; i++) {
            int currentLeftX = sm + nodeNamesDataWidth + i*ticWidth;
            int currentRightX = sm + nodeNamesDataWidth + i*ticWidth + ticWidth;
            int cellTopY = currentTopY+md+sm;

            Tic currentTic = getTic(i);

            if(currentTic != null){
                g.drawString(currentTic.toPhisLinksString(), currentLeftX + md, cellTopY);

                g.setColor(Color.blue);
                String taskString = currentTic.getTask() == null ? "" : currentTic.getTask().toString();
                g.drawString(taskString, currentLeftX + md, cellTopY + recordHeight*2);

                g.setColor(Color.black);

            }


            g.drawLine(currentLeftX, currentTopY, currentLeftX, currentBottomY);
        }

//        for(Data data : proc){
//            int begin = data.getBeginTime()* com.gant.palivo.Config.STEPWIDTH + com.gant.palivo.Config.HEADWIDTH;
//            int end = (data.getEndTime() + 1)* com.gant.palivo.Config.STEPWIDTH + com.gant.palivo.Config.HEADWIDTH;
//
//            g.setColor(new Color(180, 197, 255));
//            g.fillRect(begin + 2, index + 2, end - begin - 4, com.gant.palivo.Config.HEIGHT - 4);
//            g.setColor(Color.BLACK);
//            g.drawLine(begin, index, begin, index + com.gant.palivo.Config.HEIGHT);
//            g.drawLine(end, index, end, index + com.gant.palivo.Config.HEIGHT);
//
//            fm = g.getFontMetrics ();
//            gv = g.getFont ().createGlyphVector(fm.getFontRenderContext (), data.getName() );
//            g.drawString(data.getName(), (int) ((begin + end)/2 - gv.getVisualBounds().getWidth()/2), (int) (index + com.gant.palivo.Config.HEIGHT/2 + gv.getVisualBounds().getHeight()/2));
//        }
//
//        int tmpindex = index + com.gant.palivo.Config.HEIGHT;
//        for(int i=0; i<channel.length; i++){
//            int begin = tmpindex;
//            int end = channel[i].draw(g, tmpindex, width);
//            tmpindex = end;
//
//            fm = g.getFontMetrics();
//            gv = g.getFont().createGlyphVector(fm.getFontRenderContext(), "Chanal " + (i + 1));
//            g.drawString("Chanal " + (i + 1), (int) ((com.gant.palivo.Config.HEADWIDTH/3 + 2* com.gant.palivo.Config.HEADWIDTH/3)/2 - gv.getVisualBounds().getWidth()/2), (int) ((begin + end)/2 + gv.getVisualBounds().getHeight()/2));
//            g.drawLine(com.gant.palivo.Config.HEADWIDTH/3, end, com.gant.palivo.Config.HEADWIDTH + width* com.gant.palivo.Config.STEPWIDTH, end);
//        }
//
//        fm = g.getFontMetrics();
//        gv = g.getFont().createGlyphVector(fm.getFontRenderContext(), "P" + Integer.toString(proc_number));
//        g.drawString("P"+Integer.toString(proc_number), (int) (com.gant.palivo.Config.HEADWIDTH/6 - gv.getVisualBounds().getWidth()/2), (int) ((index + tmpindex)/2 + gv.getVisualBounds().getHeight()/2));
//        g.drawLine(com.gant.palivo.Config.HEADWIDTH/3, index, com.gant.palivo.Config.HEADWIDTH/3, tmpindex);
//        g.drawLine(0, tmpindex, com.gant.palivo.Config.HEADWIDTH + width* com.gant.palivo.Config.STEPWIDTH, tmpindex);

//        for(int i=0; i<width; i++){
//            float[] dashPattern = {2.0f, 5.0f};
//            g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0));
//            g.drawLine(com.gant.palivo.Config.HEADWIDTH + (i + 1)* com.gant.palivo.Config.STEPWIDTH, index, com.gant.palivo.Config.HEADWIDTH + (i + 1)* com.gant.palivo.Config.STEPWIDTH, index + com.gant.palivo.Config.HEIGHT);
//        }

        return index++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeWorkflow)) return false;

        NodeWorkflow workflow = (NodeWorkflow) o;

        if (!nodeId.equals(workflow.nodeId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return nodeId.hashCode();
    }

    @Override
    public String toString() {
        return nodeId +" " + tics;
    }
}
