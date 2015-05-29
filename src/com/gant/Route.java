package com.gant;

import com.analyze.Link;

import java.util.LinkedList;

/**
 * Created by Andrii Mozharovskyi on 29.05.2015.
 */
public class Route extends Link{
    private LinkedList<Link> links;

    public Route(Integer id, Integer weight, Integer sourceId, Integer targetId) {
        super(id, weight, sourceId, targetId);
    }

    public Route(Integer id, Integer weight, LinkedList<Link> links, Integer sourceId, Integer targetId) {
        super(id, weight, sourceId, targetId);
        this.links = links;
    }

    public int calculateWeight(LinkedList<Link> links){
        int weight = 0;
        for (Link l : links){
            weight += l.getWeight();
        }
        return weight;
    }

    public void addLink(Link link){
        this.links.add(link);
    }

    public LinkedList<Link> getLinks() {
        return links;
    }
}
