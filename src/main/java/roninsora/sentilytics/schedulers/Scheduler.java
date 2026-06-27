package roninsora.sentilytics.schedulers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import roninsora.sentilytics.services.SentimentPipelineService;

import java.util.List;

@Component
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private final SentimentPipelineService sentiment;

    @Value("${sentiment.keywords:Tesla,Apple,Google}")
    private List<String> keywords;

    @Value("${sentiment.posts-per-fetch:5}")
    private int postPerFetch;

    public Scheduler(SentimentPipelineService sentiment){
        this.sentiment = sentiment;
    }

    @Scheduled(fixedDelayString = "${sentiment.fetch-interval:6000}")
    public void scheduleIngestion(){
        log.info("Ingestion started for keywords: {}", keywords);

        int total = 0;
        for(String keyword: keywords){
            total+=sentiment.fetchAndAnalyze(keyword.trim(), postPerFetch);
        }

        log.info("Completed, total new post {}", total);
    }
}
