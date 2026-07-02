package roninsora.sentilytics.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import roninsora.sentilytics.mapper.Mapper;
import roninsora.sentilytics.models.Dashboard;
import roninsora.sentilytics.models.SentimentResult;
import roninsora.sentilytics.models.Stats;
import roninsora.sentilytics.models.dtos.AnalyzePostDTO;
import roninsora.sentilytics.models.dtos.SocialPostDTO;
import roninsora.sentilytics.models.entities.AnalyzePost;
import roninsora.sentilytics.models.entities.SocialPost;
import roninsora.sentilytics.repos.AnalyzePostRepo;
import roninsora.sentilytics.services.AnalyticsService;
import roninsora.sentilytics.services.SentimentAnalyzer;
import roninsora.sentilytics.services.SentimentPipelineService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/")
@Validated
public class SentimentController {

    private final Mapper<AnalyzePost, AnalyzePostDTO> analyzePostMapper;
    private final Mapper<SocialPost, SocialPostDTO> socialPostMapper;
    private final AnalyticsService analyticsService;
    private final SentimentAnalyzer sentimentAnalyzer;
    private final SentimentPipelineService sentimentPipelineService;
    private final AnalyzePostRepo analyzePostRepo;


    @Value("${sentiment.posts-per-fetch:5}")
    private int postsPerFetch;

    @Value("${sentiment.keywords:Tesla,Apple,Google}")
    private List<String> keywords;

    public SentimentController(Mapper<AnalyzePost, AnalyzePostDTO> mapper,
                               Mapper<SocialPost, SocialPostDTO> socialPostMapper,
                               AnalyticsService analyticsService,
                               SentimentAnalyzer sentimentAnalyzer,
                               SentimentPipelineService sentimentPipelineService, AnalyzePostRepo analyzePostRepo){
        this.analyzePostMapper = mapper;
        this.socialPostMapper = socialPostMapper;
        this.analyticsService = analyticsService;
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.sentimentPipelineService = sentimentPipelineService;
        this.analyzePostRepo = analyzePostRepo;
    }


    //Hourly sentiment breakdown
    @GetMapping("/sentiment/hourly")
    public ResponseEntity<List<Dashboard>> getHourlySummary(@RequestParam(defaultValue = "all") String keyword,
                                                            @RequestParam(defaultValue = "1") @Min(1) int days){
        return ResponseEntity.ok(analyticsService.getHourlySummary(keyword, days));
    }


    //Daily sentiment breakdown
    @GetMapping("/sentiment/daily")
    public ResponseEntity<List<Dashboard>> getDailySummary(@RequestParam(defaultValue = "all") String keyword,
                                                            @RequestParam(defaultValue = "7") @Min(1) int days){
        return ResponseEntity.ok(analyticsService.getDailySummary(keyword, days));
    }

    //Recent analyze post
    @GetMapping("/sentiment/recent")
    public ResponseEntity<List<SocialPostDTO>> getRecentPost(@RequestParam(defaultValue = "all") String keyword,
                                                             @RequestParam(defaultValue = "50") @Min(1) int limit){
        List<SocialPost> recentPost = analyticsService.getRecentPost(keyword, limit);
        return ResponseEntity.ok(recentPost.stream().map(socialPostMapper::mapTo).collect(Collectors.toList()));
    }

    //Overall Stats
    @GetMapping("/sentiment/overall")
    public ResponseEntity<Stats> getStats(@RequestParam(defaultValue = "all") String keyword){
        return ResponseEntity.ok(analyticsService.getOverallStats(keyword));
    }


    //Manually analyze custom text.
    //Body: { "text": "I love this product!"}
    @PostMapping("/analyze")
    public ResponseEntity<SentimentResult> analyzeText(@Valid @RequestBody AnalyzePostDTO analyzePostDTO){
        AnalyzePost analyzePost = analyzePostMapper.mapFrom(analyzePostDTO);

        SentimentResult res = sentimentAnalyzer.analyze(analyzePost.getText());
        analyzePost.setSentiment(res.getLabel());
        analyzePost.setScore(res.getScore());
        analyzePost.setAnalyzeAt(LocalDateTime.now());
        analyzePostRepo.save(analyzePost);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/ingest/trigger")
    public ResponseEntity<Map<String, Object>> trigger(@RequestParam(defaultValue = "all") String keyword){
        int count = sentimentPipelineService.fetchAndAnalyze(keyword, postsPerFetch);

        return ResponseEntity.ok(Map.of(
                "keyword", keyword,
                "newPosts", count,
                "message", "Complete"
        ));
    }
}
