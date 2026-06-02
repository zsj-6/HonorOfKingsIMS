package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Team {
    private String id;
    private String name;
    private List<String> memberIds;
    private int totalMatches;
    private int wins;

    public Team() {
        this.memberIds = new ArrayList<>();
    }

    public Team(String id, String name) {
        setId(id);
        setName(name);
        this.memberIds = new ArrayList<>();
        this.totalMatches = 0;
        this.wins = 0;
    }

    public Team(String id, String name, int totalMatches, int wins) {
        setId(id);
        setName(name);
        this.memberIds = new ArrayList<>();
        setTotalMatches(totalMatches);
        setWins(wins);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be null or blank.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null or blank.");
        }
        this.name = name;
    }

    public List<String> getMemberIds() {
        return Collections.unmodifiableList(memberIds);
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds != null ? memberIds : new ArrayList<>();
    }

    public void addMember(String playerId) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or blank.");
        }
        if (!memberIds.contains(playerId)) {
            memberIds.add(playerId);
        }
    }

    public boolean removeMember(String playerId) {
        return memberIds.remove(playerId);
    }

    public boolean hasMember(String playerId) {
        return memberIds.contains(playerId);
    }

    public int getMemberCount() {
        return memberIds.size();
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        if (totalMatches < 0) {
            throw new IllegalArgumentException("Total matches cannot be negative.");
        }
        this.totalMatches = totalMatches;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        if (wins < 0) {
            throw new IllegalArgumentException("Wins cannot be negative.");
        }
        if (wins > totalMatches) {
            throw new IllegalArgumentException("Wins cannot exceed total matches.");
        }
        this.wins = wins;
    }

    public int getLosses() {
        return totalMatches - wins;
    }

    public double getWinRate() {
        if (totalMatches == 0) {
            return 0.0;
        }
        return (double) wins / totalMatches;
    }

    public String getFormattedWinRate() {
        return String.format("%.1f%%", getWinRate() * 100);
    }

    public void recordWin() {
        totalMatches++;
        wins++;
    }

    public void recordLoss() {
        totalMatches++;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", members=" + memberIds.size() +
                ", totalMatches=" + totalMatches +
                ", wins=" + wins +
                ", winRate=" + getFormattedWinRate() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
