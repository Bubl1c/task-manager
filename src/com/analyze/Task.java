package com.analyze;

/**
 * Created by Andrew on 31.03.2015.
 */
public class Task implements Comparable<Task>{
    private Integer id;
    private Integer weight;
    private Integer criticalPath;
    private Integer criticalPathWithVertex;
    private double Pr;
    private Integer randomValue;

    public Task(Integer id, Integer weight){
        this.id = id;
        this.weight = weight;
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
		return id +" w="+weight+":" +criticalPath+";"+ criticalPathWithVertex+"|"+ Pr;
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
}
