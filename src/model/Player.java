package model;

import model.enums.Role;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player extends Person {
    private static final int MAX_LEVEL = 30;
    private static final int MIN_LEVEL = 1;

    private int level;
    private int totalMatches;
    private int wins;
    private int losses;
    private List<String> ownedHeroIds;
    private List<String> matchRecordIds;
    private String teamId;

    public Player() {
        setRole(Role.PLAYER);
        setLevel(MIN_LEVEL);
        this.ownedHeroIds = new ArrayList<>();
        this.matchRecordIds = new ArrayList<>();
    }

    public Player(String id, String username, String password,
                  int level, String teamId) {
        super(id, username, password, Role.PLAYER);
        setLevel(level);
        this.totalMatches = 0;
        this.wins = 0;
        this.losses = 0;
        this.ownedHeroIds = new ArrayList<>();
        this.matchRecordIds = new ArrayList<>();
        this.teamId = teamId;
    }

    public Player(String id, String username, String password,
                  int level, int totalMatches, int wins, int losses,
                  String teamId) {
        super(id, username, password, Role.PLAYER);
        setLevel(level);
        setTotalMatches(totalMatches);
        setWins(wins);
        setLosses(losses);
        this.ownedHeroIds = new ArrayList<>();
        this.matchRecordIds = new ArrayList<>();
        this.teamId = teamId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new IllegalArgumentException(
                    "Level must be between " + MIN_LEVEL + " and " + MAX_LEVEL + ".");
        }
        this.level = level;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        if (totalMatches < 0) {
            throw new IllegalArgumentException("Total matches cannot be negative.");
        }
        if (this.wins + this.losses > totalMatches) {
            throw new IllegalArgumentException(
                    "Total matches cannot be less than wins + losses.");
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
        if (wins + this.losses > this.totalMatches) {
            throw new IllegalArgumentException(
                    "Wins + losses cannot exceed total matches.");
        }
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        if (losses < 0) {
            throw new IllegalArgumentException("Losses cannot be negative.");
        }
        if (this.wins + losses > this.totalMatches) {
            throw new IllegalArgumentException(
                    "Wins + losses cannot exceed total matches.");
        }
        this.losses = losses;
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

    public List<String> getOwnedHeroIds() {
        return Collections.unmodifiableList(ownedHeroIds);
    }

    public void setOwnedHeroIds(List<String> ownedHeroIds) {
        this.ownedHeroIds = ownedHeroIds != null ? ownedHeroIds : new ArrayList<>();
    }

    public void addHero(String heroId) {
        if (heroId == null || heroId.trim().isEmpty()) {
            throw new IllegalArgumentException("Hero ID cannot be null or blank.");
        }
        if (!ownedHeroIds.contains(heroId)) {
            ownedHeroIds.add(heroId);
        }
    }

    public boolean removeHero(String heroId) {
        return ownedHeroIds.remove(heroId);
    }

    public boolean ownsHero(String heroId) {
        return ownedHeroIds.contains(heroId);
    }

    public List<String> getMatchRecordIds() {
        return Collections.unmodifiableList(matchRecordIds);
    }

    public void setMatchRecordIds(List<String> matchRecordIds) {
        this.matchRecordIds = matchRecordIds != null ? matchRecordIds : new ArrayList<>();
    }

    public void addMatchRecord(String matchId) {
        if (matchId == null || matchId.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or blank.");
        }
        if (!matchRecordIds.contains(matchId)) {
            matchRecordIds.add(matchId);
        }
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public void recordWin() {
        totalMatches++;
        wins++;
    }

    public void recordLoss() {
        totalMatches++;
        losses++;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + getId() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", level=" + level +
                ", totalMatches=" + totalMatches +
                ", wins=" + wins +
                ", losses=" + losses +
                ", winRate=" + getFormattedWinRate() +
                ", ownedHeroes=" + ownedHeroIds.size() +
                ", teamId='" + teamId + '\'' +
                '}';
    }
}
