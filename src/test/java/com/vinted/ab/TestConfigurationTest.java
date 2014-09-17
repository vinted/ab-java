package com.vinted.ab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Map.Entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestConfigurationTest {

    private Set<String> folders = new HashSet<>();

    @Before
    public void setUp() {
        folders.add("all_buckets");
        folders.add("already_finished");
        folders.add("already_finished_no_start_date");
        folders.add("big_weights");
        folders.add("explicit_times");
        folders.add("few_buckets");
        folders.add("has_not_started");
        folders.add("has_not_started_with_end_date");
        folders.add("multiple_tests");
        folders.add("multiple_variants");
        folders.add("no_buckets");
        folders.add("no_end_date");
        folders.add("no_variants");
        folders.add("zero_buckets");
        folders.add("zero_weight");
    }

    @Test
    public void testAssignedTests() {
        for (String set : folders) {
            TestConfiguration input = read(TestConfiguration.class, String.format("/%s/input.json", set));
            TestOutput output = read(TestOutput.class, String.format("/%s/output.json", set));

            for (Entry<String, JsonElement> element : output.variants.entrySet()) {
                String variant = element.getKey();

                if (variant.equals("")) {
                    for (String identifier : output.get(variant)) {
                        assertTrue(format(set, identifier), input.getAssignedTestVariantsForTest(identifier, output.testName).isEmpty());
                    }
                } else {
                    for (String identifier : output.get(variant)) {
                        List<AbTestVariant> assignedTestVariants = input.getAssignedTestVariantsForTest(identifier, output.testName);

                        for (AbTestVariant test : assignedTestVariants) {
                            assertEquals(format(set, identifier, variant, test.getAbTest()), variant, test.getName());
                        }
                    }
                }
            }
        }
    }

    private String format(String set, String identifier, String variant, AbTest abTest) {
        return String.format("Test set: [%s], identifier: [%s], variant: [%s], %s", set, identifier, variant, abTest);
    }

    private String format(String set, String identifier) {
        return String.format("Test set: [%s], identifier: [%s]", set, identifier);
    }

    private <T> T read(Class<T> clazz, String path) {
        Gson gson = new GsonBuilder().create();

        Reader reader = null;
        try {
            reader = new InputStreamReader(TestConfigurationTest.class.getResourceAsStream(String.format(path, path)));
            return gson.fromJson(reader, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}