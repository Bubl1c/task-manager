package com.gant.model;

/**
 * Created by Andrii on 01.06.2015.
 */
public class TaskState {
    private Integer taskId;
    private boolean processed;

    public TaskState(Integer taskId) {
        this.taskId = taskId;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
