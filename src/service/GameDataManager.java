package service;

import model.Admin;
import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Player;
import model.Team;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Central data store and CRUD manager for all game entities.
 *
 * <p>Maintains referential integrity across collections. When an entity is
 * removed, all cross-references in other entities are cleaned up.</p>
 */
public class GameDataManager {

    private final List<Player> players;
    private final List<Admin> admins;
    private final List<Hero> heroes;
    private final List<Equipment> equipments;
    private final List<Team> teams;
    private final List<MatchRecord> matchRecords;

    public GameDataManager() {
        this.players = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.heroes = new ArrayList<>();
        this.equipments = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.matchRecords = new ArrayList<>();
    }

    // ========================================================================
    // Player CRUD
    // ========================================================================

    /**
     * Adds a player. Throws if a player with the same ID already exists.
     *
     * @param player the player to add
     * @throws IllegalArgumentException if ID is duplicate
     */
    public void addPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        if (findPlayerById(player.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Player with ID '" + player.getId() + "' already exists.");
        }
        players.add(player);
    }

    /**
     * Removes a player and cleans up references from their team.
     *
     * @param playerId the ID of the player to remove
     * @throws IllegalArgumentException if player not found
     */
    public void removePlayer(String playerId) {
        Player player = findPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Player not found: " + playerId));

        if (player.getTeamId() != null) {
            findTeamById(player.getTeamId()).ifPresent(
                    team -> team.removeMember(playerId));
        }

        players.remove(player);
    }

    /**
     * Replaces an existing player with updated data.
     * The updated player must have the same ID.
     *
     * @param updated the updated player object
     * @throws IllegalArgumentException if player not found
     */
    public void updatePlayer(Player updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Updated player cannot be null.");
        }
        Player existing = findPlayerById(updated.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Player not found: " + updated.getId()));
        players.set(players.indexOf(existing), updated);
    }

