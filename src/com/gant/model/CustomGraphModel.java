package com.gant.model;

import com.com.grapheditor.SystemGraph;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

import java.util.*;

/**
 * Created by Andrii on 31.05.2015.
 */
public class CustomGraphModel {
    private Map<Integer, Node> model;

    public CustomGraphModel() {
    }

    public CustomGraphModel(mxGraph graph) {
        Link.reinitId();
        Route.reinitId();
        this.model = buildModel(graph);
    }

    public Node get(Integer id){
        return model.get(id);
    }

    public List<Link> getLinks(int nodeId) {
        Node node = model.get(nodeId);
        return node == null ? new ArrayList<>() : node.getLinks();
    }

    public Set<Integer> getNodeIds(){
        return model.keySet();
    }

    private Map<Integer, Node> reassignSourceTarget(Map<Integer, Node> nodes){
        for(Integer nodeId : nodes.keySet()){
            Node currentNode = nodes.get(nodeId);
            Iterator<Link> linksIterator = currentNode.getLinks().iterator();
            while(linksIterator.hasNext()){
                Link link = linksIterator.next();
                link.setTargetId(link.getTargetId() == nodeId ? link.getSourceId() : link.getTargetId());
                link.setSourceId(nodeId);
            }
        }
        return nodes;
    }

    public Map<Integer, Node> buildModel(mxGraph graph){
        Map<String, Object> cells = ((mxGraphModel) SystemGraph.graph.getModel()).getCells();

        Map<Integer, Node> nodes = findNodes(cells);
        List<Link> links = findLinks(cells);

        for(Link link : links) {
            Node source = nodes.get(link.getSourceId());
            source.addLink(link.copy(true));
            Node target = nodes.get(link.getTargetId());
            target.addLink(link.copy(true));
        }
        nodes = reassignSourceTarget(nodes);
        return nodes;
    }

    private Map<Integer, Node> findNodes(Map<String, Object> cells){
        Map<Integer, Node> nodes = new HashMap<>();
        for(Map.Entry<String, Object> entry : cells.entrySet()){
            mxCell currentCell = (mxCell) entry.getValue();
            if(currentCell.isVertex()){
                Node node = buildNode(currentCell);
                nodes.put(node.getId(), node);
            }
        }
        return nodes;
    }

    private List<Link> findLinks(Map<String, Object> cells){
        List<Link> links = new LinkedList<>();
        for(Map.Entry<String, Object> entry : cells.entrySet()){
            mxCell currentCell = (mxCell) entry.getValue();
            if(currentCell.isEdge()){
                Link link = buildLink(currentCell);
                links.add(link);
            }
        }
        return links;
    }

    private Link buildLink(mxCell cell){
        Link link;
        try {
            int sourceId = Integer.parseInt(cell.getSource().getId()) - 1;
            int targetId = Integer.parseInt(cell.getTarget().getId()) - 1;
            link = new Link(getLinkWeight(cell), sourceId, targetId, cell);
        } catch(Exception e){
            throw new RuntimeException("Filed to create link for cell: " + cell.getId() + " [" + cell.getValue() + "]");
        }
        return link;
    }

    private Node buildNode(mxCell cell){
        Node node;
        try {
            node = new Node(Integer.parseInt(cell.getId()) - 1, getNodeWeight(cell), cell);
        } catch(Exception e){
            throw new RuntimeException("Filed to create node for cell: " + cell.getId()+ " [" + cell.getValue() + "]");
        }
        return node;
    }

    private int getLinkWeight(mxCell cell) {
        Integer weight = 0;
        try {
            String str = (String) cell.getValue();
            weight = str.equals("") ? 1 : Integer.parseInt(str);
        } catch (Exception e){
            throw new IllegalArgumentException("Invalid cell value! Cannot get link weight.");
        }
        return weight;
    }

    private int getNodeWeight(mxCell cell) {
        Integer weight = 0;
        try {
            String str = (String) cell.getValue();
            weight = Integer.parseInt(str.substring(str.indexOf("[") + 1, str.indexOf("]")));
        } catch (Exception e){
            throw new IllegalArgumentException("Invalid cell value! Cannot get node weight.");
        }
        return weight;
    }
}
