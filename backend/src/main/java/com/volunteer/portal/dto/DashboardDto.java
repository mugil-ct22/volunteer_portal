package com.volunteer.portal.dto;

public class DashboardDto {
    private Long totalEvents;
    private Long appliedEvents;
    private Long completedEvents;
    private Long rejectedEvents;
    private Integer totalPoints;
    private Long totalVolunteers;
    private Long pendingApprovals;

    public DashboardDto() {}

    public DashboardDto(Long totalEvents, Long appliedEvents, Long completedEvents, Long rejectedEvents, Integer totalPoints, Long totalVolunteers, Long pendingApprovals) {
        this.totalEvents = totalEvents;
        this.appliedEvents = appliedEvents;
        this.completedEvents = completedEvents;
        this.rejectedEvents = rejectedEvents;
        this.totalPoints = totalPoints;
        this.totalVolunteers = totalVolunteers;
        this.pendingApprovals = pendingApprovals;
    }

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Long getAppliedEvents() {
        return appliedEvents;
    }

    public void setAppliedEvents(Long appliedEvents) {
        this.appliedEvents = appliedEvents;
    }

    public Long getCompletedEvents() {
        return completedEvents;
    }

    public void setCompletedEvents(Long completedEvents) {
        this.completedEvents = completedEvents;
    }

    public Long getRejectedEvents() {
        return rejectedEvents;
    }

    public void setRejectedEvents(Long rejectedEvents) {
        this.rejectedEvents = rejectedEvents;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Long getTotalVolunteers() {
        return totalVolunteers;
    }

    public void setTotalVolunteers(Long totalVolunteers) {
        this.totalVolunteers = totalVolunteers;
    }

    public Long getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(Long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }
}
