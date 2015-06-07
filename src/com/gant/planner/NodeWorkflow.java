package com.gant.planner;

import com.analyze.Task;

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

    public void processTic(int ticNumber){
        getTic(ticNumber, true).process();
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
            setTic(tic, ticNumber);
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

    @Override
    public String toString() {
        return nodeId +" " + tics;
    }
}
