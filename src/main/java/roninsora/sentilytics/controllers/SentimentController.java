package roninsora.sentilytics.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roninsora.sentilytics.models.entities.AnalyzePost;

@RestController
@RequestMapping("api/v1/")
public class SentimentController {

    private AnalyzePost analyzePost;

}
