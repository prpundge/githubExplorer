package com.github.explorer.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Github {
    @Id
    private String id;
    @JsonProperty("full_name")
    private String fullName;
    private String description;
    @JsonProperty("clone_url")
    private String cloneUrl;
    @JsonProperty("stargazers_count")
    private int stars;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

}