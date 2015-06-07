package com.gant.model;

import com.analyze.Task;
import com.com.grapheditor.TaskGraph;
import com.gant.model.customGraph.CustomGraphModel;
import com.mxgraph.view.mxGraph;

import java.util.*;

/**
 * Created by Andrii on 01.06.2015.
 */
public class TaskModel{
    private CustomGraphModel model;
    private Map<Integer, TaskState> taskStates;
    private List<Task> defaultTaskQueue;

    public TaskModel(mxGraph graph, List<Task> defaultTaskQueue) {
        this.model = new CustomGraphModel(graph, TaskGraph.class, true);
        this.defaultTaskQueue = defaultTaskQueue;
        taskStates = initTaskStates(this.model);
    }

    public List<Task> getJustProcessedTasks(List<Task> notProcessedOnPreviousTic){
        List<Task> currentTicNotProcessedTasks = getNotProcessedTasks();
        List<Task> justProcessedTasks = new ArrayList<>();
        for(Task task : notProcessedOnPreviousTic){
            if(!currentTicNotProcessedTasks.contains(task)){
                justProcessedTasks.add(task);
            }
        }
        return justProcessedTasks;
    }

    public boolean isAnyNotProcessedTask(){
        return getNotProcessedTasks().size() != 0;
    }

    public boolean isAnyNotProcessedParent(int taskId){
        return getNotProcessedParentTasks(taskId).size() != 0;
    }

    public List<Task> getNotProcessedParentTasks(int taskId){
        List<Task> notProcessedParentTasks = new ArrayList<>();
        for(int parentId : getParentTaskIds(taskId)){
            Task parentTask = getTask(parentId);
            if(!parentTask.isProcessed()){
                notProcessedParentTasks.add(parentTask);
            }
        }
        return notProcessedParentTasks;
    }

    public List<Task> getNotProcessedTasks(){
        List<Task> tasks = new ArrayList<>();
        for(Task task : defaultTaskQueue){
            if(!task.isProcessed()){
                tasks.add(task);
            }
        }
        return tasks;
    }

    public List<Integer> getChildTaskIds(int nodeId){
        List<Integer> childNodeIds = new ArrayList<>();
        for(Link link : model.getLinks(nodeId)){
            if(link.getSourceId() == nodeId){
                childNodeIds.add(link.getTargetId());
            }
        }
        return childNodeIds;
    }

    public List<Integer> getParentTaskIds(int nodeId){
        List<Integer> parentNodeIds = new ArrayList<>();
        for(Link link : model.getLinks(nodeId)){
            if(link.getTargetId() == nodeId){
                parentNodeIds.add(link.getSourceId());
            }
        }
        return parentNodeIds;
    }

    public Task getTask(Integer taskId){
        return getDefaultTask(taskId);
    }

    private Task getDefaultTask(Integer taskId){
        for(Task t : defaultTaskQueue){
            if(t.getId() == taskId) return t;
        }
        return null;
    }

    public List<Link> getIncomingLinks(Integer nodeId){
        List<Link> links = model.getLinks(nodeId);
        List<Link> incomingLinks = new ArrayList<>();
        for(Link link : links){
            if(link.getTargetId() == nodeId){
                incomingLinks.add(link);
            }
        }
        return incomingLinks;
    }

    public List<Integer> getTopTaskIds(){
        List<Integer> topTaskIds = new ArrayList<>();
        for(Integer taskId : model.getNodeIds()){
            if(getIncomingLinks(taskId).size() == 0)
                topTaskIds.add(taskId);
        }
        return topTaskIds;
    }

    private Map<Integer, TaskState> initTaskStates(CustomGraphModel model){
        Map<Integer, TaskState> taskStates = new HashMap<>();
        for(Integer taskId : model.getNodeIds()){
            TaskState state = new TaskState(getTask(taskId));
            taskStates.put(taskId, state);
        }
        return  taskStates;
    }

    public Set<Integer> getTaskIds(){
        return model.getNodeIds();
    }

    public List<Task> getDefaultTaskQueue() {
        return defaultTaskQueue;
    }

    public void setDefaultTaskQueue(List<Task> defaultTaskQueue) {
        this.defaultTaskQueue = defaultTaskQueue;
    }
}
