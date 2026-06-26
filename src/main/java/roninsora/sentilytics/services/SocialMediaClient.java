package roninsora.sentilytics.services;

import roninsora.sentilytics.models.Post;

import java.util.List;

public interface SocialMediaClient {

    List<Post> fetchPosts(String keyword, int limit);
    String getPlatformName();
}
