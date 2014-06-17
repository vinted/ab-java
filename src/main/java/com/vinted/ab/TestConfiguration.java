package com.vinted.ab;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TestConfiguration {

    @SerializedName("salt")
    private String salt;

    @SerializedName("bucket_count")
    private int bucketCount;

    @SerializedName("ab_tests")
    private List<AbTest> abTests;

    public TestConfiguration() {
    }

    public TestConfiguration(String salt, int bucketCount, List<AbTest> abTests) {
        this.salt = salt;
        this.bucketCount = bucketCount;
        this.abTests = abTests;
    }

    /**
     * @param identifier user identifier
     * @return List of running tests assigned to user
     */
    public List<AbTest> getRunningTests(String identifier) {
        String hex = DigestUtils.sha256Hex(salt + identifier);
        BigInteger bucketId = new BigInteger(hex, 16).mod(BigInteger.valueOf(bucketCount));

        List<AbTest> tests = new ArrayList<>();
        for (AbTest test : abTests) {
            if ((test.isAllBuckets() || test.getBuckets().contains(bucketId.intValue())) && test.isRunning()) {
                tests.add(test);
            }
        }

        return tests;
    }

    /**
     * Return all variants for given test name if any.
     * Will check among all running tests for given identifier with this test name.
     *
     * @param identifier user identifier
     * @param testName   test name
     * @return List of {@link com.vinted.ab.AbTestVariant} if has any. Empty list otherwise.
     */
    public List<AbTestVariant> getAssignedTestVariantsForTest(String identifier, String testName) {
        List<AbTestVariant> result = new ArrayList<>();

        for (AbTest test : getRunningTests(identifier)) {
            if (test.getName().equals(testName)) {
                AbTestVariant variant = test.getAssignedVariant(identifier);
                if (variant != null) {
                    result.add(variant);
                }
            }
        }

        return result;
    }
}
