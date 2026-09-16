package com.example.demo.service;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SubmissionCreatedEvent;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.repository.Submission;
import com.example.demo.repository.SubmissionRepository;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class SubmissionService {
  private final SubmissionRepository repository;
  private final BucketComponent bucketComponent;
  private final EventProducer<SubmissionCreatedEvent> eventProducer;

  @SneakyThrows
  public Submission create(String email, MultipartFile file) {
    Submission submission =
        repository.save(
            Submission.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .createdAt(Instant.now())
                .build());

    String fileKey = "submissions/" + submission.getId() + "/" + file.getOriginalFilename();
    File tempFile = toTempFile(file);
    bucketComponent.upload(tempFile, fileKey);

    submission.setOriginalKey(fileKey);
    repository.save(submission);

    eventProducer.accept(List.of(new SubmissionCreatedEvent(submission.getId(), fileKey)));
    return submission;
  }

  public List<Submission> list() {
    return repository.findAll();
  }

  @SneakyThrows
  private File toTempFile(MultipartFile file) {
    File temp = File.createTempFile("submission-", file.getOriginalFilename());
    file.transferTo(temp);
    return temp;
  }
}
