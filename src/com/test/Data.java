package com.test;

import com.analyze.Task;
import com.gant.planner.NodeWorkflow;
import com.gant.planner.PhisLink;
import com.gant.planner.Tic;
import com.gant.planner.Transfer;

/**
 * Created by Andrii on 07.06.2015.
 */
public class Data {

    public PhisLinkData pLink;
    public TransferData transfer;
    public TicData tic;
    public NodeWorkflowData workflow;
    public TaskData task;

    public Data() {
        transfer = new TransferData();
        task = new TaskData();
        workflow = new NodeWorkflowData();
        pLink = new PhisLinkData();
        tic = new TicData();
    }
    
    public class TicData {
        public Tic empty = new Tic(workflow.for1node);
        public Tic withTask;
        public Tic withTaskAndSend12;
        public Tic withSend12;

        public TicData() {
            withTask = new Tic(workflow.for1node);
            withTask.setWork(task.w1);

            withTaskAndSend12 = new Tic(workflow.for1node);
            withTaskAndSend12.setWork(task.w1);
            withTaskAndSend12.setWork(transfer.send12);

            withSend12 = new Tic(workflow.for1node);
            withSend12.setWork(transfer.send12);
        }
    }

    public class TaskData {
        public Task w1 = new Task(1,1);
        public Task w2 = new Task(1,2);
    }

    public class PhisLinkData {
        public PhisLink phisLink = new PhisLink();
        public PhisLink phisLinkSend12Receive21 = new PhisLink(transfer.send12, transfer.receive21);
        public PhisLink phisLinkSend12Receive12 = new PhisLink(transfer.send12, transfer.receive12);
        public PhisLink phisLinkSend21 = new PhisLink(transfer.send21);
    }

    public class NodeWorkflowData {
        public NodeWorkflow for1node = new NodeWorkflow(1, 1);
        public NodeWorkflow taskAtStart = new NodeWorkflow(1, 1);
        public NodeWorkflow taskAt7thTic = new NodeWorkflow(1, 1);
        public NodeWorkflow send12At7thTic = new NodeWorkflow(1, 1);
        public NodeWorkflow taskAt7thSend12At9thTic = new NodeWorkflow(1, 1);

        public NodeWorkflowData() {
            taskAtStart.assignWork(task.w1,0);

            taskAt7thTic.assignWork(task.w1, 7);

            send12At7thTic.assignWork(transfer.send12, 7);

            taskAt7thSend12At9thTic.assignWork(task.w1, 7);
            taskAt7thSend12At9thTic.assignWork(transfer.send12, 9);
        }
    }

    public class TransferData {
        public Transfer send12 = new Transfer(1,2,9,10,1, Transfer.Type.SEND);
        public Transfer send21 = new Transfer(2,1,9,10,1, Transfer.Type.SEND);
        public Transfer send13 = new Transfer(1,3,9,10,1, Transfer.Type.SEND);
        public Transfer receive12 = new Transfer(send12, true);
        public Transfer receive21 = new Transfer(send21, true);
        public Transfer receive31 = new Transfer(send13, true);

        public Transfer send12w3 = new Transfer(1,2,9,10,3, Transfer.Type.SEND);
        public Transfer send21w3 = new Transfer(2,1,9,10,1, Transfer.Type.SEND);
    }
}
