package com.example.demo.endpoint.rest.controller;

import static java.util.regex.Pattern.compile;

import com.example.demo.endpoint.rest.model.ErrorResponse;
import com.example.demo.endpoint.rest.model.SubmissionView;
import com.example.demo.repository.Submission;
import com.example.demo.service.SubmissionService;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class SubmissionController {
  private static final Pattern EMAIL_PATTERN = compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private final SubmissionService service;

  @PostMapping("/submissions")
  public ResponseEntity<?> createSubmission(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
    if (file == null || file.isEmpty()) {
      return badRequest("Le fichier est obligatoire");
    }
    if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
      return badRequest("Email invalide");
    }
    Submission submission = service.create(email);
    return ResponseEntity.status(HttpStatus.CREATED).body(toView(submission));
  }

  @GetMapping("/submissions")
  public List<SubmissionView> listSubmissions() {
    return service.list().stream().map(this::toView).toList();
  }

  private SubmissionView toView(Submission submission) {
    return new SubmissionView(
        submission.getId(),
        submission.getEmail(),
        submission.getThumbnailKey(),
        submission.getCreatedAt());
  }

  private ResponseEntity<ErrorResponse> badRequest(String message) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
  }
}
