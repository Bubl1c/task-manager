package com.gant.model;

import com.analyze.Task;
import com.gant.planner.Tic;
import com.gant.planner.Transfer;

/**
 * Created by Andrii on 01.06.2015.
 */
public class NodeState {
    private Tic tic;

    public NodeState(Tic tic) {
        this.tic = tic;
    }

    private int getNodeId(){
        return tic == null ? -1 : tic.getNodeWorkflow().getNodeId();
    }

    public Task getTask() {
        return tic == null ? null : tic.getTask();
    }
}
