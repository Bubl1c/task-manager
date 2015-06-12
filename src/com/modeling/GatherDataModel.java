package com.modeling;

import com.com.grapheditor.ActionManager;
import com.com.grapheditor.SystemGraph;
import com.com.grapheditor.TaskGraph;
import com.gant.Config;
import com.gant.GantDiagram;
import com.gant.planner.TaskPlanner;
import com.mxgraph.view.mxGraph;
import org.codehaus.groovy.runtime.powerassert.SourceText;

import java.util.*;

/**
 * Created by Andrii on 11.06.2015.
 */
public class GatherDataModel {

    public static final String PL_TYPE_SPLIT_SYMBOL = "-";

    public void workflow(){
        Map<Object, Map<Object, Map<Object, ModelingResult>>> map = planWithinDiffrentTasksCount(1, 5, 0.2, 1.0, 0.2, 10);
        int i = 0;
    }

    public Map<Object, Map<Object, Map<Object, ModelingResult>>> planWithinDiffrentTasksCount(int min, int max, double connMin, double connMax, double connStep, int repeats){
        Map<Object, Map<Object, Map<Object, ModelingResult>>> map = new TreeMap<>();

        for(int i : getTaskCountsInTaskGraph()){
            System.out.println("++++++++++++++++++++++++++++++++ Task counts:  " + i);
            map.put(i, planAlgorithms(min, max, i, connMin, connMax, connStep, repeats));
        }

        return map;
    }

    public Map<Object, Map<Object, ModelingResult>> planAlgorithms(int min, int max, int count, double connMin, double connMax,
                                                                   double connStep, int repeats){
        Map<Object, Map<Object, ModelingResult>> map = getEmptyCombinationsMap(getAlgorithmCombinationsList());
        System.out.println("++++++++++++++++++++++++++++++++ Task counts:  " + count);
        Set keys = map.keySet();
        for(Object key : keys){
            System.out.println("=========================================== " + key);
            map.put(key, planAlgorithm((String) key, min, max, count, connMin, connMax, connStep, repeats));
        }

        return map;
    }

    public Map<Object, Map<Object, ModelingResult>> planPhisLinks(int min, int max, int count, double connMin, double connMax,
                                                                   double connStep, int repeats){
        Map<Object, Map<Object, ModelingResult>> map = getEmptyCombinationsMap(getPhisLinksCombinationsList());
        System.out.println("++++++++++++++++++++++++++++++++ Task counts:  " + count);
        Set keys = map.keySet();
        for(Object key : keys){
            System.out.println("=========================================== " + key);
            map.put(key, planPhisLink((int) key, min, max, count, connMin, connMax, connStep, repeats));
        }

        return map;
    }

    public Map<Object, Map<Object, ModelingResult>> planIO(int min, int max, int count, double connMin, double connMax,
                                                                  double connStep, int repeats){
        Map<Object, Map<Object, ModelingResult>> map = new TreeMap<>();
        System.out.println("++++++++++++++++++++++++++++++++ Task counts:  " + count);

        System.out.println("=========================================== " + "+DUPLEX");
        map.put(true, planIsIO(true, min, max, count, connMin, connMax, connStep, repeats));

        System.out.println("=========================================== " + "-DUPLEX");
        map.put(false, planIsIO(false, min, max, count, connMin, connMax, connStep, repeats));

        return map;
    }

    public Map<Object, ModelingResult> planIsIO(boolean isIO, int min, int max, int count, double connMin, double connMax, double connStep, int repeats){

        Config.createCopy();

        Config.duplex = isIO;

        //System.out.println("=========================================== " + Config.getAsString());

        Map<Object, ModelingResult> map = new TreeMap<>();

        for (double i = connMin; i < connMax; i += connStep) {
            System.out.println("connectivity " + i);
            map.put(i, generate(min, max, count, i, repeats));
        }

        Config.restoreFromCopy();
        return map;
    }

