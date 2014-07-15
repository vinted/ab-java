package com.vinted.ab;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

public class AbTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @SerializedName("name")
    private String name;

    /**
     * Buckets assigned for this tests
     */
    @SerializedName("buckets")
    private Set<Integer> buckets;

    /**
     * Flag when all buckets receive this test. No need to check `buckets` then
     */
    @SerializedName("all_buckets")
    private boolean allBuckets;

    /**
     * the start date time for ab test, in ISO 8601 format. Is not required, in which case, test has already started.
     */
    @SerializedName("start_at")
    private String startAt;

    /**
     * the end date time for ab test, in ISO 8601 format. Is not required, in which case, there's no predetermined date when test will end.
     */
    @SerializedName("end_at")
    private String endAt;

    @SerializedName("seed")
    private String seed;

    @SerializedName("variants")
    private List<AbTestVariant> variants;

    public AbTest() {
    }

    public AbTest(String name, Set<Integer> buckets, boolean allBuckets, String startAt, String endAt, String seed, List<AbTestVariant> variants) {
        this.name = name;
        this.buckets = buckets;
        this.allBuckets = allBuckets;
        this.startAt = startAt;
        this.endAt = endAt;
        this.seed = seed;
        this.variants = variants;
    }

    public boolean isAllBuckets() {
        return allBuckets;
    }

    public AbTestVariant getAssignedVariant(String identifier) {
        String hex = new String(Hex.encodeHex(DigestUtils.sha256(seed + identifier)));
        int variantId = new BigInteger(hex, 16).mod(BigInteger.valueOf(positiveWeightSum())).intValue();

        int sum = 0;
        for (AbTestVariant variant : getVariants()) {
            sum += variant.getChanceWeight();
            if (sum > variantId) {
                return variant;
            }
        }

        return null;
    }

    private int positiveWeightSum() {
        int sum = 0;
        for (AbTestVariant variant : getVariants()) {
            sum += variant.getChanceWeight();
        }

        return sum > 0 ? sum : 1;
    }

    public boolean isRunning() {
        if (startAt == null && endAt == null) {
            return true;
        }

        Date now = new Date();
        Date startDate = parseDate(startAt);
        Date endDate = parseDate(endAt);

        if (endDate != null) {
            return now.before(endDate);
        }

        if (startDate != null) {
            return now.after(startDate);
        }

        return now.before(endDate) && now.after(startDate);
    }

    private Date parseDate(String date) {
        Date result = null;

        if (date != null) {
            try {
                result = DATE_FORMAT.parse(date);
            } catch (Exception ignored) {
            }
        }

        return result;
    }

    public List<AbTestVariant> getVariants() {
        for (AbTestVariant variant : variants) {
            variant.setAbTest(this);
        }

        return variants;
    }

    public Set<Integer> getBuckets() {
        if (buckets != null) {
            return new HashSet<>(buckets);
        }

        return new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getStartAt() {
        return startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbTest{");
        sb.append("name='").append(name).append('\'');
        sb.append(", buckets=").append(buckets);
        sb.append(", allBuckets=").append(allBuckets);
        sb.append(", startAt='").append(startAt).append('\'');
        sb.append(", endAt='").append(endAt).append('\'');
        sb.append(", seed='").append(seed).append('\'');
        sb.append('}').toString();
        return sb.toString();
    }
}