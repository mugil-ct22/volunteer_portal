package com.volunteer.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public class EventDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    @NotNull(message = "Points are required")
    @Positive(message = "Points must be positive")
    private Integer points;

    @NotNull(message = "Max volunteers is required")
    @Positive(message = "Max volunteers must be positive")
    private Integer maxVolunteers;

    private String category;

    private LocalDateTime createdAt;

    private Integer registeredVolunteers;

    public EventDto() {}

    public EventDto(Long id, String title, String description, LocalDateTime eventDate, Integer points, Integer maxVolunteers, LocalDateTime createdAt, Integer registeredVolunteers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.points = points;
        this.maxVolunteers = maxVolunteers;
        this.createdAt = createdAt;
        this.registeredVolunteers = registeredVolunteers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getMaxVolunteers() {
        return maxVolunteers;
    }

    public void setMaxVolunteers(Integer maxVolunteers) {
        this.maxVolunteers = maxVolunteers;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getRegisteredVolunteers() {
        return registeredVolunteers;
    }

    public void setRegisteredVolunteers(Integer registeredVolunteers) {
        this.registeredVolunteers = registeredVolunteers;
    }
}
