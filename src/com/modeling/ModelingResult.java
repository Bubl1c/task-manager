package com.modeling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii on 11.06.2015.
 */
public class ModelingResult {
    List<Double> speedUpVals = new ArrayList<>();
    List<Double> effectivenessVals = new ArrayList<>();
    List<Double> timeVals = new ArrayList<>();

    private double speedup;
    private double effectiveness;
    private double time;

    private double connectivity;
    private int minTaskWeight;
    private int maxTaskWeight;
    private int numberOfTasks;

    public ModelingResult() {
    }

    public ModelingResult(double speedup, double effectiveness, double time) {
        this.speedup = speedup;
        this.effectiveness = effectiveness;
        this.time = time;
    }

    public ModelingResult(int minTaskWeight, int maxTaskWeight, int numberOfTasks, double connectivity) {
        this.minTaskWeight = minTaskWeight;
        this.maxTaskWeight = maxTaskWeight;
        this.numberOfTasks = numberOfTasks;
        this.connectivity = connectivity;
    }

    public void calculateMeans(){
        speedup = mean(speedUpVals);
        effectiveness = mean(effectivenessVals);
        time = mean(timeVals);
    }

    public static double calculateSpeedup(double timeOn1CPU, double timeOnCS){
        return timeOn1CPU/timeOnCS;
    }

    public static double calculateEffectiveness(double speedup, int numberOfCPUs){
        return speedup/numberOfCPUs;
    }

    public void addSpeedUpVal(double val){
        speedUpVals.add(val);
    }

    public void addEffectivenessVal(double val){
        effectivenessVals.add(val);
    }

    public void addTimeVal(double val){
        timeVals.add(val);
    }

    public int getRepeats(){
        return timeVals.size();
    }

    public List<Double> getSpeedUpVals() {
        return speedUpVals;
    }

    public List<Double> getEffectivenessVals() {
        return effectivenessVals;
    }

    public List<Double> getTimeVals() {
        return timeVals;
    }

    public double getSpeedup() {
        return speedup;
    }

    public void setSpeedup(double speedup) {
        this.speedup = speedup;
    }

    public double getEffectiveness() {
        return effectiveness;
    }

    public void setEffectiveness(double effectiveness) {
        this.effectiveness = effectiveness;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(double connectivity) {
        this.connectivity = connectivity;
    }

    public int getMinTaskWeight() {
        return minTaskWeight;
    }

    public void setMinTaskWeight(int minTaskWeight) {
        this.minTaskWeight = minTaskWeight;
    }

    public int getMaxTaskWeight() {
        return maxTaskWeight;
    }

    public void setMaxTaskWeight(int maxTaskWeight) {
        this.maxTaskWeight = maxTaskWeight;
    }

    public int getNumberOfTasks() {
        return numberOfTasks;
    }

    public void setNumberOfTasks(int numberOfTasks) {
        this.numberOfTasks = numberOfTasks;
    }

    public double mean(List<Double> values) {
        double sum = 0;
        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i);
        }
        return sum / values.size();
    }

    @Override
    public String toString() {
        return "s:" + speedup + ", e:" + effectiveness + ", t:" + time;
    }
}
