package com.gant.planner;

import com.analyze.Task;
import com.gant.model.RoutingModel;
import com.gant.model.TaskModel;

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

    public void assignTasksToNodes(){
        assignTopTasks();
        boolean continuePlanningFlag = true;
        while(continuePlanningFlag){
            Task task = taskQueue.poll();
            if(task != null) {
                if(getMostSuitableFreeNodeId(Task.class) != -1){

                }
            }
            //if()
            currentTic++;
        }
        if(currentTic > 800) {
            throw new RuntimeException("assignTasksToNodes() Fucking cycle!");
        }
    }

    private void assignTopTasks(){
        int mostSuitableNodeId = getMostSuitableFreeNodeId(Task.class);
        while(mostSuitableNodeId != -1 && taskQueue.size() > 0){
            Task task = taskQueue.poll();
            getWorkflow(mostSuitableNodeId).assignWork(task, currentTic);
            addChildsToWaitForParent(task);
            mostSuitableNodeId = getMostSuitableFreeNodeId(Task.class);
        }
    }

    private Integer getMostSuitableFreeNodeId(Class<? extends Plannable> freeFor){
        return getFreeRandomNodeId(freeFor);
    }

    private Integer getFreeRandomNodeId(Class<? extends Plannable> freeFor){
        List<Integer> freeNodeIds = getFreeNodeIds(freeFor);
        return freeNodeIds.size() == 0 ? -1 : freeNodeIds.get(r.nextInt(freeNodeIds.size()));
    }

    private List<Integer> getFreeNodeIds(Class<? extends Plannable> freeFor){
        List<Integer> freeNodeIds = new ArrayList<>();
        for(Map.Entry<Integer, Tic> entry : getTics(currentTic).entrySet()){
            if(entry.getValue().isFree(freeFor))
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
}
