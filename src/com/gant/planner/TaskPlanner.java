package com.gant.planner;

import com.analyze.Task;
import com.gant.model.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Andrii on 01.06.2015.
 */
public class TaskPlanner {
    private TaskModel taskModel;
    private RoutingModel routingModel;


    private Queue<Task> taskQueue;
    private List<Task> waitForParentTasks;
    private List<Task> notProcessedForPreviousTicTasks;
    private Map<Integer, NodeWorkflow> model;
    private int currentTic = 0;

    private final Random r = new Random();

    public TaskPlanner(RoutingModel routingModel, TaskModel taskModel) {
        this.routingModel = routingModel;
        this.taskModel = taskModel;
        initModel();
        buildDefaultQueue();
        int i = 0;
    }

    public void processTic(){
        notProcessedForPreviousTicTasks = taskModel.getNotProcessedTasks();
        processWorkflows();
        taskQueue.addAll(getReadyToProcessTasks());
        currentTic++;
    }

    private void processJustProcessedTasks(){
        List<Task> justProcessedTasks = taskModel.getJustProcessedTasks(notProcessedForPreviousTicTasks);
        for(Task justProcessedTask : justProcessedTasks){
            for(int childTaskId : taskModel.getChildTaskIds(justProcessedTask.getId())){
                if(taskModel.isAnyNotProcessedParent(childTaskId)){
                    //Handle problem when need to assign task after all parents finished and send data
                }
            }
        }
    }

    private void planTransfer(){

    }

    private Route getOptimalRouteFromNodeToNode(int sourceNodeId, int targetNodeId, int startTicNumber){
        Node sourceNode = routingModel.get(sourceNodeId);
        TreeMap<Integer, Route> routeWeights = new TreeMap<>();
        for(Route route : sourceNode.getRoutes()){
            if(route.getTargetId() == targetNodeId){
                routeWeights.put(calculateRouteWeightDependingOnCurrentModelState(route, startTicNumber), route);
            }
        }

        Map.Entry<Integer, Route> topRouteWeight = routeWeights.lastEntry();
        if(topRouteWeight.getKey() != -1){
            return topRouteWeight.getValue();
        }
        return null;
    }

    private int calculateRouteWeightDependingOnCurrentModelState(Route route, int startTicNumber){
        int routeWeight = 0;
        for(Link link : route.getLinks()){
            if(isTransferPossible(link.getSourceId(), link.getTargetId(), link.getWeight(), startTicNumber)){
                routeWeight += link.getWeight();
            }
            return -1;
        }
        return routeWeight;
    }

    private boolean isTransferPossible(int sourceNodeId, int targetNodeId, int transferWeight, int startTicNumber){
        Transfer transfer = new Transfer(sourceNodeId, targetNodeId, 1, 2, transferWeight, Transfer.Type.SEND);
        return getWorkflow(sourceNodeId).isFree(transfer, startTicNumber) &&
                getWorkflow(targetNodeId).isFree(transfer, startTicNumber) ? true : false;
    }

    private List<Task> getReadyToProcessTasks(){
        List<Task> readyTasks = new ArrayList<>();
        for(Task task : getWaitingTasksWhichAllChildTasksAreProcessed()){
            if(isAllTransfersCompletedFor(task)){
                readyTasks.add(task);
            }
        }
        return readyTasks;
    }

    private List<Task> getWaitingTasksWhichAllChildTasksAreProcessed(){
        List<Task> readyTasks = new ArrayList<>();
        for(Task task : waitForParentTasks){
            boolean isAllParentsAreReady = true;
            for(int parentTaskId : taskModel.getParentTaskIds(task.getId())){
                if(!taskModel.getTask(parentTaskId).isProcessed()){
                    isAllParentsAreReady = false;
                }
            }
            if(isAllParentsAreReady){
                readyTasks.add(task);
            }
        }
        return readyTasks;
    }

    private boolean isAllTransfersCompletedFor(Task task){
        List<Transfer> transfers = new ArrayList<>();
        for(NodeWorkflow workflow : getWorkflows()){
            transfers.addAll(workflow.findTransfersFor(task));
        }

        for(Transfer transfer : transfers){
            if(!transfer.isProcessed()){
                return false;
            }
        }
        return true;
    }

