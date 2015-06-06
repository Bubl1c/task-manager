package com.gant.planner;

/**
 * Created by Andrii on 05.06.2015.
 */
public interface Plannable {
    Integer getWeight();
    boolean processTic();
    boolean isProcessed();
}
