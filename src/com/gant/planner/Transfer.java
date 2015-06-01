package com.gant.planner;

/**
 * Created by Andrii on 01.06.2015.
 */
public class Transfer {
    private int sourceNodeId;
    private int targetNodeId;

    private int sourceTaskId;
    private int targetTaskId;

    public Transfer(int sourceNodeId, int targetNodeId, int sourceTaskId, int targetTaskId) {
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.sourceTaskId = sourceTaskId;
        this.targetTaskId = targetTaskId;
    }

    public int getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(int sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public int getTargetNodeId() {
        return targetNodeId;
    }

    public void setTargetNodeId(int targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public int getSourceTaskId() {
        return sourceTaskId;
    }

    public void setSourceTaskId(int sourceTaskId) {
        this.sourceTaskId = sourceTaskId;
    }

    public int getTargetTaskId() {
        return targetTaskId;
    }

    public void setTargetTaskId(int targetTaskId) {
        this.targetTaskId = targetTaskId;
    }
}
