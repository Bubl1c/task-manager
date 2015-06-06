package com.gant;

/**
 * Created by Andrii Mozharovskyi on 29.05.2015.
 */
public class Config {
    public static boolean isIO = true;
    public static boolean duplex = true;
    public static QueueType queueType = QueueType.NORMAL_CRITICAL;
    public static AssignmentType assignmentType = AssignmentType.RANDOM;
    public static int physLinksNumber = 1;

    public static enum QueueType {
        NORMAL_CRITICAL,
        CRITICAL,
        RANDOM
    }

    public static enum AssignmentType {
        NEIGHBOR_5,
        RANDOM
    }
}
