package com.gant;

import com.analyze.AnalyzeManager;
import com.analyze.Task;
import com.com.grapheditor.TaskGraph;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrii Mozharovskyi on 28.05.2015.
 */
public class GantDiagram extends JPanel {
    static Map<String, ArrayList<NodeAction>> model;

    public GantDiagram() {
        model = new HashMap<>();
    }

    public static void init(){
        ArrayList<Task> tasks = AnalyzeManager.getCriticalPathOrderQueue(TaskGraph.graph);
        int minNumberOfNodes = minNumberOfNodes(tasks);
        if(minNumberOfNodes == 0) {
            System.out.println("No tasks to process!");
        }
        if(minNumberOfNodes > tasks.size()){
            System.out.println(minNumberOfNodes + " nodes needed, but only " + tasks.size() + " exists!");
        }
    }

    public static int minNumberOfNodes(ArrayList<Task> tasks){
        if(tasks.size() == 0){
            return 0;
        }
        int criticalPathWeight = tasks.get(0) == null ? 0 : tasks.get(0).getCriticalPathWithVertex();

        int taskWeightsSum = 0;
        for(Task task : tasks){
            taskWeightsSum += task.getWeight();
        }

        return criticalPathWeight == 0 ? 0 : taskWeightsSum/criticalPathWeight;
    }
}
