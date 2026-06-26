package roninsora.sentilytics.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SentimentResult {

    private final String label; //Positive, Negative, Neutral
    private final double score; // -1.0 to 1.0

    @Override
    public String toString(){
        return "Sentiment Result: label: "+ label + "\nscore: " + score + '}';
    }
}
