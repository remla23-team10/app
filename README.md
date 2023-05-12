# Restaurant review sentiment app
Spring application that serves a frontend for a restaurant review sentiment analysis service. 
The Spring app relies on a model-service REST API to query a Machine Learning model for sentiment analysis.

The app also offers the user the opportunity to provide feedback on the sentiment analysis results,
which is then used to calculate accuracy metrics.

## Monitoring
The app exposes a Prometheus endpoint at `/actuator/prometheus` with some custom metrics to measure usage and user interactions.

- `num_requests` - Counter: Number of requests received by the app, with the following labels:
  - `status`: `200` or `500`
  - `path`: `sentiment` or `feedback`

- `accuracy` - Gauge: Accuracy of the sentiment analysis model, based on feedback received.

- `model_service_response_time` - Histogram: Response time of the model-service, 
with labels to monitor if a particular prediction is slower than the others.
  - `sentiment`: `positive` or `negative`

- `review_size` - Summary: Size of the review text, in words, to monitor if there is some bias with respect to the length of the review.
To monitor accuracy and bias, the following labels are used:
  - `sentiment`: `positive` or `negative`
  - `correct`: `true` or `false`