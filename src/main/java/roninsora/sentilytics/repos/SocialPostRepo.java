package roninsora.sentilytics.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import roninsora.sentilytics.models.entities.SocialPost;

import java.util.UUID;

public interface SocialPostRepo extends JpaRepository<SocialPost, UUID> {
}
