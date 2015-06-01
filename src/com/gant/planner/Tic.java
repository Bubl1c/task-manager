package com.gant.planner;

import com.analyze.Task;

/**
 * Created by Andrii on 01.06.2015.
 */
public class Tic {
    private NodeWorkflow nodeWorkflow;

    private Task task;
    private Transfer transfer;

    public Tic(NodeWorkflow nodeWorkflow) {
        this.nodeWorkflow = nodeWorkflow;
    }

    public boolean isFree(){
        return task == null && transfer == null;   //Change for [IO]
    }

    public NodeWorkflow getNodeWorkflow() {
        return nodeWorkflow;
    }

    public void setNodeWorkflow(NodeWorkflow nodeWorkflow) {
        this.nodeWorkflow = nodeWorkflow;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }
}
