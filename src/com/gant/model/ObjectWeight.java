package com.gant.model;

import java.util.Comparator;

/**
 * Created by Andrii on 09.06.2015.
 */
public class ObjectWeight implements Comparable<ObjectWeight> {
    private int weight;
    private Object object;

    public ObjectWeight(int weight, Object object) {
        this.weight = weight;
        this.object = object;
    }

    public int getWeight() {
        return weight;
    }

    public Object getObject() {
        return object;
    }

    public static Comparator<ObjectWeight> getComparator(){
        return new Comparator<ObjectWeight>() {
            @Override
            public int compare(ObjectWeight o1, ObjectWeight o2) {
                return o1.getWeight() - o2.getWeight();
            }
        };
    }

    @Override
    public int compareTo(ObjectWeight o) {
        return getWeight() - o.getWeight();
    }

    @Override
    public String toString() {
        return "" + weight;
    }
}
