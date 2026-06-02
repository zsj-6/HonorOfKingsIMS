package model;

import model.enums.MatchResult;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MatchRecord {
    private String id;
    private LocalDate matchDate;
    private String teamId;
    private String opponentTeamName;
    private MatchResult result;
    private List<String> playerIds;
    private List<String> heroIds;

    public MatchRecord() {
        this.playerIds = new ArrayList<>();
        this.heroIds = new ArrayList<>();
    }

    public MatchRecord(String id, LocalDate matchDate, String teamId,
                       String opponentTeamName, MatchResult result) {
        setId(id);
        setMatchDate(matchDate);
        setTeamId(teamId);
        setOpponentTeamName(opponentTeamName);
        setResult(result);
        this.playerIds = new ArrayList<>();
        this.heroIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or blank.");
        }
        this.id = id;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        if (matchDate == null) {
            throw new IllegalArgumentException("Match date cannot be null.");
        }
        if (matchDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Match date cannot be in the future.");
        }
        this.matchDate = matchDate;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        if (teamId == null || teamId.trim().isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be null or blank.");
        }
        this.teamId = teamId;
    }

    public String getOpponentTeamName() {
        return opponentTeamName;
    }

    public void setOpponentTeamName(String opponentTeamName) {
        if (opponentTeamName == null || opponentTeamName.trim().isEmpty()) {
            throw new IllegalArgumentException("Opponent team name cannot be null or blank.");
        }
        this.opponentTeamName = opponentTeamName;
    }

    public MatchResult getResult() {
        return result;
    }

    public void setResult(MatchResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Match result cannot be null.");
        }
        this.result = result;
    }

    public List<String> getPlayerIds() {
        return Collections.unmodifiableList(playerIds);
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = playerIds != null ? playerIds : new ArrayList<>();
    }

    public void addPlayer(String playerId) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or blank.");
        }
        if (!playerIds.contains(playerId)) {
            playerIds.add(playerId);
        }
    }

    public List<String> getHeroIds() {
        return Collections.unmodifiableList(heroIds);
    }

    public void setHeroIds(List<String> heroIds) {
        this.heroIds = heroIds != null ? heroIds : new ArrayList<>();
    }

    public void addHero(String heroId) {
        if (heroId == null || heroId.trim().isEmpty()) {
            throw new IllegalArgumentException("Hero ID cannot be null or blank.");
        }
        if (!heroIds.contains(heroId)) {
            heroIds.add(heroId);
        }
    }

    public boolean isWin() {
        return result == MatchResult.WIN;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "MatchRecord{" +
                "id='" + id + '\'' +
                ", date=" + (matchDate != null ? matchDate.format(fmt) : "null") +
                ", teamId='" + teamId + '\'' +
                ", opponent='" + opponentTeamName + '\'' +
                ", result=" + result +
                ", players=" + playerIds.size() +
                ", heroes=" + heroIds.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchRecord that = (MatchRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
