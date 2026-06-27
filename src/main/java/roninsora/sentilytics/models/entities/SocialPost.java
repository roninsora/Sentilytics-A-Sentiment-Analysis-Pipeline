package roninsora.sentilytics.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class SocialPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "platform cannot be blank")
    private String platform;

    @Column(nullable = false,
            unique = true
    )
    @NotBlank(message = "post id cannot be blank")
    private String postId;

    @NotBlank(message = "author name cannot be blank")
    private String author;

    @Column(columnDefinition = "TEXT",
            nullable = false
    )
    @NotBlank(message = "content can not be blank")
    private String content;

    @Column(nullable = false)
    private String sentiment;

    @Column(nullable = false)
    private double score;

    private String keyword;

    private LocalDateTime createdAt;
    private LocalDateTime analyzedAt;
}
