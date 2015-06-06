package com.gant.planner;

import com.analyze.Task;
import com.gant.Config;
import com.gant.model.NodeState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii on 01.06.2015.
 */
public class Tic {
    private NodeWorkflow nodeWorkflow;
    private int sequenceNumber = 0;

    private Task task;
    private List<PhisLink> phisLinks;

    public Tic(NodeWorkflow nodeWorkflow) {
        this.nodeWorkflow = nodeWorkflow;
        initPhisLinks();
    }

    public boolean isFree(){
        return task == null && hasFreePhisLinks();
    }

    public boolean isFree(Class<? extends Plannable> cl){
        if(Config.isIO){
            if(cl.equals(Task.class)){
                return task == null;
            } else if(cl.equals(Transfer.class)){
                return hasFreePhisLinks();
            }
        }
        return isFree();
    }

    private boolean hasFreePhisLinks(){
        return getFirstFreePhisLinkIndex() != -1;
    }

    private PhisLink getFirstFreePhisLink(){
        return phisLinks.get(getFirstFreePhisLinkIndex());
    }

    private int getFirstFreePhisLinkIndex(){
        for(int i = 0; i < phisLinks.size(); i++){
            if(phisLinks.get(i).isFree()){
                return i;
            }
        }
        return -1;
    }

    public NodeState getState(){
        return new NodeState(this);
    }

    public void setWork(Plannable work){
        if(work instanceof Task){
            setTask((Task) work);
        } else if(work instanceof Transfer){
            if(hasFreePhisLinks()){
                getFirstFreePhisLink().setTransfer((Transfer) work);
            }
        }
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

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    private void initPhisLinks(){
        phisLinks = new ArrayList<>(Config.physLinksNumber);
        for(int i = 0; i < Config.physLinksNumber; i++){
            phisLinks.add(new PhisLink());
        }
    }

    @Override
    public String toString() {
        return "" + sequenceNumber +
                ":" + (task == null ? "_" : task) + " " + phisLinks;
    }
}
