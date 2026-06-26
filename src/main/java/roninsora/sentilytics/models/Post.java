package roninsora.sentilytics.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post {

    private String platform;
    private String postId;
    private String author;
    private String content;
    private LocalDateTime createdAt;
}
