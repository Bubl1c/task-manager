package com.gant;

import com.analyze.AnalyzeManager;
import com.analyze.Task;
import com.com.grapheditor.SystemGraph;
import com.com.grapheditor.TaskGraph;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gant.model.TaskModel;
import com.gant.model.customGraph.CustomGraphModel;
import com.gant.model.RoutingModel;
import com.gant.planner.TaskPlanner;

/**
 * Created by Andrii Mozharovskyi on 28.05.2015.
 */
public class GantDiagram extends JPanel {
    static Map<String, ArrayList<NodeAction>> model;

    public GantDiagram() {
        model = new HashMap<>();
    }

    public static void init(){
        List<Task> tasks = new ArrayList<>();
        switch(Config.queueType) {
            case CRITICAL:
                tasks = AnalyzeManager.getCriticalPathOrderQueue(TaskGraph.graph);
                break;
            case NORMAL_CRITICAL:
                tasks = AnalyzeManager.getCriticalPathNormalizationOrderQueue(TaskGraph.graph);
                break;
            case RANDOM:
                tasks = AnalyzeManager.getRandomOrderQueue(TaskGraph.graph);
                break;
            default:
                tasks = AnalyzeManager.getRandomOrderQueue(TaskGraph.graph);
        }
//        int minNumberOfNodes = minNumberOfNodes(tasks);
//        if(minNumberOfNodes == 0) {
//            System.out.println("No tasks to process!");
//            return;
//        }
//        if(minNumberOfNodes > tasks.size()){
//            System.out.println(minNumberOfNodes + " nodes needed, but only " + tasks.size() + " exists!");
//            return;
//        }
        RoutingModel routingModel = new RoutingModel(SystemGraph.graph);
        TaskModel tasksModel = new TaskModel(TaskGraph.graph);
        TaskPlanner planner = new TaskPlanner(routingModel, tasksModel, tasks);
        planner.assignTasksToNodes();
        int i = 1;
    }

    public static int minNumberOfNodes(List<Task> tasks){
        if(tasks.size() == 0){
            return 0;
        }
        Integer criticalPathWeight = tasks.get(0) == null ? 0 : tasks.get(0).getCriticalPathWithVertex();

        int taskWeightsSum = 0;
        for(Task task : tasks){
            taskWeightsSum += task.getWeight();
        }

        return criticalPathWeight == 0 ? 0 : taskWeightsSum/criticalPathWeight;
    }
}
