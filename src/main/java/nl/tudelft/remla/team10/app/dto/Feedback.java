package nl.tudelft.remla.team10.app.dto;

import lombok.Data;

@Data
public class Feedback {
    private String review;
    private String sentiment;
    private Boolean correct;
}
