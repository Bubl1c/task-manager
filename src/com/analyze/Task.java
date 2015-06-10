package com.analyze;

import com.gant.model.TaskState;
import com.gant.planner.Plannable;
import com.gant.planner.Tic;

import java.io.Serializable;

/**
 * Created by Andrew on 31.03.2015.
 */
public class Task implements Comparable<Task>, Plannable, Serializable {
    private Integer id;
    private Integer weight = 0;
    private Integer criticalPath = 0;
    private Integer criticalPathWithVertex = 0;
    private double Pr;
    private Integer randomValue;
    private double priority;
    private int progress;
    private int processedBy;
    private int processedTicNumber;

    public Task(Integer id, Integer weight){
        this.id = id;
        this.weight = weight;
    }

    @Override
    public boolean processTic(){
        progress++;
        return isProcessed() ? true : false;
    }

    @Override
    public boolean isProcessed() {
        return weight == progress;
    }

    public Integer getId() {
        return id;
    }

    public Integer getWeight() {
        return weight;
    }
    
    public Integer getCriticalPath() {
		return criticalPath;
	}

	public void setCriticalPath(Integer criticalPath) {
		this.criticalPath = criticalPath;
	}

	public Integer getCriticalPathWithVertex() {
		return criticalPathWithVertex;
	}

	public void setCriticalPathWithVertex(Integer criticalPathWithvertex) {
		this.criticalPathWithVertex = criticalPathWithvertex;
	}

    @Override
	public String toString() {
		return "T" + id + "["  + getPriority() + "]";
	}

	@Override
    public int compareTo(Task obj) {
        return weight - obj.getWeight();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != null ? !id.equals(task.id) : task.id != null) return false;
        return !(weight != null ? !weight.equals(task.weight) : task.weight != null);

    }

	public double getPr() {
		return Pr;
	}

	public void setPr(double pr) {
		Pr = pr;
	}

    public Integer getRandomValue() {
        return randomValue;
    }

    public void setRandomValue(Integer randomValue) {
        this.randomValue = randomValue;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public int getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(int processedBy) {
        this.processedBy = processedBy;
    }

    public int getProcessedTicNumber() {
        return processedTicNumber;
    }

    public void setProcessedTicNumber(int processedTicNumber) {
        this.processedTicNumber = processedTicNumber;
    }
}
