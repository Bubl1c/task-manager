package com.gant;

/**
 * Created by Andrii Mozharovskyi on 29.05.2015.
 */
public class Config {
    public static boolean isIO = false;
    public static boolean duplex = false;
    public static QueueType queueType = QueueType.RANDOM;
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
