package roninsora.sentilytics.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import roninsora.sentilytics.mapper.Mapper;
import roninsora.sentilytics.models.Dashboard;
import roninsora.sentilytics.models.Stats;
import roninsora.sentilytics.models.dtos.SocialPostDTO;
import roninsora.sentilytics.models.entities.SocialPost;
import roninsora.sentilytics.repos.SocialPostRepo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final SocialPostRepo socialPostRepo;

    public AnalyticsService(SocialPostRepo socialPostRepo){
        this.socialPostRepo = socialPostRepo;
    }

    public List<Dashboard> getHourlySummary(String keyword, int days){
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<SocialPost> posts;

        if (keyword == null || keyword.isBlank() || keyword.equals("all")) {
            posts = socialPostRepo.findByAnalyzedAtAfterOrderByAnalyzedAtAsc(since);
        } else {
            posts = socialPostRepo.findByKeywordAndAnalyzedAtAfterOrderByAnalyzedAtAsc(keyword, since);
        }

        return aggregatedByTime(posts, ChronoUnit.HOURS);
    }

    public List<Dashboard> getDailySummary(String keyword, int days){
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<SocialPost> posts;

        if(keyword==null || keyword.isBlank() || keyword.equals("all")){
            posts = socialPostRepo.findByAnalyzedAtAfterOrderByAnalyzedAtAsc(since);
        }else{
            posts = socialPostRepo.findByKeywordAndAnalyzedAtAfterOrderByAnalyzedAtAsc(keyword, since);
        }

        return aggregatedByTime(posts, ChronoUnit.DAYS);
    }


    public List<SocialPost> getRecentPost(String keyword, int limit){
        List<SocialPost> posts;

        if (keyword == null || keyword.isBlank() || keyword.equals("all")) {
            posts = socialPostRepo.findAllByOrderByAnalyzedAtDesc(PageRequest.of(0, limit));
        } else {
            posts = socialPostRepo.findByKeywordOrderByAnalyzedAtDesc(keyword, PageRequest.of(0, limit));
        }
        return posts;
    }


    public Stats getOverallStats(String keyword){
        Stats stats = new Stats();
        stats.setKeyword(keyword==null || keyword.isBlank() ? "all" : keyword);

        long total, pos, neg, neu;
        double avg;

        if(keyword==null || keyword.isBlank() || keyword.equals("all")){
            total = socialPostRepo.countAllPost();

            List<SocialPost> allPosts = socialPostRepo.findAll();
            pos = allPosts.stream().filter(p -> "POSITIVE".equals(p.getSentiment())).count();
            neg = allPosts.stream().filter(p -> "NEGATIVE".equals(p.getSentiment())).count();
            neu = allPosts.stream().filter(p -> "NEUTRAL".equals(p.getSentiment())).count();
            avg = allPosts.stream().mapToDouble(SocialPost::getScore).average().orElse(0.0);
        }else{
            total = socialPostRepo.countByKeyword(keyword);
            pos = socialPostRepo.countByKeywordAndSentiment(keyword, "POSITIVE");
            neg = socialPostRepo.countByKeywordAndSentiment(keyword, "NEGATIVE");
            neu = socialPostRepo.countByKeywordAndSentiment(keyword, "NEUTRAL");
            avg = socialPostRepo.averageScoreByKeyword(keyword);
        }

        stats.setTotalPosts(total);
        stats.setPositiveCount(pos);
        stats.setNegativeCount(neg);
        stats.setNeutralCount(neu);
        stats.setAverage(avg);

        stats.setPositivePercentage(total > 0 ? Math.round((pos * 100.0 / total) * 10.0) / 10.0 : 0);
        stats.setNegativePercentage(total > 0 ? Math.round((neg * 100.0 / total) * 10.0) / 10.0 : 0);
        stats.setNeutralPercentage(total > 0 ? Math.round((neu * 100.0 / total) * 10.0) / 10.0 : 0);

        return stats;
    }

    public List<Dashboard> aggregatedByTime(List<SocialPost> posts, ChronoUnit chronoUnit) {
        DateTimeFormatter formatter = chronoUnit==ChronoUnit.HOURS
                ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH::00")
                : DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, List<SocialPost>> grouped = posts.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getAnalyzedAt()
                                .truncatedTo(chronoUnit)
                                .format(formatter),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<Dashboard> summaries = new ArrayList<>();
        for(Map.Entry<String, List<SocialPost>> entry: grouped.entrySet()){
            List<SocialPost> list = entry.getValue();

            long pos = list.stream().filter(p -> "POSITIVE".equals(p.getSentiment())).count();
            long neg = list.stream().filter(p -> "NEGATIVE".equals(p.getSentiment())).count();
            long neu = list.stream().filter(p -> "NEUTRAL".equals(p.getSentiment())).count();

            double avg = list.stream().mapToDouble(SocialPost::getScore).average().orElse(0);

            summaries.add(new Dashboard(entry.getKey(), pos, neg, neu, Math.round(avg*1000.0)/1000.0));
        }

        return summaries;
    }
}
