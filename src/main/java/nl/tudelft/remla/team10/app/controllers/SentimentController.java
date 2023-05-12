package nl.tudelft.remla.team10.app.controllers;

import lombok.extern.slf4j.Slf4j;
import nl.tudelft.remla.team10.app.dto.Review;
import nl.tudelft.remla.team10.app.dto.Sentiment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "/**")
@RestController
@RequestMapping("/sentiment")
@Slf4j
public class SentimentController {

    @Value("${model-service.url}")
    private String modelServiceUrl;

    @PostMapping("")
    public Sentiment getSentiment(@RequestBody Review review){
        log.info(review.toString());

        RestTemplate client = new RestTemplate();
        String url = modelServiceUrl + "/predict";
        log.info("Request to " + url);

        HttpEntity<Review> request = new HttpEntity<>(review);
        Sentiment sentiment = client.postForObject(url,request,Sentiment.class);

        return sentiment;
    }
}
