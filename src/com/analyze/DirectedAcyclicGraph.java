package com.analyze;

import java.util.*;

/**
 * Created by Andrew on 08.04.2015.
 */
public class DirectedAcyclicGraph {
    public Map<Integer,Task> vertexMap = new HashMap<Integer, Task>();
    public Map<Integer,Link> edgeMap = new HashMap<Integer, Link>();
    public Integer Ncr;
    public Integer Tcr;
    private ArrayList<String> vertexPaths;
    
    public ArrayList<Task> getCriticalPathWeightsWithNormalization(ArrayList<Task> list, Integer maxNcr, Integer maxTcr){
    	for(Task task : list){
    		double Pri = task.getCriticalPathWithVertex().doubleValue()/maxTcr + task.getCriticalPath().doubleValue()/maxNcr;
    		Pri = (int)(Pri*100)/100.0;
    		task.setPr(Pri);
    	}
    	return list;
    }
    
    public ArrayList<Task> getVertexCriticalPathWeights(){
    	ArrayList<Task> taskList = new ArrayList<Task>();
    	Ncr = 0;
    	Tcr = 0;
    	for(Map.Entry<Integer,Task> t : vertexMap.entrySet()){
    		String path = getCriticalPath(t.getKey());
    		Task task = t.getValue();
    		task.setCriticalPath(pathWeight(path));
    		task.setCriticalPathWithVertex(getCriticalPathVertexWeight(t.getKey()));
    		taskList.add(task);
    		if(Ncr < task.getCriticalPath() ){
    			Ncr = task.getCriticalPath(); 
    		}
    		if(Tcr < task.getCriticalPathWithVertex()){
    			Tcr = task.getCriticalPathWithVertex();
    		}
    	}
    	return taskList;
    }
    
    private String getCriticalPath(Integer sourseId){
    	vertexPaths = new ArrayList<String>();
    	ArrayList<Integer> pathWeightList =  new ArrayList<Integer>();
        buildPaths(sourseId,"");
        for(String path : vertexPaths){
        	pathWeightList.add(pathWeight(path));
        }
        Integer maxWeight = Collections.max(pathWeightList);
        int criticalPathIndex = pathWeightList.indexOf(maxWeight);
        return vertexPaths.get(criticalPathIndex).trim();
    }
    
    private Integer getCriticalPathVertexWeight(Integer sourseId){
    	ArrayList<Integer> pathWeightList =  new ArrayList<Integer>();
    	for(String path : vertexPaths){
    		int weight = pathVertexWeight(path);
    		if(weight == -1){
    			pathWeightList.add(vertexMap.get(sourseId).getWeight());
    		}else{
    			pathWeightList.add(weight);
    		}  	
        }
    	Integer maxWeight = Collections.max(pathWeightList);
    	return maxWeight;
    }

    private int pathVertexWeight(String path){
    	int weight = 0;
    	path = path.trim();
    	if(path.equals("")){
    		return -1;
    	}
    	String[] edgeIds = path.split(" ");
    	Set<Integer> vertexIds = new HashSet<Integer>();
    	for(int i=0; i<edgeIds.length; i++){
    		Link link = edgeMap.get(Integer.valueOf(edgeIds[i]));
    		vertexIds.add(link.getSourceId());
    		vertexIds.add(link.getTargetId());
    	}
    	for(Integer id : vertexIds){
    		weight += vertexMap.get(id).getWeight();
    	}
    	return weight;
    }
    private int pathWeight(String path){
    	int weight = 1;
    	path = path.trim();
    	if(path.equals("")){
    		return 1;
    	}
    	String[] edgeIds = path.split(" ");
    	for(int i=0; i<edgeIds.length; i++){
    		Link link = edgeMap.get(Integer.valueOf(edgeIds[i]));
    		weight += link.getWeight();
    	}
    	return weight;
    }
    
    private void buildPaths(Integer fromId, String path){
    	ArrayList<Integer> linksID = containsLinkWithSourseId(fromId);
        if(linksID.size() > 0){
        	for(int i=0; i<linksID.size(); i++){
        		String newPath =path + " " + linksID.get(i);
        		buildPaths(edgeMap.get(linksID.get(i)).getTargetId(),newPath);
        	}
        }else{
        	//end point vertex
        	vertexPaths.add(path);
        }
    }
    
    private ArrayList<Integer> containsLinkWithSourseId(Integer sourseId){
    	ArrayList<Integer> linksID = new ArrayList<Integer>();
    	for(Map.Entry<Integer,Link> e : edgeMap.entrySet()){
            Link link = e.getValue();
            if(link.getSourceId().equals(sourseId)){
            	linksID.add(link.getId());
            }
        }
    	return linksID;
    }
}
