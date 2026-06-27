package roninsora.sentilytics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roninsora.sentilytics.services.impl.PythonSentimentAnalyzer;

@SpringBootTest
class SentilyticsApplicationTests {

    @Autowired
    PythonSentimentAnalyzer analyzer;

    @Test
    void testAnalyzer() {
        System.out.println("RESULT: " + analyzer.analyze("I hate this product!"));
    }
}
