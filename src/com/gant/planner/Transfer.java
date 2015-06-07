package com.gant.planner;

import java.io.Serializable;

/**
 * Created by Andrii on 01.06.2015.
 */
public class Transfer implements Plannable, Serializable{
    private Type type;
    private int linkWeight;
    private int progress;
    private int sourceNodeId;
    private int targetNodeId;

    private int sourceTaskId;
    private int targetTaskId;

    public static enum Type {
        SEND,
        RECEIVE
    }

    public Transfer(int sourceNodeId, int targetNodeId, int sourceTaskId, int targetTaskId, int linkWeight, Type type) {
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.sourceTaskId = sourceTaskId;
        this.targetTaskId = targetTaskId;
        this.linkWeight = linkWeight;
        this.type = type;
    }

    public Transfer(Transfer transfer, boolean revers) {
        this.sourceNodeId = transfer.sourceNodeId;
        this.targetNodeId = transfer.targetNodeId;
        this.sourceTaskId = transfer.sourceTaskId;
        this.targetTaskId = transfer.targetTaskId;
        this.linkWeight = transfer.linkWeight;
        if(revers){
            if(transfer.getType().equals(Type.RECEIVE)){
                this.type = Type.SEND;
            } else {
                this.type = Type.RECEIVE;
            }
        } else {
            this.type = transfer.getType();
        }
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Integer getWeight() {
        return linkWeight;
    }

    @Override
    public boolean processTic() {
        progress++;
        return isProcessed() ? true : false;
    }

    @Override
    public boolean isProcessed() {
        return linkWeight == progress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transfer)) return false;

        Transfer transfer = (Transfer) o;

        if (linkWeight != transfer.linkWeight) return false;
        if (sourceNodeId != transfer.sourceNodeId) return false;
        if (sourceTaskId != transfer.sourceTaskId) return false;
        if (targetNodeId != transfer.targetNodeId) return false;
        if (targetTaskId != transfer.targetTaskId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = linkWeight;
        result = 31 * result + sourceNodeId;
        result = 31 * result + targetNodeId;
        result = 31 * result + sourceTaskId;
        result = 31 * result + targetTaskId;
        return result;
    }

    @Override
    public String toString() {
        return "R{" + sourceNodeId + "-" + targetNodeId +
                ", " + sourceTaskId + "-" + targetTaskId + '}';
    }
}
