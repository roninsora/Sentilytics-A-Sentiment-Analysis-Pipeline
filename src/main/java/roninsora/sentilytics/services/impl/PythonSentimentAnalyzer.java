package roninsora.sentilytics.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roninsora.sentilytics.models.SentimentResult;
import roninsora.sentilytics.services.SentimentAnalyzer;

import java.util.Map;

@Service
public class PythonSentimentAnalyzer implements SentimentAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(PythonSentimentAnalyzer.class);

    private final RestClient restClient;

    public PythonSentimentAnalyzer(@Value("${python.ml.api.url:http://localhost:5000}") String apiUrl) {
        this.restClient = RestClient.builder().baseUrl(apiUrl).build();
        log.info("Initialized Python ML Sentiment Analyzer pointing to {}", apiUrl);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SentimentResult analyze(String text) {
        if(text==null || text.isBlank()){
            return new SentimentResult("NEUTRAL", 0.0);
        }

        try{
            Map<String, Object> response = restClient.post()
                    .uri("")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(Map.of("text", text))
                    .retrieve()
                    .body(Map.class);

            if(response!=null && response.containsKey("sentiment") && response.containsKey("score")){
                String label = (String) response.get("sentiment");
                Number scoreNum = (Number) response.get("score");
                double score = scoreNum != null ? scoreNum.doubleValue() : 0.0;
                return new SentimentResult(label, score);
            }
        }catch (Exception e) {
            log.error("Failed to analyze sentiment using Python ML API. Is the python server running?", e);
        }

        return new SentimentResult("NEUTRAL", 0.0);
    }
}