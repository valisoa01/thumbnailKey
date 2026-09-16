package com.example.demo.endpoint.event.model;

import java.time.Duration;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubmissionCreatedEvent extends PojaEvent {
  private String submissionId;
  private String originalKey;

  public SubmissionCreatedEvent(String submissionId, String originalKey) {
    this.submissionId = submissionId;
    this.originalKey = originalKey;
  }

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(60);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(180);
  }
}
