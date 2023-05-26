package nl.tudelft.remla.team10.app.controllers;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.remla.team10.app.dto.Feedback;
import nl.tudelft.remla.team10.app.dto.Review;
import nl.tudelft.remla.team10.app.dto.Sentiment;
import nl.tudelft.remla.team10.app.services.ModelService;
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
    private ModelService modelService;
    @Autowired
    public SentimentController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("")
    public ResponseEntity<?> getSentiment(@RequestBody Review review){
        try {
            Sentiment sentiment = modelService.getSentiment(review);
            // Count number of request to this endpoint, and label with the sentiment
            Metrics.counter("num_requests","status", "200", "route", "prediction").increment();
            return new ResponseEntity<>(sentiment, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error while calling model service: ", e);
            Metrics.counter("num_requests","status", "500", "route", "prediction").increment();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> getFeedback(@RequestBody Feedback feedback){
        try {
            modelService.processFeedback(feedback);
            // Count number of request to this endpoint, and label with correctness
            Metrics.counter("num_requests","status", "200", "route", "feedback").increment();
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error processing feedback: " + e);
            Metrics.counter("num_requests","status", "500", "route", "feedback").increment();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(){
        try {
            return new ResponseEntity<>(modelService.feedbackHistory, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting feedback history: " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