    public Map<Object, ModelingResult> planPhisLink(int phisLinksNumber, int min, int max, int count, double connMin, double connMax, double connStep, int repeats){

        Config.createCopy();

        Config.physLinksNumber = phisLinksNumber;

        //System.out.println("=========================================== " + Config.getAsString());

        Map<Object, ModelingResult> map = new TreeMap<>();

        for (double i = connMin; i < connMax; i += connStep) {
            System.out.println("connectivity " + i);
            map.put(i, generate(min, max, count, i, repeats));
        }

        Config.restoreFromCopy();
        return map;
    }

    public Map<Object, ModelingResult> planAlgorithm(String algString, int min, int max, int count, double connMin, double connMax, double connStep, int repeats){
        String[] algs = algString.split(PL_TYPE_SPLIT_SYMBOL);
        int queueAlg = Integer.parseInt(algs[0]);
        int planAlg = Integer.parseInt(algs[1]);

        Config.createCopy();

        Config.queueType = Config.QueueType.getById(queueAlg);
        Config.assignmentType = Config.AssignmentType.getById(planAlg);

        //System.out.println("=========================================== " + Config.getAsString());

        Map<Object, ModelingResult> map = new TreeMap<>();

        for (double i = connMin; i < connMax; i += connStep) {
            System.out.println("connectivity " + i);
            map.put(i, generate(min, max, count, i, repeats));
        }

        Config.restoreFromCopy();
        return map;
    }

    public ModelingResult generate(int min, int max, int count, double connectivity, int repeats){
        ModelingResult result = new ModelingResult(min, max, count, connectivity);
        for (int i = 0; i < repeats; i++) {
            mxGraph graph = ActionManager.generateGraph(min, max, count, connectivity);

            Date timeStrat = new Date();
            TaskPlanner plan = GantDiagram.generate(graph, SystemGraph.graph);
            Date timeEnd = new Date();

            long resTime = timeEnd.getTime() - timeStrat.getTime();
            double speedup = ModelingResult.calculateSpeedup(plan.getTimeToProcessTasksOn1CPU(), plan.size());
            double effectiveness = ModelingResult.calculateEffectiveness(speedup, plan.getRoutingModel().getNodeIds().size());

            result.addSpeedUpVal(speedup);
            result.addEffectivenessVal(effectiveness);
            result.addTimeVal(resTime);
        }
        result.calculateMeans();

        return result;
    }

    public Map<Object, Map<Object, ModelingResult>> getEmptyCombinationsMap(List<Object> list){
        Map<Object, Map<Object, ModelingResult>> map = new TreeMap<>();
        for(Object o : list){
            map.put(o, new TreeMap<>());
        }
        return map;
    }

    public List<Integer> getTaskCountsInTaskGraph(){
        List<Integer> list = new ArrayList<>();
//        list.add(7);
//        list.add(14);
//        list.add(21);
        list.add(28);
        return list;
    }

    public List<Object> getPhisLinksCombinationsList(){
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(i+1);
        }
        return list;
    }
    
    public List<Object> getAlgorithmCombinationsList(){
        List<Object> list = new ArrayList<>();
        list.add(new PlanningType(Config.QueueType.NORMAL_CRITICAL, Config.AssignmentType.RANDOM));
        list.add(new PlanningType(Config.QueueType.NORMAL_CRITICAL, Config.AssignmentType.NEIGHBOR_5));
        list.add(new PlanningType(Config.QueueType.CRITICAL, Config.AssignmentType.RANDOM));
        list.add(new PlanningType(Config.QueueType.CRITICAL, Config.AssignmentType.NEIGHBOR_5));
        list.add(new PlanningType(Config.QueueType.RANDOM, Config.AssignmentType.RANDOM));
        list.add(new PlanningType(Config.QueueType.RANDOM, Config.AssignmentType.NEIGHBOR_5));
        return list;
    }

}
