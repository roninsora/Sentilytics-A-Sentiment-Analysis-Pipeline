import org.springframework.web.client.RestClient;
import java.util.Map;

public class TestAnalyzer {
    public static void main(String[] args) {
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:8000/analyze").build();
        try {
            Map response = restClient.post()
                    .body(Map.of("text", "I hate this product!"))
                    .retrieve()
                    .body(Map.class);
            System.out.println(response);
            
            String label = (String) response.get("sentiment");
            Number scoreNum = (Number) response.get("score");
            double score = scoreNum != null ? scoreNum.doubleValue() : 0.0;
            if ("NEGATIVE".equalsIgnoreCase(label)) {
                score *= -1;
            }
            System.out.println("Label: " + label + ", Score: " + score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
