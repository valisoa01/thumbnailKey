package com.example.demo.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "submissions")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
  @Id private String id;
  private String email;
  private String thumbnailKey;
  private String originalKey;
  private Instant createdAt;
}
