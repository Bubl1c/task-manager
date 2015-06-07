package com.gant.model;

import com.com.grapheditor.SystemGraph;
import com.gant.model.customGraph.CustomGraphModel;
import com.mxgraph.view.mxGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Andrii on 31.05.2015.
 */
public class RoutingModel {
    private CustomGraphModel model;
    private List<Route> routes = new ArrayList<>();

    public RoutingModel(mxGraph graph) {
        this.model = new CustomGraphModel(graph, SystemGraph.class, false);
        this.routes = buildRoutes();
        this.model = assignRoutesToNodes(this.model, this.routes);
    }

    public Node get(int nodeId){
        return model.get(nodeId);
    }

    public Set<Integer> getNodeIds(){
        return model.getNodeIds();
    }

    private CustomGraphModel assignRoutesToNodes(CustomGraphModel model, List<Route> routes){
        for(Integer nodeId : model.getNodeIds()){
            Node currentNode = model.get(nodeId);
            if(currentNode != null){
                currentNode.setRoutes(new ArrayList<>());
                for(Route route : routes){
                    if(route.getSourceId() == currentNode.getId())
                        currentNode.addRoute(route);
                }
            }

        }
        return model;
    }

    private List<Route> buildRoutes(){
        List<Route> routes = new ArrayList<>();
        for(Integer sourceNodeId : model.getNodeIds()){
            for(Integer targetNodeId : model.getNodeIds()){
                if(targetNodeId == sourceNodeId) continue;
                getAllRoutes(sourceNodeId, targetNodeId, routes);
            }
        }
        return routes;
    }

    private void getAllRoutes(int sourceId, int targetId, List<Route> routes){
        for(Link link : model.getLinks(sourceId)){
            Route route = new Route(sourceId, targetId);
            route.addLink(link);
            routes.add(route);
            getAllRoutes(sourceId, targetId, link.getTargetId(), route, routes);
        }
    }

    private void getAllRoutes(int sourceId, int targetId, int currentNodeId, Route currentRoute, List<Route> routes){
        if(currentNodeId == targetId) {
            routes.remove(currentRoute);
            routes.add(currentRoute.copy());
            currentRoute.remove(currentRoute.getLastLink());
            return;
        }

        if(currentRoute.containsSourceNode(currentNodeId)){
            routes.remove(currentRoute);
            currentRoute.remove(currentRoute.getLastLink());
            return;
        }

        List<Link> links = model.getLinks(currentNodeId);
        boolean isFirstLinkToContinueCurrentRoute = true;
        for(Link link : links){
            Route newRoute = currentRoute;
            if(isFirstLinkToContinueCurrentRoute){
                isFirstLinkToContinueCurrentRoute = false;
            } else {
                newRoute = currentRoute.copy();
                routes.add(newRoute);
            }
            if(!currentRoute.containsLink(link)){
                newRoute.addLink(link);
                getAllRoutes(sourceId, targetId, link.getTargetId(), newRoute, routes);
            } else {
                routes.remove(newRoute);
            }
        }
        currentRoute.remove(currentRoute.getLastLink());
    }
}
