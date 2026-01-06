package com.volunteer.portal.dto;

public class LeaderboardDto {
    private Integer rank;
    private String name;
    private Integer points;

    public LeaderboardDto() {}

    public LeaderboardDto(Integer rank, String name, Integer points) {
        this.rank = rank;
        this.name = name;
        this.points = points;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