    private void processWorkflows(){
        Iterator<NodeWorkflow> it = getWorkflows().iterator();
        while(it.hasNext()){
            it.next().processTic(currentTic);
        }
    }

    public void assignTasksToNodes(){
        assignTopTasks();
        processTic();

        while(taskModel.isAnyNotProcessedTask()){
            while(taskQueue.size() > 0 && getMostSuitableFreeNodeId(taskQueue.peek()) != -1){
                Task task = taskQueue.poll();
                assignTask(task, getMostSuitableFreeNodeId(task));
            }
        }
//        boolean continuePlanningFlag = true;
//        while(continuePlanningFlag){
//            Task task = taskQueue.poll();
//            if(task != null) {
//                if(getMostSuitableFreeNodeId(task) != -1){
//
//                }
//            }
//            //if()
//            currentTic++;
//        }
//        if(currentTic > 800) {
//            throw new RuntimeException("assignTasksToNodes() Fucking cycle!");
//        }
    }

    private void assignTopTasks(){
        while(taskQueue.size() > 0 && getMostSuitableFreeNodeId(taskQueue.peek()) != -1){
            Task task = taskQueue.poll();
            assignTask(task, getMostSuitableFreeNodeId(task));
        }
    }

    private void assignTask(Task task, int nodeId){
        getWorkflow(nodeId).assignWork(task, currentTic);
        addChildsToWaitForParent(task);
    }

    private Integer getMostSuitableFreeNodeId(Plannable work){
        return getFreeRandomNodeId(work);
    }

    private Integer getFreeRandomNodeId(Plannable work){
        List<Integer> freeNodeIds = getFreeNodeIds(work);
        return freeNodeIds.size() == 0 ? -1 : freeNodeIds.get(r.nextInt(freeNodeIds.size()));
    }

    private List<Integer> getFreeNodeIds(Plannable work){
        List<Integer> freeNodeIds = new ArrayList<>();
        for(Map.Entry<Integer, Tic> entry : getTics(currentTic).entrySet()){
            if(entry.getValue().isFree(work))
                freeNodeIds.add(entry.getKey());
        }
        return freeNodeIds;
    }

    private Map<Integer, Tic> getTics(int ticNumber){
        Map<Integer, Tic> tics = new HashMap<>();
        for(NodeWorkflow workflow : getWorkflows()){
            tics.put(workflow.getNodeId(), workflow.getTic(ticNumber, true));
        }
        return tics;
    }

    private void addChildsToWaitForParent(Task task){
        List<Integer> childs = taskModel.getChildTaskIds(task.getId());
        for(int childId : childs){
            Task childTask = taskModel.getTask(childId);
            addToWaitForParent(childTask);
        }
    }

    private void addToWaitForParent(Task task){
        if(!waitForParentTasks.contains(task)){
            waitForParentTasks.add(task);
        }
    }

    private List<NodeWorkflow> getWorkflows(){
        return model.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    private NodeWorkflow getWorkflow(int nodeId){
        return model.get(nodeId);
    }

    private void initModel(){
        model = new HashMap<>();
        for(Integer nodeId : routingModel.getNodeIds()){
            model.put(nodeId, new NodeWorkflow(nodeId));
        }
        taskQueue = new PriorityQueue<>(10, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if(o1.getPriority() < o2.getPriority()){
                    return 1;
                } else if(o1.getPriority() > o2.getPriority()){
                    return -1;
                }
                return 0;
            }
        });
        waitForParentTasks = new ArrayList<>();
    }

    private void buildDefaultQueue(){
        List<Integer> topTaskIds = taskModel.getTopTaskIds();
        for(Task task : taskModel.getDefaultTaskQueue()){
            if(topTaskIds.contains(task.getId())){
                taskQueue.add(task);
            }
        }
    }

    public String getModelOutput() {
        StringBuilder sb = new StringBuilder();
        sb.append("Planner Model: \n");
        for(Map.Entry<Integer, NodeWorkflow> entry : model.entrySet()){
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void serializeModel(Map<Integer, NodeWorkflow> map){
        try
        {
            FileOutputStream fos = new FileOutputStream("hashmap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static Map<Integer, NodeWorkflow> deserializeModel() {
        HashMap<Integer, NodeWorkflow> map = null;
        try {
            FileInputStream fis = new FileInputStream("hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return null;
        }
        return map;
    }
}
