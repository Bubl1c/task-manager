package com.gant.planner;

import com.analyze.Task;
import com.gant.Config;
import com.gant.model.NodeState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Andrii on 01.06.2015.
 */
public class Tic implements Serializable{
    private NodeWorkflow nodeWorkflow;
    private int sequenceNumber = 0;

    private Task task;
    private List<PhisLink> phisLinks;
    private List<Plannable> processedWorks;

    public Tic(NodeWorkflow nodeWorkflow) {
        this.nodeWorkflow = nodeWorkflow;
        processedWorks = new ArrayList<>();
        initPhisLinks();
    }

    public List<Plannable> process(){
        List<Plannable> processed = new ArrayList<>();
        if(task != null && task.processTic()){
            task.setProcessedBy(getNodeWorkflow().getNodeId());
            task.setProcessedTicNumber(sequenceNumber);
            processed.add(task);
        }
        Iterator<PhisLink> it = phisLinks.iterator();
        while(it.hasNext()){
            processed.addAll(it.next().processTic());
        }
        this.processedWorks.addAll(processed);
        return processed;
    }

    public boolean isFree(){
        return task == null && hasFreePhisLinks(null);
    }

    public boolean isFree(Plannable work){
        if(work == null){
            return isFree();
        }
        if(work instanceof Task){
            if(Config.isIO){
                if(task == null){
                    return true;
                }
            } else {
                if(task == null && !isAnyTransfer()){
                    return true;
                }
            }
            return false;
        } else if(work instanceof Transfer){
            if(Config.isIO){
                return hasFreePhisLinks((Transfer) work);
            } else {
                if(task == null && hasFreePhisLinks((Transfer) work)){
                    return true;
                }
            }
            return false;
        }
        return isFree();
    }

    private boolean hasFreePhisLinks(Transfer transfer){
        return getFirstFreePhisLinkIndex(transfer) != -1;
    }

    private PhisLink getFirstFreePhisLink(Transfer transfer){
        return phisLinks.get(getFirstFreePhisLinkIndex(transfer));
    }

    public boolean isAnyTransfer(){
        for(int i = 0; i < phisLinks.size(); i++){
            if(phisLinks.get(i).isAnyTransfer()){
                return true;
            }
        }
        return false;
    }

    private int getFirstFreePhisLinkIndex(Transfer transfer){
        for(int i = 0; i < phisLinks.size(); i++){
            if(phisLinks.get(i).isFree(transfer)){
                return i;
            }
        }
        return -1;
    }

    public List<Transfer> findTransfersFor(Task task){
        List<Transfer> transfers = new ArrayList<>();
        for(PhisLink pLink : phisLinks){
            transfers.addAll(pLink.findTransfersFor(task));
        }
        return transfers;
    }

    public boolean setWork(Plannable work){
        if(isFree(work)){
            if(work instanceof Task){
                setTask((Task) work);
                return true;
            } else if(work instanceof Transfer){
                getFirstFreePhisLink((Transfer) work).setTransfer((Transfer) work);
                return true;
            }
        }
        return false;
    }

    public NodeWorkflow getNodeWorkflow() {
        return nodeWorkflow;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public NodeState getState(){
        return new NodeState(this);
    }

    public List<Plannable> getProcessedWorks() {
        return processedWorks;
    }

    public List<PhisLink> getPhisLinks() {
        return phisLinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tic)) return false;

        Tic tic = (Tic) o;

        if (sequenceNumber != tic.sequenceNumber) return false;
        if (!nodeWorkflow.equals(tic.nodeWorkflow)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeWorkflow.hashCode();
        result = 31 * result + sequenceNumber;
        return result;
    }

    private void initPhisLinks(){
        phisLinks = new ArrayList<>();
        for(int i = 0; i < (nodeWorkflow.getLogLinksNumber() < Config.physLinksNumber
                ? nodeWorkflow.getLogLinksNumber() : Config.physLinksNumber); i++){
            phisLinks.add(new PhisLink());
        }
    }

    public String toPhisLinksString() {
        String result = "";
        for(PhisLink phisLink : phisLinks){
            if(phisLink.isAnyTransfer()){
                result += phisLink.toString() + "\n";
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "" + sequenceNumber +
                ":" + (task == null ? "_" : task) + " " + phisLinks;
    }
}
