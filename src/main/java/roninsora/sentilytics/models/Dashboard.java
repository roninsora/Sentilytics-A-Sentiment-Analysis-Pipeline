package roninsora.sentilytics.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Dashboard {

    private String date;
    private long positiveCount;
    private long negativeCount;
    private long neutralCount;
    private double averageScore;
}
