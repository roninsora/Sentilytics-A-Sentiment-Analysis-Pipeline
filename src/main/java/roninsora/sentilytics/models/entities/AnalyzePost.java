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
public class AnalyzePost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(nullable = false)
    private String sentiment;

    @Column(nullable = false)
    private double score;

    @Column(nullable = false)
    private LocalDateTime analyzeAt;
}
