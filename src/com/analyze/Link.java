package com.analyze;

/**
 * Created by Andrew on 08.04.2015.
 */
public class Link {
    private Integer id;
    private Integer weight;
    private Integer sourceId;
    private Integer targetId;

    public Link(Integer id, Integer weight,Integer sourceId,Integer targetId){
        this.id = id;
        this.weight = weight;
        this.sourceId = sourceId;
        this.targetId = targetId;
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

	@Override
	public String toString() {
		return "Link [id=" + id + ", weight=" + weight + ", sourceId="
				+ sourceId + ", targetId=" + targetId + "]";
	}
	
    
}
