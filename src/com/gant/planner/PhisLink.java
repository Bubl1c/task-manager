package com.gant.planner;

import com.gant.Config;

/**
 * Created by Andrii on 07.06.2015.
 */
public class PhisLink {
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

    public boolean isFree(){
        if(Config.duplex){
            return duplexTransfer == null;
        }
        return transfer == null && duplexTransfer == null;
    }

    public boolean isFree(Transfer transfer){
        if(Config.duplex){
            if(isFree() && isApplicableDuplexTransfer(transfer)){
                return true;
            }
        }
        return false;
    }

    private boolean isApplicableDuplexTransfer(Transfer transfer){
        if(this.transfer != null && transfer != null){
            if(transfer.getSourceNodeId() == this.transfer.getTargetNodeId()
                    && transfer.getTargetNodeId() == this.transfer.getSourceNodeId()){
                return true;
            }
        }
        return false;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public Transfer getDuplexTransfer() {
        return duplexTransfer;
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

    @Override
    public String toString() {
        return "" + (transfer == null ? "" : transfer + " + ") + (duplexTransfer == null ? "" : duplexTransfer);
    }
}