    /**
     * Finds a player by ID.
     *
     * @param playerId the player ID
     * @return an Optional containing the player, or empty if not found
     */
    public Optional<Player> findPlayerById(String playerId) {
        if (playerId == null) {
            return Optional.empty();
        }
        for (Player p : players) {
            if (p.getId().equals(playerId)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns an unmodifiable view of all players.
     *
     * @return unmodifiable list of players
     */
    public List<Player> getAllPlayers() {
        return Collections.unmodifiableList(players);
    }

    // ========================================================================
    // Admin CRUD
    // ========================================================================

    /**
     * Adds an admin. Throws if an admin with the same ID already exists.
     *
     * @param admin the admin to add
     * @throws IllegalArgumentException if ID is duplicate
     */
    public void addAdmin(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null.");
        }
        if (findAdminById(admin.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Admin with ID '" + admin.getId() + "' already exists.");
        }
        admins.add(admin);
    }

    /**
     * Removes an admin by ID.
     *
     * @param adminId the admin ID
     * @throws IllegalArgumentException if admin not found
     */
    public void removeAdmin(String adminId) {
        Admin admin = findAdminById(adminId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Admin not found: " + adminId));
        admins.remove(admin);
    }

    /**
     * Finds an admin by ID.
     *
     * @param adminId the admin ID
     * @return an Optional containing the admin, or empty if not found
     */
    public Optional<Admin> findAdminById(String adminId) {
        if (adminId == null) {
            return Optional.empty();
        }
        for (Admin a : admins) {
            if (a.getId().equals(adminId)) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns an unmodifiable view of all admins.
     *
     * @return unmodifiable list of admins
     */
    public List<Admin> getAllAdmins() {
        return Collections.unmodifiableList(admins);
    }

    // ========================================================================
    // Hero CRUD
    // ========================================================================

    /**
     * Adds a hero. Throws if a hero with the same ID already exists.
     *
     * @param hero the hero to add
     * @throws IllegalArgumentException if ID is duplicate
     */
    public void addHero(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null.");
        }
        if (findHeroById(hero.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Hero with ID '" + hero.getId() + "' already exists.");
        }
        heroes.add(hero);
    }

    /**
     * Removes a hero and cleans up references from all players'
     * owned hero lists.
     *
     * @param heroId the hero ID
     * @throws IllegalArgumentException if hero not found
     */
    public void removeHero(String heroId) {
        Hero hero = findHeroById(heroId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Hero not found: " + heroId));

        for (Player player : players) {
            player.removeHero(heroId);
        }

        heroes.remove(hero);
    }

    /**
     * Replaces an existing hero with updated data.
     *
     * @param updated the updated hero object
     * @throws IllegalArgumentException if hero not found
     */
    public void updateHero(Hero updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Updated hero cannot be null.");
        }
        Hero existing = findHeroById(updated.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Hero not found: " + updated.getId()));
        heroes.set(heroes.indexOf(existing), updated);
    }

    /**
     * Finds a hero by ID.
     *
     * @param heroId the hero ID
     * @return an Optional containing the hero, or empty if not found
     */
    public Optional<Hero> findHeroById(String heroId) {
        if (heroId == null) {
            return Optional.empty();
        }
        for (Hero h : heroes) {
            if (h.getId().equals(heroId)) {
                return Optional.of(h);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns an unmodifiable view of all heroes.
     *
     * @return unmodifiable list of heroes
     */
    public List<Hero> getAllHeroes() {
        return Collections.unmodifiableList(heroes);
    }

    // ========================================================================
    // Equipment CRUD
    // ========================================================================

    /**
     * Adds equipment. Throws if equipment with the same ID already exists.
     *
     * @param equipment the equipment to add
     * @throws IllegalArgumentException if ID is duplicate
     */
    public void addEquipment(Equipment equipment) {
        if (equipment == null) {
            throw new IllegalArgumentException("Equipment cannot be null.");
        }
        if (findEquipmentById(equipment.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Equipment with ID '" + equipment.getId() + "' already exists.");
        }
        equipments.add(equipment);
    }

    /**
     * Removes equipment and cleans up references from all heroes'
     * compatible equipment lists.
     *
     * @param equipmentId the equipment ID
     * @throws IllegalArgumentException if equipment not found
     */
    public void removeEquipment(String equipmentId) {
        Equipment equipment = findEquipmentById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Equipment not found: " + equipmentId));

        for (Hero hero : heroes) {
            hero.removeCompatibleEquipment(equipmentId);
        }

        equipments.remove(equipment);
    }

    /**
     * Replaces existing equipment with updated data.
     *
     * @param updated the updated equipment object
     * @throws IllegalArgumentException if equipment not found
     */
    public void updateEquipment(Equipment updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Updated equipment cannot be null.");
        }
        Equipment existing = findEquipmentById(updated.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Equipment not found: " + updated.getId()));
        equipments.set(equipments.indexOf(existing), updated);
    }

    /**
     * Finds equipment by ID.
     *
     * @param equipmentId the equipment ID
     * @return an Optional containing the equipment, or empty if not found
     */
    public Optional<Equipment> findEquipmentById(String equipmentId) {
        if (equipmentId == null) {
            return Optional.empty();
        }
        for (Equipment e : equipments) {
            if (e.getId().equals(equipmentId)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns an unmodifiable view of all equipment.
     *
     * @return unmodifiable list of equipment
     */
    public List<Equipment> getAllEquipments() {
        return Collections.unmodifiableList(equipments);
    }

    // ========================================================================
    // Team CRUD
    // ========================================================================

    /**
     * Adds a team. Throws if a team with the same ID already exists.
     *
     * @param team the team to add
     * @throws IllegalArgumentException if ID is duplicate
     */
    public void addTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }
        if (findTeamById(team.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Team with ID '" + team.getId() + "' already exists.");
        }
        teams.add(team);
    }

    /**
     * Removes a team and clears the team reference from all member players.
     *
     * @param teamId the team ID
     * @throws IllegalArgumentException if team not found
     */
    public void removeTeam(String teamId) {
        Team team = findTeamById(teamId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Team not found: " + teamId));

        for (Player player : players) {
            if (teamId.equals(player.getTeamId())) {
                player.setTeamId(null);
            }
        }

        teams.remove(team);
    }

    /**
     * Replaces an existing team with updated data.
     *
     * @param updated the updated team object
     * @throws IllegalArgumentException if team not found
     */
    public void updateTeam(Team updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Updated team cannot be null.");
        }
        Team existing = findTeamById(updated.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Team not found: " + updated.getId()));
        teams.set(teams.indexOf(existing), updated);
    }

    /**
     * Finds a team by ID.
     *
     * @param teamId the team ID
     * @return an Optional containing the team, or empty if not found
     */
    public Optional<Team> findTeamById(String teamId) {
        if (teamId == null) {
            return Optional.empty();
        }
        for (Team t : teams) {
            if (t.getId().equals(teamId)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns an unmodifiable view of all teams.
     *
     * @return unmodifiable list of teams
     */
    public List<Team> getAllTeams() {
        return Collections.unmodifiableList(teams);
    }

    // ========================================================================
    // MatchRecord CRUD
    // ========================================================================

    /**
     * Adds a match record. Throws if a record with the same ID already exists.
     *
     * @param matchRecord the match record to add
     * @throws IllegalArgumentException if ID is duplicate
     */
    public void addMatchRecord(MatchRecord matchRecord) {
        if (matchRecord == null) {
            throw new IllegalArgumentException("Match record cannot be null.");
        }
        if (findMatchRecordById(matchRecord.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Match record with ID '" + matchRecord.getId()
                            + "' already exists.");
        }
        matchRecords.add(matchRecord);
    }

    /**
     * Removes a match record and cleans up references from all players'
     * match record lists.
     *
     * @param matchId the match record ID
     * @throws IllegalArgumentException if match record not found
     */
    public void removeMatchRecord(String matchId) {
        MatchRecord match = findMatchRecordById(matchId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Match record not found: " + matchId));

        for (Player player : players) {
            List<String> matchIds = new ArrayList<>(player.getMatchRecordIds());
            if (matchIds.remove(matchId)) {
                player.setMatchRecordIds(matchIds);
            }
        }

        matchRecords.remove(match);
    }

    /**
     * Finds a match record by ID.
     *
     * @param matchId the match record ID
     * @return an Optional containing the match record, or empty if not found
     */
    public Optional<MatchRecord> findMatchRecordById(String matchId) {
        if (matchId == null) {
            return Optional.empty();
        }
        for (MatchRecord m : matchRecords) {
            if (m.getId().equals(matchId)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns an unmodifiable view of all match records.
     *
     * @return unmodifiable list of match records
     */
    public List<MatchRecord> getAllMatchRecords() {
        return Collections.unmodifiableList(matchRecords);
    }
}
