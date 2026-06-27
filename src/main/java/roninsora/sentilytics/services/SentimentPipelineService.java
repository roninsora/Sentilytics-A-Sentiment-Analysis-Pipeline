package roninsora.sentilytics.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import roninsora.sentilytics.models.Post;
import roninsora.sentilytics.models.SentimentResult;
import roninsora.sentilytics.models.entities.SocialPost;
import roninsora.sentilytics.repos.SocialPostRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SentimentPipelineService {

    private static final Logger log = LoggerFactory.getLogger(SentimentPipelineService.class);

    private final List<SocialMediaClient> socialMediaClients;
    private final SentimentAnalyzer sentimentAnalyzer;
    private final SocialPostRepo socialPostRepo;

    public SentimentPipelineService(List<SocialMediaClient> socialMediaClients,
                                    SentimentAnalyzer sentimentAnalyzer,
                                    SocialPostRepo socialPostRepo){
        this.socialMediaClients = socialMediaClients;
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.socialPostRepo = socialPostRepo;
    }

    public int fetchAndAnalyze(String keyword, int limit){
        int totalFetched = 0;

        for(SocialMediaClient client: socialMediaClients){
            try{
                List<Post> posts = client.fetchPosts(keyword, limit);

                log.debug("Fetch {} posts from {} fro keywords '{}'",
                        posts.size(), client.getPlatformName(), keyword);

                for(Post post: posts){
                    if(socialPostRepo.existsByPostId(post.getPostId())){
                        continue;
                    }

                    SentimentResult result = sentimentAnalyzer.analyze(post.getContent());

                    SocialPost p = SocialPost.builder()
                            .platform(post.getPlatform())
                            .postId(post.getPostId())
                            .author(post.getAuthor())
                            .content(post.getContent())
                            .sentiment(result.getLabel())
                            .score(result.getScore())
                            .keyword(keyword)
                            .createdAt(post.getCreatedAt())
                            .analyzedAt(LocalDateTime.now())
                            .build();

                    socialPostRepo.save(p);
                    totalFetched++;
                }
            }catch (Exception e){
                log.error("Error ingesting post from {} fro keyword '{}'",
                        client.getPlatformName(), keyword, e);
            }
        }
        return totalFetched;
    }
}
