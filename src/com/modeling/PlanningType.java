package com.modeling;

import com.gant.Config;

/**
 * Created by Andrii on 11.06.2015.
 */
public class PlanningType {

    private Config.QueueType queueType;
    private Config.AssignmentType assignmentType;

    public PlanningType(Config.QueueType queueType, Config.AssignmentType assignmentType) {
        this.queueType = queueType;
        this.assignmentType = assignmentType;
    }
}
