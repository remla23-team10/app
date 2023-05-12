package nl.tudelft.remla.team10.app.controllers;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.remla.team10.app.dto.Feedback;
import nl.tudelft.remla.team10.app.dto.Review;
import nl.tudelft.remla.team10.app.dto.Sentiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "/**")
@RestController
@RequestMapping("/sentiment")
@Slf4j
public class SentimentController {

    private static final String REVIEW_SIZE="review_size";
    private static final String[] SENTIMENTS = {"positive", "negative"};
    private static final String[] CORRECT = {"true", "false","unknown"};
    @Value("${model-service.url}")
    private String modelServiceUrl;

    // Cumulative confusion matrix
    // First index is the actual sentiment, second index is the predicted sentiment
    // e.g. confusionMatrix[0][1] is the number of false positives
    private Integer[][] confusionMatrix = {{0,0},{0,0}};
    private MeterRegistry registry;

    @Autowired
    public SentimentController(PrometheusMeterRegistry registry) {
        this.registry = registry;

        for(String sentiment: SENTIMENTS){
            for(String correct: CORRECT){
                DistributionSummary.builder(REVIEW_SIZE)
                        .description("The size of requests sent to the sentiment service")
                        .tags("prediction", sentiment, "correct", correct)
                        .publishPercentiles(0.5, 0.75, 0.95)
                        .register(registry);
            }
        }

        Metrics.globalRegistry.add(registry);
    }

    @PostMapping("")
    public ResponseEntity<?> getSentiment(@RequestBody Review review){
        log.info(review.toString());

        RestTemplate client = new RestTemplate();
        String url = modelServiceUrl + "/predict";
        log.info("Request to " + url);

        HttpEntity<Review> request = new HttpEntity<>(review);
        try {
            Sentiment sentiment = client.postForObject(url, request, Sentiment.class);
            return new ResponseEntity<>(sentiment, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while calling model service", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> getFeedback(@RequestBody Feedback feedback){
        int words = feedback.getReview().split(" ").length;
        DistributionSummary summary = registry.summary(REVIEW_SIZE,"prediction", feedback.getSentiment(), "correct", feedback.getCorrect().toString());
        summary.record(words);

        Integer predicted = feedback.getSentiment().equals("positive") ? 1 : 0;
        Integer real = feedback.getCorrect() && predicted.equals(1) ||
                !feedback.getCorrect() && predicted.equals(0) ? 1 : 0;

        confusionMatrix[predicted][real]++;
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
