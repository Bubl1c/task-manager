package com.gant.model;

import java.util.LinkedList;

/**
 * Created by Andrii Mozharovskyi on 29.05.2015.
 */
public class Route extends Link{
    private static int currentId = 1;
    private LinkedList<Link> links = new LinkedList<>();
    private boolean completed = false;

    public Route(Integer id, Integer sourceId, Integer targetId) {
        super(id, 0, sourceId, targetId);
    }

    public Route(Integer sourceId, Integer targetId) {
        super(getNextId(), 0, sourceId, targetId);
    }

    public int calculateWeight(LinkedList<Link> links){
        int weight = 0;
        for (Link l : links){
            weight += l.getWeight();
        }
        return weight;
    }

    public static int getNextId(){
        return currentId++;
    }
    public static void reinitId(){
        currentId = 1;
    }

    public void addLink(Link link){
        this.links.add(link);
    }

    public boolean containsLink(Link link) {
        return this.links.contains(link);
    }

    public boolean containsSourceNode(Integer nodeId) {
        for(Link link : links){
            if(link.getSourceId() == nodeId){
                return true;
            }
        }
        return false;
    }


    public LinkedList<Link> getLinks() {
        return links;
    }

    public Link getLastLink(){
        return this.getLinks() == null ? null : this.getLinks().getLast();
    }

    public Link getFirstLink(){
        return this.getLinks() == null ? null : this.getLinks().getFirst();
    }

    public void remove(Link link){
        this.links.remove(link);
    }

    public void setLinks(LinkedList<Link> links) {
        this.links = links;
    }

    public LinkedList<Integer> getVertexIds(){
        LinkedList<Integer> vertexes = new LinkedList<>();
        for(Link l : links){
            if(!vertexes.contains(l.getSourceId())){
                vertexes.add(l.getSourceId());
            }
            if(!vertexes.contains(l.getTargetId())){
                vertexes.add(l.getTargetId());
            }
        }
        return vertexes;
    }

    public Route copy() {
        Route route = new Route(this.getSourceId(), this.getTargetId());
        route.setLinks(new LinkedList<>(this.getLinks()));
        return route;
    }

    public Route copy(boolean saveId) {
        Route route;
        if(saveId){
            route = new Route(this.getId(), this.getSourceId(), this.getTargetId());
            route.setWeight(this.getWeight());
            route.setLinks(new LinkedList<>(this.getLinks()));
        } else {
            route = copy();
        }
        return route;
    }

    public void complete(){ this.completed = true; }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        if (!super.equals(o)) return false;

        Route route = (Route) o;

        return !(links != null ? !links.equals(route.links) : route.links != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "R[ " + getSourceId() + "-" + getTargetId() + " " + links +" ]";
    }
}
