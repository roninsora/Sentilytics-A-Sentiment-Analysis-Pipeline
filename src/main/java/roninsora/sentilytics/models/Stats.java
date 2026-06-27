package roninsora.sentilytics.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Stats {
    private String keyword;
    private long totalPosts;

    private long positiveCount;
    private long negativeCount;
    private long neutralCount;
    private double average;

    private double positivePercentage;
    private double negativePercentage;
    private double neutralPercentage;
}
