package com.gant.planner;

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

    public void addTic(Tic tic){
        tics.add(tic);
    }

    public void addTic(Tic tic, int position){
        for(int in = tics.size(); in <= position; in++){
            tics.add(new Tic(this));
        }
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

    public boolean isFree(int ticNumber){
        Tic tic = getTic(ticNumber);
        return tic == null ? true : tic.isFree();
    }
}
