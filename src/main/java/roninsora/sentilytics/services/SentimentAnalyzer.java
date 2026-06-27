package roninsora.sentilytics.services;

import roninsora.sentilytics.models.SentimentResult;

public interface SentimentAnalyzer {

    SentimentResult analyze(String text);
}
