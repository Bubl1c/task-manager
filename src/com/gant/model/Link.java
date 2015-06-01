package com.gant.model;

import com.mxgraph.model.mxCell;

/**
 * Created by Andrew on 08.04.2015.
 */
public class Link {
    private static int currentId = 1;

    private Integer id;
    private Integer weight;
    private Integer sourceId;
    private Integer targetId;
    private mxCell cell;
    private LinkState state;

    public Link(Integer weight,Integer sourceId,Integer targetId){
        this.id = getNextId();
        this.weight = weight;
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public Link(Integer id, Integer weight, Integer sourceId, Integer targetId){
        this(weight, sourceId, targetId);
        this.id = id;
    }

    public Link(Integer weight, Integer sourceId, Integer targetId, mxCell cell) {
        this(weight, sourceId, targetId);
        this.cell = cell;
    }
    public Link(Integer id, Integer weight, Integer sourceId, Integer targetId, mxCell cell) {
        this(id, weight, sourceId, targetId);
        this.cell = cell;
    }

    public static int getNextId(){
        return currentId++;
    }
    public static void reinitId(){
        currentId = 1;
    }

    public Integer getId() {
        return id;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public mxCell getCell() {
        return cell;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCell(mxCell cell) {
        this.cell = cell;
    }

    public Link copy(boolean saveId){
        return new Link(saveId ? getId() : getNextId(), getWeight(), getSourceId(), getTargetId(), getCell());
    }

    public LinkState getState() {
        return state;
    }

    public void setState(LinkState state) {
        this.state = state;
    }

    //    @Override
//	public String toString() {
//		return "l{id=" + id + ", sourId="
//				+ sourceId + ", targId=" + targetId + "}";
//	}

    @Override
	public String toString() {
		return " " + /*id + "|" + sourceId + "^" +*/ targetId + " ";
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;

        Link link = (Link) o;

        return !(id != null ? !id.equals(link.id) : link.id != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        return result;
    }
}
