package roninsora.sentilytics.models.dtos;

import jakarta.persistence.Column;
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
public class SocialPostDTO {

    private UUID id;

    private String platform;
    private String postId;
    private String author;
    private String content;
    private String sentiment;
    private String keyword;
    private LocalDateTime createdAt;
    private LocalDateTime analyzedAt;

    /*
    * Right now, my project is only outgoing
    * for this case we do not need validation rules
    * here, because data is coming out from my db not
    * going in. If it was incoming i definitely needed the
    * validation here in dto's too
    * */
}
