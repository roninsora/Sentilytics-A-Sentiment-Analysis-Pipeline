package roninsora.sentilytics.repos;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roninsora.sentilytics.models.entities.SocialPost;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SocialPostRepo extends JpaRepository<SocialPost, UUID> {

    boolean existsByPostId(String postId);

    @Query("SELECT s FROM SocialPost s WHERE s.analyzedAt > :since ORDER BY s.analyzedAt ASC")
    List<SocialPost> findByAnalyzedAtAfterOrderByAnalyzedAtAsc(@Param("since") LocalDateTime since);

    @Query("SELECT s FROM SocialPost s WHERE s.keyword = :keyword AND s.analyzedAt > :since ORDER BY s.analyzedAt ASC")
    List<SocialPost> findByKeywordAndAnalyzedAtAfterOrderByAnalyzedAtAsc(@Param("keyword") String keyword,
                                                                         @Param("since") LocalDateTime since);

    @Query("SELECT count(s) FROM SocialPost s")
    long countAllPost();

    long countByKeyword(String keyword);

    long countByKeywordAndSentiment(String keyword, String positive);

    @Query("SELECT COALESCE(AVG(s.score), 0.0) FROM SocialPost s WHERE s.keyword = :keyword")
    double averageScoreByKeyword(@Param("keyword") String keyword);

    List<SocialPost> findAllByOrderByAnalyzedAtDesc(PageRequest of);

    List<SocialPost> findByKeywordOrderByAnalyzedAtDesc(String keyword, PageRequest of);
}