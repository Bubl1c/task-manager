package com.gant.planner;

import com.analyze.Task;
import com.gant.planner.Tic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii on 01.06.2015.
 */
public class NodeWorkflow {
    private Integer nodeId;
    private ArrayList<Tic> tics;

    public NodeWorkflow(Integer nodeId) {
        this.nodeId = nodeId;
        tics = new ArrayList<>();
    }

    public void assignWork(Plannable work, int startTicNumber){
        int workWeight = work.getWeight();
        int ticNumber = startTicNumber;
        while(workWeight > 0){
            Tic tic = getTic(ticNumber, true);
            tic.setWork(work);
            setTic(tic, ticNumber);
            ticNumber++;
            workWeight--;
        }
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

    public List<Tic> getTics() {
        return tics;
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

    public Integer getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return nodeId +" " + tics;
    }
}
