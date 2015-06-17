package com.gant.planner;

import com.analyze.Task;
import com.gant.Config;
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

    private Map<Integer, Integer> nodesConnectivityOrder;

    private Queue<Task> taskQueue;
    private List<Task> waitForParentTasks;

    private TreeMap<Integer, List<Transfer>> processedTransfers;
    private TreeMap<Integer, List<Task>> processedTasks;

    private Map<Integer, NodeWorkflow> model;
    private int currentTic = 0;

    private final Random r = new Random();

    public TaskPlanner(RoutingModel routingModel, TaskModel taskModel) {

        this.routingModel = routingModel;
        this.taskModel = taskModel;
        initModel();
        buildDefaultQueue();
        buildNodesConnectivityOrder();
        assignTasksToNodes();
    }

    public void processTic(){
        processWorkflows();
        plan(getReadyToProcessChildsFromJustProcessedTasks());
        currentTic++;
    }

    private void processWorkflows(){
        List<Plannable> processedWorks = new ArrayList<>();
        Iterator<NodeWorkflow> it = getWorkflows().iterator();
        while(it.hasNext()){
            NodeWorkflow currentWorkflow = it.next();
            processedWorks.addAll(currentWorkflow.processTic(currentTic));
        }
        processedTasks.put(currentTic, getTasksFromWorks(processedWorks));
        processedTransfers.put(currentTic, getTransfersFromWorks(processedWorks));
    }

    public void assignTasksToNodes(){
        assignTopTasks();
        processTic();

        while(taskModel.isAnyNotProcessedTask()){
            while(taskQueue.size() > 0 && getMostSuitableFreeNodeId(taskQueue.peek()) != -1){
                Task task = taskQueue.poll();
                assignTask(task, getMostSuitableFreeNodeId(task), currentTic);
            }
            processTic();
        }
        trimModel();
    }

    private Queue<Task> getReadyToProcessChildsFromJustProcessedTasks(){
        Queue<Task> childsOfJustProcessed = createTasksQueue();
        if(processedTasks.lastEntry() != null){
            int lastProcessedTicNumber = processedTasks.lastEntry().getKey();
            List<Task> lastProcessedTasks = processedTasks.lastEntry().getValue();
            for(Task justProcessedTask : lastProcessedTasks){
                for(int childTaskId : taskModel.getChildTaskIds(justProcessedTask.getId())){
                    if(!taskModel.isAnyNotProcessedParent(childTaskId)){
                        if(!childsOfJustProcessed.contains(taskModel.getTask(childTaskId))){
                            childsOfJustProcessed.add(taskModel.getTask(childTaskId));
                        }
                    }
                }
            }
        }
        return childsOfJustProcessed;
    }

    private void plan(Queue<Task> tasks){
        while(tasks.size() > 0){
            int currentTaskId = tasks.poll().getId();
            int currentTicNumber = currentTic + 1;
            int mostSuitableNodeId = getMostSuitableNodeIdToTransferTo(currentTaskId, currentTicNumber);
            while (mostSuitableNodeId == -1){
                currentTicNumber++;
                mostSuitableNodeId = getMostSuitableNodeIdToTransferTo(currentTaskId, currentTicNumber);
                if(currentTicNumber - currentTic > 10000){
                    throw new RuntimeException("Too long searching optimal Node for child task!");
                }
            }
            int ticToPlanTask = planTransfersFromParentsToChild(currentTaskId, mostSuitableNodeId);

            currentTicNumber = ticToPlanTask;
            while (!assignTask(taskModel.getTask(currentTaskId), mostSuitableNodeId, currentTicNumber)) {
                currentTicNumber++;
                if(currentTicNumber - ticToPlanTask > 10000){
                    throw new RuntimeException("Too long planning task!");
                }
            }
        }
    }

    private int planTransfersFromParentsToChild(int childTaskId, int chosenNodeId){
        List<Integer> lastTicNumbersOfPlannedTransfers = new ArrayList<>();

        Queue<ObjectWeight> transferWeightsFromParentsToChild = new PriorityQueue<>(ObjectWeight.getComparator());
        for(int parentTaskId : taskModel.getParentTaskIds(childTaskId)){
//            int weight = taskModel.getLinkBetween(parentTaskId, childTaskId).getWeight()
//                    - taskModel.getTask(parentTaskId).getProcessedTicNumber();
            int weight = taskModel.getTask(parentTaskId).getProcessedTicNumber();
            transferWeightsFromParentsToChild.add(new ObjectWeight(weight,
                    taskModel.getTask(parentTaskId)));
        }

        while(transferWeightsFromParentsToChild.size() > 0){
            Task currentParentTask = (Task) transferWeightsFromParentsToChild.poll().getObject();
            int currentLastTicNumber = planTransfer(currentParentTask.getProcessedBy(), chosenNodeId, currentParentTask.getId(), childTaskId,
                    currentParentTask.getProcessedTicNumber()+1);
            lastTicNumbersOfPlannedTransfers.add(currentLastTicNumber);
        }
        Collections.sort(lastTicNumbersOfPlannedTransfers);
        return lastTicNumbersOfPlannedTransfers.get(lastTicNumbersOfPlannedTransfers.size() - 1);
    }

    private int planTransfer(int sourceNodeId, int targetNodeId, int sourceTaskId, int targetTaskId, int startTicNumber){
        Link linkBetweenTasks = taskModel.getLinkBetween(sourceTaskId, targetTaskId);
        if(linkBetweenTasks == null){
            throw new RuntimeException("No link between tasks. while planning transfer!");
        }
        int weightOfTransfering = linkBetweenTasks.getWeight();

        if(sourceNodeId == targetNodeId){
            return startTicNumber + getTimeToFree(taskModel.getTask(targetTaskId), sourceNodeId, startTicNumber);
        }

        Route shortestRoute = getShortestRouteFromNodeToNodeDependingOnCurrentModelState(sourceNodeId, targetNodeId, weightOfTransfering, startTicNumber);
        int nextTicNumber = startTicNumber;
        while (shortestRoute == null){
            shortestRoute = getShortestRouteFromNodeToNodeDependingOnCurrentModelState(sourceNodeId, targetNodeId, weightOfTransfering, nextTicNumber++);
            if(nextTicNumber - currentTic > 10000){
                throw new RuntimeException("Too long searching route to plan transfer!");
            }
        }
        Transfer transfer = new Transfer(sourceNodeId, targetNodeId, sourceTaskId, targetTaskId, weightOfTransfering, Transfer.Type.SEND);
        return planTransfer(shortestRoute, transfer, startTicNumber);
    }

    private int planTransfer(Route route, Transfer transfer, int startTicNumber){
        int currentTicNumber = startTicNumber;
        for(Link link : route.getLinks()){
            while(!isTransferPossible(link.getSourceId(), link.getTargetId(), transfer.getWeight(), currentTicNumber)){
                currentTicNumber++;
                if(currentTicNumber - startTicNumber > 10000){
                    throw new RuntimeException("Too long planning transfer!");
                }
            }
            Transfer currentTransfer = new Transfer(link.getSourceId(), link.getTargetId(), transfer.getSourceTaskId(),
                    transfer.getTargetTaskId(), transfer.getWeight(), Transfer.Type.SEND);
            getWorkflow(link.getSourceId()).assignWork(currentTransfer, currentTicNumber);
            getWorkflow(link.getTargetId()).assignWork(new Transfer(currentTransfer, true), currentTicNumber);
            currentTicNumber += transfer.getWeight();
        }
        return currentTicNumber;
    }

    public int getTimeToProcessTasksOn1CPU(){
        List<Task> tasks = taskModel.getDefaultTaskQueue();
        int time = 0;
        for(Task task : tasks){
            time += task.getWeight();
        }
        return time;
    }
    
    private Route getShortestRouteFromNodeToNodeDependingOnCurrentModelState(int sourceNodeId, int targetNodeId, int transferWeight, int startTicNumber){
        PriorityQueue<ObjectWeight> possibleRoutes = new PriorityQueue<>(ObjectWeight.getComparator());
        for(Route route : routingModel.getRoutesBetween(sourceNodeId, targetNodeId)){
            int routeWeight = calculateRouteWeightDependingOnCurrentModelState(route, transferWeight, startTicNumber);
            if(routeWeight != -1){
                possibleRoutes.add(new ObjectWeight(routeWeight, route));
            }
        }
        return possibleRoutes.peek() != null ? (Route) possibleRoutes.poll().getObject() : null;
    }

    private int calculateRouteWeightDependingOnCurrentModelState(Route route, int transferWeight, int startTicNumber){
        int routeWeight = 0;
        for(Link link : route.getLinks()){
            while(!isTransferPossible(link.getSourceId(), link.getTargetId(), transferWeight, routeWeight+startTicNumber)){
                routeWeight++;
                if(routeWeight > 10000){
                    throw new RuntimeException("Too long calculating route weight!");
                }
            }
            routeWeight += transferWeight;
        }
        return routeWeight;
    }
    
    private int getMostSuitableNodeIdToTransferTo(int taskId, int ticNumber){
        if(Config.assignmentType == Config.AssignmentType.NEIGHBOR_5){
            return getBy5Algorithm(taskId, ticNumber);
        } else if(Config.assignmentType == Config.AssignmentType.NEIGHBOR_7){
            return getBy7Algorithm(taskId, ticNumber);
        }
        return getFreeRandomNodeId(taskModel.getTask(taskId), ticNumber);
    }

    private int getBy5Algorithm(int taskId, int ticNumber){
        Queue<ObjectWeight> shortestRoutesFreeNodes = new PriorityQueue<>(ObjectWeight.getComparator());
        Task task = taskModel.getTask(taskId);
        for(int freeNodeId : getFreeNodeIds(taskModel.getTask(taskId), ticNumber)){
            Queue<ObjectWeight> shortestRoutesParents = new PriorityQueue<>(ObjectWeight.getComparator());
            for(int parentTaskId : taskModel.getParentTaskIds(taskId)){
                int parentTaskProcessedNodeId = taskModel.getTask(parentTaskId).getProcessedBy();
                if(parentTaskProcessedNodeId == freeNodeId){
                    shortestRoutesParents.add(new ObjectWeight(getTimeToFree(task, freeNodeId, ticNumber),
                            new Route(parentTaskProcessedNodeId, parentTaskProcessedNodeId)));
                } else {
                    Route shortestRoute = getShortestRouteFromNodeToNode(parentTaskProcessedNodeId, freeNodeId);
                    if(shortestRoute != null){
                        shortestRoutesParents.add(new ObjectWeight(shortestRoute.size() * taskModel.getLinkBetween(parentTaskId,
                                taskId).getWeight(), shortestRoute));
                    }
                }
            }
            int sumWeight = 0;
            for(ObjectWeight objectWeight : shortestRoutesParents){
                sumWeight += objectWeight.getWeight();
            }
            shortestRoutesFreeNodes.add(new ObjectWeight(sumWeight, freeNodeId));
        }
        return shortestRoutesFreeNodes.peek() != null ? (Integer) shortestRoutesFreeNodes.peek().getObject() : -1;
    }

    private int getBy7Algorithm(int taskId, int ticNumber){
        Queue<ObjectWeight> shortestRoutesNodes = new PriorityQueue<>(ObjectWeight.getComparator());
        Task task = taskModel.getTask(taskId);
        for(int nodeId : routingModel.getNodeIds()){
            Queue<ObjectWeight> shortestRoutesParents = new PriorityQueue<>(ObjectWeight.getComparator());
            for(int parentTaskId : taskModel.getParentTaskIds(taskId)){
                int parentTaskProcessedNodeId = taskModel.getTask(parentTaskId).getProcessedBy();
                if(parentTaskProcessedNodeId == nodeId){
                    shortestRoutesParents.add(new ObjectWeight(getTimeToFree(task, nodeId, ticNumber),
                            new Route(parentTaskProcessedNodeId, parentTaskProcessedNodeId)));
                } else {
                    Link linkBetweenParentAndChildTasks = taskModel.getLinkBetween(parentTaskId, taskId);
                    Route shortestRoute = getShortestRouteFromNodeToNodeDependingOnCurrentModelState(parentTaskProcessedNodeId, nodeId,
                            linkBetweenParentAndChildTasks.getWeight(), ticNumber);
                    if(shortestRoute != null){
                        shortestRoutesParents.add(new ObjectWeight(shortestRoute.size() * linkBetweenParentAndChildTasks.getWeight(), shortestRoute));
                    }
                }
            }
            int sumWeight = 0;
            for(ObjectWeight objectWeight : shortestRoutesParents){
                sumWeight += objectWeight.getWeight();
            }
            shortestRoutesNodes.add(new ObjectWeight(sumWeight, nodeId));
        }
        return shortestRoutesNodes.peek() != null ? (Integer) shortestRoutesNodes.peek().getObject() : -1;
    }
    
    private int getTimeToFree(Plannable work, int nodeId, int startTicNumber){
        NodeWorkflow workflow = getWorkflow(nodeId);
        int currentTicNumber = startTicNumber;
        while(!workflow.isFree(work, currentTicNumber)){
            currentTicNumber++;
            if(currentTicNumber - startTicNumber > 500){
                throw new RuntimeException("Too searching time to free!");
            }
        }
        return currentTicNumber - startTicNumber;
    }

    private Route getShortestRouteFromNodeToNode(int sourceNodeId, int targetNodeId){
        Node sourceNode = routingModel.get(sourceNodeId);
        Queue<ObjectWeight> routeWeights = new PriorityQueue<>(ObjectWeight.getComparator());
        for(Route route : sourceNode.getRoutes()){
            if(route.getTargetId() == targetNodeId){
                routeWeights.add(new ObjectWeight(route.getLinks().size(), route));
            }
        }
        return routeWeights.peek() != null ? (Route) routeWeights.peek().getObject() : null;
    }

    private boolean isTransferPossible(int sourceNodeId, int targetNodeId, int transferWeight, int startTicNumber){
        Transfer transfer = new Transfer(sourceNodeId, targetNodeId, 1, 2, transferWeight, Transfer.Type.SEND);
        return getWorkflow(sourceNodeId).isFree(transfer, startTicNumber)
                && getWorkflow(targetNodeId).isFree(transfer, startTicNumber);
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

    private void assignTopTasks(){
        while(taskQueue.size() > 0 && getMostSuitableFreeNodeId(taskQueue.peek()) != -1){
            Task task = taskQueue.poll();
            assignTask(task, getMostSuitableFreeNodeId(task), currentTic);
        }
    }

    private boolean assignTask(Task task, int nodeId, int ticNumber){
        if(getWorkflow(nodeId).assignWork(task, ticNumber)){
            addChildsToWaitForParent(task);
            return true;
        }
        return false;
    }

    private Integer getMostSuitableFreeNodeId(Plannable work){
        if(Config.assignmentType == Config.AssignmentType.NEIGHBOR_5
                || Config.assignmentType == Config.AssignmentType.NEIGHBOR_7) {
            return getFreeMostConnectiveNodeId(work, currentTic);
        }
        return getFreeRandomNodeId(work, currentTic);
    }

    private Integer getFreeMostConnectiveNodeId(Plannable work, int ticNumber){
        List<Integer> freeNodeIds = getFreeNodeIds(work, ticNumber);
        Collections.sort(freeNodeIds, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return nodesConnectivityOrder.get(o1) - nodesConnectivityOrder.get(o2);
            }
        });
        return freeNodeIds.size() == 0 ? -1 : freeNodeIds.get(0);
    }

    private Integer getFreeRandomNodeId(Plannable work, int ticNumber){
        List<Integer> freeNodeIds = getFreeNodeIds(work, ticNumber);
        return freeNodeIds.size() == 0 ? -1 : freeNodeIds.get(r.nextInt(freeNodeIds.size()));
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

    private List<Task> getTasksFromWorks(List<Plannable> works){
        List<Task> tasks = new ArrayList<>();
        for(Plannable work : works){
            if(work instanceof Task){
                tasks.add((Task) work);
            }
        }
        return tasks;
    }

    private List<Transfer> getTransfersFromWorks(List<Plannable> works){
        List<Transfer> transfers = new ArrayList<>();
        for(Plannable work : works){
            if(work instanceof Transfer){
                transfers.add((Transfer) work);
            }
        }
        return transfers;
    }

    private List<Integer> getFreeNodeIds(Plannable work, int ticNumber){
        List<Integer> freeNodeIds = new ArrayList<>();
        for(NodeWorkflow workflow : getWorkflows()){
            if(workflow.isFree(work, ticNumber))
                freeNodeIds.add(workflow.getNodeId());
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

    private List<NodeWorkflow> getWorkflows(){
        return model.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    private NodeWorkflow getWorkflow(int nodeId){
        return model.get(nodeId);
    }

    private void initModel(){
        model = new HashMap<>();
        for(Integer nodeId : routingModel.getNodeIds()){
            model.put(nodeId, new NodeWorkflow(nodeId, routingModel.get(nodeId).getLinks().size()));
        }
        taskQueue = createTasksQueue();

        nodesConnectivityOrder = new HashMap<>();

        waitForParentTasks = new ArrayList<>();
        processedTasks = new TreeMap<>();
        processedTransfers = new TreeMap<>();
    }

    private Queue<Task> createTasksQueue(){
        return new PriorityQueue<>(10, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if(o1.getPriority() < o2.getPriority()){
                    return 1;
                } else if(o1.getPriority() > o2.getPriority()){
                    return -1;
                }
                return o1.getId() - o2.getId();
            }
        });
    }

    private void buildDefaultQueue(){
        List<Integer> topTaskIds = taskModel.getTopTaskIds();
        for(Task task : taskModel.getDefaultTaskQueue()){
            if(topTaskIds.contains(task.getId())){
                taskQueue.add(task);
            }
        }
    }

    private void buildNodesConnectivityOrder(){
        Set<Integer> nodeIds = routingModel.getNodeIds();
        Queue<Node> nodesQueue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if((o2.getLinks().size() - o1.getLinks().size()) == 0){
                    return o1.getId() - o2.getId();
                }
                return o2.getLinks().size() - o1.getLinks().size();
            }
        });
        for(int nodeId : nodeIds){
            nodesQueue.add(routingModel.get(nodeId));
        }
        int counter = 0;
        while(nodesQueue.size() > 0) {
            nodesConnectivityOrder.put(nodesQueue.poll().getId(), counter);
            counter++;
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

    public Map<Integer, NodeWorkflow> getModel(){
        return this.model;
    }

    public void trimModel(){
        for(NodeWorkflow workflow : getWorkflows()){
            workflow.trim();
        }
    }

    public int size(){
        int maxWorkflowSize = 0;
        for(NodeWorkflow workflow : getWorkflows()){
            if(maxWorkflowSize < workflow.size()){
                maxWorkflowSize = workflow.size();
            }
        }
        return maxWorkflowSize;
    }

    public RoutingModel getRoutingModel() {
        return routingModel;
    }

    public TaskModel getTaskModel() {
        return taskModel;
    }

    /**
     * UNUSED METHODS --------------------------------------------------------------------------------------------------
     */

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
