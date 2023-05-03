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

    @Value("${model-service.host}")
    private String modelServiceHost;

    @PostMapping("")
    public Sentiment getSentiment(Review review){
        log.info(review.toString());

        RestTemplate client = new RestTemplate();

        String url = "http://" + modelServiceHost;
        log.info("Request to " + url);

        // TODO: Implement request to model-service once it is done
//        HttpEntity<Review> request = new HttpEntity<>(review);
//        Sentiment sentiment = client.postForObject(url,request,Sentiment.class);

        Sentiment sentiment = new Sentiment();
        sentiment.setSentiment("positive");
        return sentiment;
    }
}
