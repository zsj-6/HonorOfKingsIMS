package service;

import model.Equipment;
import model.Player;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Provides leaderboard and statistics ranking operations.
 *
 * <p>Player tie-breaking follows the coursework specification:
 * <ol>
 *   <li>Higher win rate</li>
 *   <li>Higher level</li>
 *   <li>More matches</li>
 *   <li>Player ID (ascending)</li>
 * </ol>
 *
 * <p>Equipment is ranked by its computed score
 * ({@link Equipment#getEquipmentScore()}).</p>
 */
public class RankingService {

    /**
     * Master tiebreaker comparator used across all player ranking methods.
     * Sorts by: winRate DESC, level DESC, totalMatches DESC, id ASC.
     */
    private static final Comparator<Player> TIEBREAKER =
            Comparator.comparingDouble(Player::getWinRate).reversed()
                    .thenComparing(
                            Comparator.comparingInt(Player::getLevel).reversed())
                    .thenComparing(
                            Comparator.comparingInt(
                                    Player::getTotalMatches).reversed())
                    .thenComparing(Player::getId);

    private final GameDataManager dataManager;

    /**
     * Constructs a RankingService backed by the given data manager.
     *
     * @param dataManager the central data store
     */
    public RankingService(GameDataManager dataManager) {
        if (dataManager == null) {
            throw new IllegalArgumentException(
                    "GameDataManager cannot be null.");
        }
        this.dataManager = dataManager;
    }

    /**
     * Ranks all players by win rate (descending), using the standard
     * tiebreaker chain for equal win rates.
     *
     * @return a new list of players sorted by ranking
     */
    public List<Player> getTopPlayersByWinRate() {
        List<Player> sorted = new ArrayList<>(dataManager.getAllPlayers());
        sorted.sort(TIEBREAKER);
        return sorted;
    }

    /**
     * Ranks all players by level (descending), using the standard
     * tiebreaker chain for equal levels.
     *
     * @return a new list of players sorted by ranking
     */
    public List<Player> getTopPlayersByLevel() {
        List<Player> sorted = new ArrayList<>(dataManager.getAllPlayers());
        sorted.sort(
                Comparator.comparingInt(Player::getLevel).reversed()
                        .thenComparing(TIEBREAKER));
        return sorted;
    }

    /**
     * Ranks all players by total match count (descending), using the
     * standard tiebreaker chain for equal match counts.
     *
     * @return a new list of players sorted by ranking
     */
    public List<Player> getTopPlayersByMatchCount() {
        List<Player> sorted = new ArrayList<>(dataManager.getAllPlayers());
        sorted.sort(
                Comparator.comparingInt(Player::getTotalMatches).reversed()
                        .thenComparing(TIEBREAKER));
        return sorted;
    }

    /**
     * Ranks all equipment by computed score (descending).
     *
     * @return a new list of equipment sorted by score, highest first
     */
    public List<Equipment> getEquipmentRanking() {
        List<Equipment> sorted = new ArrayList<>(
                dataManager.getAllEquipments());
        sorted.sort(Comparator.comparingDouble(
                Equipment::getEquipmentScore).reversed());
        return sorted;
    }
}
