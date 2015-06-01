package com.gant.planner;

import com.analyze.Task;
import com.gant.model.RoutingModel;
import com.gant.model.TaskModel;

import java.util.*;

/**
 * Created by Andrii on 01.06.2015.
 */
public class TaskPlanner {
    private TaskModel taskModel;
    private RoutingModel routingModel;


    private LinkedList<Task> taskQueue;
    private LinkedList<Task> waitForParentTaskQueue;
    private Map<Integer, NodeWorkflow> model;
    private int currentTic = 0;

    private final Random r = new Random();

    public TaskPlanner(RoutingModel routingModel, TaskModel taskModel, List<Task> taskQueue) {
        this.routingModel = routingModel;
        this.taskModel = taskModel;
        init();
    }

    public Map<Integer, NodeWorkflow> assignTasksToNodes(){
        while(currentTic < 50){
            currentTic++;
            while(taskQueue.size() > 0){
                int nodeId = getMostSuitableNodeId();
                if(nodeId == -1) {
                    break;
                }
                Task task = taskQueue.remove();
                for(Integer childTaskId : taskModel.getChildNodeIds(task.getId())){
                    waitForParentTaskQueue.add(taskModel.getTask(childTaskId));
                }
                assignTask(taskQueue.remove(), nodeId);
            }
        }
        return this.model;
    }

    private void assignTask(Task task, int nodeId){
        NodeWorkflow workflow = model.get(nodeId);
        Tic tic = new Tic(workflow);
        tic.setTask(task);
        workflow.addTic(tic, currentTic);
    }

    private Integer getMostSuitableNodeId(){
        return getFreeRandomNodeId();
    }

    private Integer getFreeRandomNodeId(){
        List<Integer> freeNodeIds = getFreeNodeIds();
        if(freeNodeIds.size() == 0){
            return -1;
        }
        freeNodeIds.get(r.nextInt(freeNodeIds.size()));
        return 0;
    }

    private List<Integer> getFreeNodeIds(){
        List<Integer> freeNodeIds = new ArrayList<>();
        for(Map.Entry<Integer, Tic> entry : getTics(currentTic).entrySet()){
            if(entry.getValue().isFree())
                freeNodeIds.add(entry.getKey());
        }
        return freeNodeIds;
    }

    private Map<Integer, Tic> getTics(int ticNumber){
        Map<Integer, Tic> tics = new HashMap<>();
        for(Map.Entry<Integer, NodeWorkflow> entry : model.entrySet()){
            tics.put(entry.getKey(), entry.getValue().getTic(ticNumber));
        }
        return tics;
    }

    private void buildDefaultQueue(){
        List<Integer> topTaskIds = taskModel.getTopTaskIds();
        for(Task task : taskModel.getDefaultTaskQueue()){
            if(topTaskIds.contains(task.getId())){
                taskQueue.add(task);
            }
        }
    }

    private void init(){
        initModel();
        buildDefaultQueue();
    }

    private void initModel(){
        model = new HashMap<>();
        taskQueue = new LinkedList<>();
        for(Integer nodeId : routingModel.getNodeIds()){
            model.put(nodeId, new NodeWorkflow(nodeId));
        }
    }
}
