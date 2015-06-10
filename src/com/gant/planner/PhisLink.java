package com.gant.planner;

import com.analyze.Task;
import com.gant.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii on 07.06.2015.
 */
public class PhisLink implements Serializable{
    private Transfer transfer;
    private Transfer duplexTransfer;

    public PhisLink() {
    }

    public PhisLink(Transfer transfer) {
        this();
        this.transfer = transfer;
    }

    public PhisLink(Transfer transfer, Transfer duplexTransfer) {
        this(transfer);
        this.duplexTransfer = duplexTransfer;
    }

    public List<Plannable> processTic(){
        List<Plannable> processed = new ArrayList<>();
        if(transfer != null && transfer.processTic()){
            processed.add(transfer);
        }
        if(duplexTransfer != null && duplexTransfer.processTic()){
            processed.add(transfer);
        }
        return processed;
    }

    public boolean isAnyTransfer(){
        return transfer != null;
    }

    public boolean isFree(Transfer transfer){
        if(transfer == null){
            return isFree();
        }
        if(isFree() && isApplicableDuplexTransfer(transfer)){
            return true;
        }
        return false;
    }

    public boolean setTransfer(Transfer transfer) {
        if(isFree()) {
            if (Config.duplex) {
                if (this.transfer == null) {
                    this.transfer = transfer;
                } else if (isApplicableDuplexTransfer(transfer)) {
                    this.duplexTransfer = transfer;
                } else {
                    return false;
                }
            } else {
                this.transfer = transfer;
            }
            return true;
        }
        return false;
    }

    public boolean isFree(){
        if(Config.duplex){
            return duplexTransfer == null;
        }
        return transfer == null && duplexTransfer == null;
    }

    private boolean isApplicableDuplexTransfer(Transfer transfer){
        if(this.transfer == null) {
            return true;
        } else {
            if(transfer.getSourceNodeId() == this.transfer.getTargetNodeId()
                    && transfer.getTargetNodeId() == this.transfer.getSourceNodeId()){
                return true;
            }
        }
        return false;
    }

    public List<Transfer> findTransfersFor(Task task){
        List<Transfer> transfers = new ArrayList<>();
        if(transfer.getTargetTaskId() == task.getId()){
            transfers.add(transfer);
        }
        if(duplexTransfer.getTargetTaskId() == task.getId()){
            transfers.add(duplexTransfer);
        }
        return transfers;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public Transfer getDuplexTransfer() {
        return duplexTransfer;
    }

    @Override
    public String toString() {
        return "" + (transfer == null ? "" : transfer) + (duplexTransfer == null ? "" : " + " + duplexTransfer);
    }
}
