package roninsora.sentilytics.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import roninsora.sentilytics.models.entities.AnalyzePost;

import java.util.UUID;

public interface AnalyzePostRepo extends JpaRepository<AnalyzePost, UUID> {
}
