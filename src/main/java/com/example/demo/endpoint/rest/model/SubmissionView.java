package com.example.demo.endpoint.rest.model;

import java.time.Instant;

public record SubmissionView(String id, String email, String thumbnailKey, Instant createdAt) {}
