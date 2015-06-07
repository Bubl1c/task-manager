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

    public static ConfigCopy copy;

    public static enum QueueType {
        NORMAL_CRITICAL,
        CRITICAL,
        RANDOM
    }

    public static enum AssignmentType {
        NEIGHBOR_5,
        RANDOM
    }
    
    public static class ConfigCopy {
        public boolean isIO;
        public boolean duplex;
        public QueueType queueType;
        public AssignmentType assignmentType;
        public int physLinksNumber;

        public ConfigCopy() {
            this.isIO = Config.isIO;
            this.duplex = Config.duplex;
            this.queueType = Config.queueType;
            this.assignmentType = Config.assignmentType;
            this.physLinksNumber = Config.physLinksNumber;
        }
    }

    public static void createCopy(){
        copy = new ConfigCopy();
    }
    
    public static void restoreFromCopy(){
        Config.isIO = copy.isIO;
        Config.duplex = copy.duplex;
        Config.queueType = copy.queueType;
        Config.assignmentType = copy.assignmentType;
        Config.physLinksNumber = copy.physLinksNumber;
    }
}
