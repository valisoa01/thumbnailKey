package com.example.demo.service;

import com.example.demo.repository.Submission;
import com.example.demo.repository.SubmissionRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SubmissionService {
  private final SubmissionRepository repository;

  public Submission create(String email) {
    return repository.save(
        Submission.builder()
            .id(UUID.randomUUID().toString())
            .email(email)
            .createdAt(Instant.now())
            .build());
  }

  public List<Submission> list() {
    return repository.findAll();
  }
}
