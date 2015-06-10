package com.gant.model;

import com.mxgraph.model.mxCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii on 31.05.2015.
 */
public class Node implements Comparable<Node> {
    private Integer id;
    private Integer weight;
    private List<Link> links = new ArrayList<>();
    private mxCell cell;
    private List<Route> routes = new ArrayList<>();

    public Node() {
    }

    public Node(Integer id, Integer weight, mxCell cell) {
        this.cell = cell;
        this.id = id;
        this.weight = weight;
    }

    public Node(Integer id, Integer weight, mxCell cell, List<Link> links) {
        this(id, weight, cell);
        this.links = links;
    }

    public void addRoute(Route route){
        routes.add(route);
    }

    public void addLink(Link link){
        links.add(link);
    }

    public Integer getId() {
        return id;
    }

    public Integer getWeight() {
        return weight;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public mxCell getCell() {
        return cell;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    @Override
    public int compareTo(Node obj) {
        return links.size() - obj.getLinks().size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        if (!id.equals(node.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", weight=" + weight +
                '}';
    }
}
