package com.vinted.ab;

import com.google.gson.annotations.SerializedName;

public class AbTestVariant {

    @SerializedName("name")
    private String name;

    @SerializedName("chance_weight")
    private int chanceWeight;

    private AbTest abTest;

    public AbTestVariant() {
    }

    public AbTestVariant(String name, int chanceWeight) {
        this.name = name;
        this.chanceWeight = chanceWeight;
    }

    public String getName() {
        return name;
    }

    public int getChanceWeight() {
        return chanceWeight;
    }

    public AbTest getAbTest() {
        return abTest;
    }

    public void setAbTest(AbTest abTest) {
        this.abTest = abTest;
    }
}
