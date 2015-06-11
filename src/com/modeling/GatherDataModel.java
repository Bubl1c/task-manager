package com.modeling;

import com.com.grapheditor.ActionManager;
import com.gant.Config;
import com.gant.GantDiagram;
import com.gant.planner.TaskPlanner;
import com.mxgraph.view.mxGraph;

import java.util.*;

/**
 * Created by Andrii on 11.06.2015.
 */
public class GatherDataModel {

    public void workflow(){


    }

//    public Map<Object, Map<Object, Map<Object, Double>>> planFewTimes(int times){
//        Map<Object, Map<Object, Map<Object, Double>>> map = new TreeMap<>();
//
//        for(int i : getTaskCountsInTaskGraph()){
//            map.put(i, planAlgorithms(min, max, i, connMin, connMax, connStep));
//        }
//
//        return map;
//    }

    public Map<Object, Map<Object, Map<Object, Double>>> planWithinDiffrentTasksCount(int min, int max, double connMin, double connMax, double connStep){
        Map<Object, Map<Object, Map<Object, Double>>> map = new TreeMap<>();

        for(int i : getTaskCountsInTaskGraph()){
            map.put(i, planAlgorithms(min, max, i, connMin, connMax, connStep));
        }

        return map;
    }

    public Map<Object, Map<Object, Double>> planAlgorithms(int min, int max, int count, double connMin, double connMax, double connStep){
        Map<Object, Map<Object, Double>> map = getEmptyAlgorithmCombinationsMap();

        Set keys = map.keySet();
        for(Object key : keys){
            map.put(key, planAlgorithm((String) key, min, max, count, connMin, connMax, connStep));
        }

        return map;
    }

    public Map<Object, Double> planAlgorithm(String algString, int min, int max, int count, double connMin, double connMax, double connStep){
        String[] algs = algString.split("-");
        int queueAlg = Integer.parseInt(algs[0]);
        int planAlg = Integer.parseInt(algs[1]);

        Config.createCopy();

        Config.queueType = Config.QueueType.getById(queueAlg);
        Config.assignmentType = Config.AssignmentType.getById(planAlg);

        Map<Object, Double> map = new TreeMap<>();

        for (double i = connMin; i < connMax; i += connStep) {
            map.put(i, generate(min, max, count, i));
        }

        Config.restoreFromCopy();
        return map;
    }

    public Double generate(int min, int max, int count, double connectivity){
        mxGraph graph = ActionManager.generateGraph(min, max, count, connectivity);
        TaskPlanner plan = GantDiagram.generate();
        return (double)plan.size();
    }

    public Map<Object, Map<Object, Double>> getEmptyAlgorithmCombinationsMap(){
        Map<Object, Map<Object, Double>> map = new TreeMap<>();
        for(String s : getAlgorithmCombinationsList()){
            map.put(s, new TreeMap<>());
        }
        return map;
    }

    public List<Integer> getTaskCountsInTaskGraph(){
        List<Integer> list = new ArrayList<>();
        list.add(7);
        list.add(14);
        list.add(21);
        list.add(28);
        return list;
    }
    
    public List<String> getAlgorithmCombinationsList(){
        List<String> list = new ArrayList<>();
        list.add("1-1");
        list.add("1-5");
        list.add("3-1");
        list.add("3-5");
        list.add("13-1");
        list.add("13-5");
        return list;
    }

}
