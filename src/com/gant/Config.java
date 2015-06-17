package com.gant;

/**
 * Created by Andrii Mozharovskyi on 29.05.2015.
 */
public class Config {
    public static boolean isIO = false;
    public static boolean duplex = false;
    public static QueueType queueType = QueueType.NORMAL_CRITICAL;
    public static AssignmentType assignmentType = AssignmentType.NEIGHBOR_7;
    public static int physLinksNumber = 1;

    public static ConfigCopy copy;

    public static enum QueueType {
        NORMAL_CRITICAL(1),
        CRITICAL(3),
        RANDOM(13);

        private final int id;

        QueueType(int id) {
            this.id = id;
        }

        public int getId(){
            return this.id;
        }

        public static QueueType getById(int id){
            QueueType[] vals = QueueType.values();
            for(QueueType qt : vals){
                if(qt.getId() == id){
                    return qt;
                }
            }
            return QueueType.NORMAL_CRITICAL;
        }
    }

    public static enum AssignmentType {
        NEIGHBOR_5(5),
        NEIGHBOR_7(7),
        RANDOM(1);

        private final int id;

        AssignmentType(int id) {
            this.id = id;
        }

        public int getId(){
            return this.id;
        }

        public static AssignmentType getById(int id){
            AssignmentType[] vals = AssignmentType.values();
            for(AssignmentType at : vals){
                if(at.getId() == id){
                    return at;
                }
            }
            return AssignmentType.NEIGHBOR_5;
        }
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

    public static String getAsString(){
        return "io:" + isIO + " dup:" + duplex + " q:" + queueType.getId() + " ass: " + assignmentType.getId();
    }
}
