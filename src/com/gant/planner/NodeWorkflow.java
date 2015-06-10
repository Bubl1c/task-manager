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
    private int logLinksNumber;
    private ArrayList<Tic> tics;

    public NodeWorkflow(Integer nodeId, int logLinksNumber) {
        this.nodeId = nodeId;
        this.logLinksNumber = logLinksNumber;
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
                int counter = 0;
                for(PhisLink phisLink : currentTic.getPhisLinks()){
                    g.drawString(phisLink.toString(), currentLeftX + sm, cellTopY + recordHeight*counter);
                    counter++;
                }
                //g.drawString(currentTic.toPhisLinksString(), currentLeftX + md, cellTopY);

                g.setColor(Color.blue);

                String taskString = currentTic.getTask() == null ? "" : currentTic.getTask().toString();
                g.drawString(taskString, currentLeftX + sm, cellTopY + lineHeight - recordHeight);

                g.setColor(Color.black);
            }


            g.drawLine(currentLeftX, currentTopY, currentLeftX, currentBottomY);
        }

        return index++;
    }

    public int getLogLinksNumber() {
        return logLinksNumber;
    }

    public void setLogLinksNumber(int logLinksNumber) {
        this.logLinksNumber = logLinksNumber;
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
