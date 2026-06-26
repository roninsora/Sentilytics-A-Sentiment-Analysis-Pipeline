package roninsora.sentilytics.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnalyzePostDTO {

    @NotBlank(message = "Text needed to analyze")
    @Size(max = 5000, message = "Text exceeds the maximum allowed length of 5000 characters")
    private String text;
}
