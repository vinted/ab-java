package com.vinted.ab;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;

public class TestOutput {

    @SerializedName("test")
    public String testName;

    @SerializedName("variants")
    public JsonObject variants;

    public Set<String> get(String variant) {
        JsonArray variantJson = variants.getAsJsonArray(variant);

        Set<String> result = new HashSet<>();
        for (JsonElement element : variantJson) {
            result.add(element.getAsString());
        }

        return result;
    }
}
