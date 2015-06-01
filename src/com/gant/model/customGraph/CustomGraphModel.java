package com.gant.model.customGraph;

import com.com.grapheditor.SystemGraph;
import com.com.grapheditor.TaskGraph;
import com.gant.model.Link;
import com.gant.model.Node;
import com.gant.model.Route;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

import java.util.*;

/**
 * Created by Andrii on 31.05.2015.
 */
public class CustomGraphModel {
    private Map<Integer, Node> model = new HashMap<>();
    private List<Link> links = new ArrayList<>();
    private boolean directed;
    private GraphElementsProvider elementsProvider;

    public CustomGraphModel(mxGraph graph, Class graphType, boolean isDirected) {
        Link.reinitId();
        Route.reinitId();
        if(graphType.equals(TaskGraph.class)){
            this.elementsProvider = new TaskGraphElementsProvider();
        } else if(graphType.equals(SystemGraph.class)){
            this.elementsProvider = new SystemGraphElementsProvider();
        }
        this.directed = isDirected;
        this.model = buildModel(graph);
    }

    public Node get(Integer id){
        return model.get(id);
    }

    public List<Link> getLinks(int nodeId) {
        Node node = model.get(nodeId);
        return node == null ? new ArrayList<>() : node.getLinks();
    }

    public List<Link> getLinks() {
        return this.links;
    }

    public boolean isDirected() {
        return directed;
    }

    public Set<Integer> getNodeIds(){
        return model.keySet();
    }

    public Map<Integer, Node> buildModel(mxGraph graph){
        Map<String, Object> cells = ((mxGraphModel) graph.getModel()).getCells();

        Map<Integer, Node> nodes = findNodes(cells);
        List<Link> links = findLinks(cells);
        this.links = links;

        for(Link link : links) {
            Node source = nodes.get(link.getSourceId());
            source.addLink(link.copy(true));
            Node target = nodes.get(link.getTargetId());
            target.addLink(link.copy(true));
        }
        if(!isDirected()){
            nodes = reassignSourceTarget(nodes);
        }
        return nodes;
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

    private Map<Integer, Node> findNodes(Map<String, Object> cells){
        Map<Integer, Node> nodes = new HashMap<>();
        for(Map.Entry<String, Object> entry : cells.entrySet()){
            mxCell currentCell = (mxCell) entry.getValue();
            if(currentCell.isVertex()){
                Node node = elementsProvider.buildNode(currentCell);
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
                Link link = elementsProvider.buildLink(currentCell);
                links.add(link);
            }
        }
        return links;
    }
}
