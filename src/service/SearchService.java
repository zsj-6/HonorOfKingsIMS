package service;

import model.Hero;
import model.Player;
import model.Team;
import java.util.Optional;

/**
 * Provides search operations across players, teams, and heroes.
 *
 * <p>All search methods return {@link Optional} to avoid null returns.
 * Name-based searches are case-insensitive.</p>
 */
public class SearchService {

    private final GameDataManager dataManager;

    /**
     * Constructs a SearchService backed by the given data manager.
     *
     * @param dataManager the central data store
     */
    public SearchService(GameDataManager dataManager) {
        if (dataManager == null) {
            throw new IllegalArgumentException(
                    "GameDataManager cannot be null.");
        }
        this.dataManager = dataManager;
    }

    /**
     * Searches for a player by exact ID match.
     *
     * @param id the player ID
     * @return an Optional containing the player if found
     */
    public Optional<Player> searchPlayerById(String id) {
        return dataManager.findPlayerById(id);
    }

    /**
     * Searches for a player by name (case-insensitive).
     * Returns the first match if multiple players share the same name.
     *
     * @param name the player name
     * @return an Optional containing the player if found
     */
    public Optional<Player> searchPlayerByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        String lowerName = name.trim().toLowerCase();
        for (Player player : dataManager.getAllPlayers()) {
            if (player.getUsername().toLowerCase().equals(lowerName)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    /**
     * Searches for a team by exact ID match.
     *
     * @param id the team ID
     * @return an Optional containing the team if found
     */
    public Optional<Team> searchTeamById(String id) {
        return dataManager.findTeamById(id);
    }

    /**
     * Searches for a team by name (case-insensitive).
     * Returns the first match if multiple teams share the same name.
     *
     * @param name the team name
     * @return an Optional containing the team if found
     */
    public Optional<Team> searchTeamByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        String lowerName = name.trim().toLowerCase();
        for (Team team : dataManager.getAllTeams()) {
            if (team.getName().toLowerCase().equals(lowerName)) {
                return Optional.of(team);
            }
        }
        return Optional.empty();
    }

    /**
     * Searches for a hero by name (case-insensitive).
     * Returns the first match if multiple heroes share the same name.
     *
     * @param name the hero name
     * @return an Optional containing the hero if found
     */
    public Optional<Hero> searchHeroByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        String lowerName = name.trim().toLowerCase();
        for (Hero hero : dataManager.getAllHeroes()) {
            if (hero.getName().toLowerCase().equals(lowerName)) {
                return Optional.of(hero);
            }
        }
        return Optional.empty();
    }
}
