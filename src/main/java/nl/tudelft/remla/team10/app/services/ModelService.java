package nl.tudelft.remla.team10.app.services;

import io.micrometer.core.instrument.*;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.Gauge;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.remla.team10.app.dto.Feedback;
import nl.tudelft.remla.team10.app.dto.Review;
import nl.tudelft.remla.team10.app.dto.Sentiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ModelService {

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
    private Timer timerPositive;
    private Timer timerNegative;

    @Autowired
    public ModelService(PrometheusMeterRegistry registry) {
        this.registry = registry;
        Metrics.globalRegistry.add(registry);

        // Timers (histograms) labeled with the sentiment of the prediction
        // to check if the model takes longer to predict negative or positive reviews
        // Timer for model service response times for positive predictions
        this.timerPositive = Timer
                .builder("model_service_response_time")
                .description("Response time of the model service")
                .tags("prediction", "positive")
                .publishPercentileHistogram()
                .register(registry);

        // Timer for model service response times for negative predictions
        this.timerNegative = Timer
                .builder("model_service_response_time")
                .description("Response time of the model service")
                .tags("prediction", "negative")
                .publishPercentileHistogram()
                .register(registry);

        // Register accuracy gauge
        Metrics.globalRegistry.gauge("accuracy",confusionMatrix, this::getAccuracy);

        // Register word count summary, labeled with the sentiment of the prediction and correctness of the prediction
        // With this, we can check if the model is biased for short or long reviews
        for(String sentiment: SENTIMENTS){
            for(String correct: CORRECT){
                DistributionSummary.builder(REVIEW_SIZE)
                        .description("The size of requests sent to the sentiment service")
                        .tags("prediction", sentiment, "correct", correct)
                        .publishPercentiles(0.5, 0.75, 0.95)
                        .register(registry);
            }
        }
    }

    public Sentiment getSentiment(Review review) throws Exception {
        log.info(review.toString());
        Timer.Sample sample = Timer.start();

        RestTemplate client = new RestTemplate();
        String url = modelServiceUrl + "/predict";
        log.info("Request to " + url);

        HttpEntity<Review> request = new HttpEntity<>(review);
        try {
            Sentiment sentiment = client.postForObject(url, request, Sentiment.class);
            if (sentiment.getSentiment().equals("positive"))
                sample.stop(timerPositive);
            else
                sample.stop(timerNegative);
            return sentiment;
        } catch (Exception e) {
            log.error("Error while calling model service", e);
            throw new Exception(e);
        }
    }

    public void processFeedback(@RequestBody Feedback feedback){
        int words = feedback.getReview().split(" ").length;
        DistributionSummary summary = registry.summary(REVIEW_SIZE,"prediction", feedback.getSentiment(), "correct", feedback.getCorrect().toString());
        summary.record(words);

        Integer predicted = feedback.getSentiment().equals("positive") ? 1 : 0;
        Integer real = feedback.getCorrect() && predicted.equals(1) ||
                !feedback.getCorrect() && predicted.equals(0) ? 1 : 0;

        confusionMatrix[predicted][real]++;

        Metrics.counter("final_predictions","sentiment", feedback.getSentiment(), "correct", feedback.getCorrect().toString()).increment();
    }

    private double getAccuracy(Integer[][] confusionMatrix){
        double correct = confusionMatrix[0][0] + confusionMatrix[1][1];
        double total = confusionMatrix[0][0] + confusionMatrix[0][1] + confusionMatrix[1][0] + confusionMatrix[1][1];
        return total != 0 ? correct / total : 0;
    }
}
