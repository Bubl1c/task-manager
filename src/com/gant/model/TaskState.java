package com.gant.model;

import com.analyze.Task;

/**
 * Created by Andrii on 01.06.2015.
 */
public class TaskState {
    private Task task;
    private int progress;

    public TaskState(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isProcessed() {
        return task.getWeight() == progress;
    }
}
