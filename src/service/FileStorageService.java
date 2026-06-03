package service;

import interfaces.Persistable;
import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Player;
import model.Team;
import model.enums.EquipmentType;
import model.enums.HeroType;
import model.enums.MatchResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-based persistence service that saves and loads game data to/from
 * the filesystem.
 *
 * <p>Uses standard Java I/O ({@link BufferedReader}, {@link BufferedWriter},
 * {@link FileReader}, {@link FileWriter}) for all file operations. Each entity
 * type is stored in its own CSV file within a configurable data directory.</p>
 *
 * <h3>CSV Files</h3>
 * <ul>
 *   <li>{@code players.csv} - player accounts with hero/match references</li>
 *   <li>{@code heroes.csv} - hero definitions with equipment references</li>
 *   <li>{@code equipment.csv} - equipment items with stat bonuses</li>
 *   <li>{@code teams.csv} - team records with member references</li>
 *   <li>{@code matches.csv} - match history with player/hero references</li>
 * </ul>
 *
 * <p>List fields (ownedHeroIds, memberIds, etc.) use semicolon ({@code ;})
 * as the intra-cell separator to avoid conflict with the CSV comma delimiter.
 * Empty lists are written as empty strings.</p>
 */
public class FileStorageService implements Persistable {

    private static final String LIST_SEPARATOR = ";";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final GameDataManager dataManager;
    private final String dataDirectory;

    /**
     * Constructs a FileStorageService backed by the given data manager.
     *
     * @param dataManager   the central data store to save/load from
     * @param dataDirectory the directory path for CSV files
     * @throws IllegalArgumentException if dataManager or directory is null
     */
    public FileStorageService(GameDataManager dataManager,
                              String dataDirectory) {
        if (dataManager == null) {
            throw new IllegalArgumentException(
                    "GameDataManager cannot be null.");
        }
        if (dataDirectory == null || dataDirectory.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Data directory cannot be null or blank.");
        }
        this.dataManager = dataManager;
        this.dataDirectory = dataDirectory;
    }

    // ========================================================================
    // Persistable interface
    // ========================================================================

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #saveAllData()}.</p>
     */
    @Override
    public void save() throws IOException {
        saveAllData();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #loadAllData()}.</p>
     */
    @Override
    public void load() throws IOException {
        loadAllData();
    }

    // ========================================================================
    // Bulk operations
    // ========================================================================

    /**
     * Saves all entity collections to their respective CSV files.
     * Creates the data directory if it does not exist.
     *
     * @throws IOException if an I/O error occurs during any save operation
     */
    public void saveAllData() throws IOException {
        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        saveHeroes();
        saveEquipment();
        saveTeams();
        savePlayers();
        saveMatchRecords();
    }

    /**
     * Loads all entity collections from their respective CSV files.
     * Existing data in the GameDataManager is cleared before loading.
     *
     * <p>Load order respects dependency relationships:
     * Equipment, Heroes, Teams, Players, MatchRecords.</p>
     *
     * @throws IOException if an I/O error or data format error occurs
     */
    public void loadAllData() throws IOException {
        clearAllData();

        loadEquipment();
        loadHeroes();
        loadTeams();
        loadPlayers();
        loadMatchRecords();
    }

    // ========================================================================
    // Player save / load
    // ========================================================================

    /**
     * Saves all players to {@code players.csv}.
     *
     * <p>CSV format:
     * {@code id,username,password,level,totalMatches,wins,losses,teamId,ownedHeroIds,matchRecordIds}
     * </p>
     *
     * @throws IOException if an I/O error occurs
     */
    public void savePlayers() throws IOException {
        File file = new File(dataDirectory, "players.csv");
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file))) {
            writer.write("id,username,password,level,totalMatches,wins,"
                    + "losses,teamId,ownedHeroIds,matchRecordIds");
            writer.newLine();

            for (Player p : dataManager.getAllPlayers()) {
                writer.write(String.join(",",
                        escape(p.getId()),
                        escape(p.getUsername()),
                        escape(p.getPassword()),
                        String.valueOf(p.getLevel()),
                        String.valueOf(p.getTotalMatches()),
                        String.valueOf(p.getWins()),
                        String.valueOf(p.getLosses()),
                        escape(nullToEmpty(p.getTeamId())),
                        escape(formatList(p.getOwnedHeroIds())),
                        escape(formatList(p.getMatchRecordIds()))
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Loads players from {@code players.csv}.
     * Silently returns if the file does not exist.
     *
     * @throws IOException if the file exists but contains invalid data
     */
    public void loadPlayers() throws IOException {
        File file = new File(dataDirectory, "players.csv");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) {
                return;
            }

            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length < 10) {
                    throw new IOException(String.format(
                            "players.csv line %d: expected 10 fields, got %d",
                            lineNumber, fields.length));
                }

                try {
                    String id = fields[0];
                    String username = fields[1];
                    String password = fields[2];
                    int level = Integer.parseInt(fields[3]);
                    int totalMatches = Integer.parseInt(fields[4]);
                    int wins = Integer.parseInt(fields[5]);
                    int losses = Integer.parseInt(fields[6]);
                    String teamId = emptyToNull(fields[7]);

                    Player player = new Player(id, username, password,
                            level, totalMatches, wins, losses, teamId);

                    List<String> heroIds = parseList(fields[8]);
                    if (!heroIds.isEmpty()) {
                        player.setOwnedHeroIds(heroIds);
                    }

                    List<String> matchIds = parseList(fields[9]);
                    if (!matchIds.isEmpty()) {
                        player.setMatchRecordIds(matchIds);
                    }

                    dataManager.addPlayer(player);
                } catch (NumberFormatException e) {
                    throw new IOException(String.format(
                            "players.csv line %d: invalid number format",
                            lineNumber), e);
                } catch (IllegalArgumentException e) {
                    throw new IOException(String.format(
                            "players.csv line %d: %s",
                            lineNumber, e.getMessage()), e);
                }
            }
        }
    }

    // ========================================================================
    // Hero save / load
    // ========================================================================

    /**
     * Saves all heroes to {@code heroes.csv}.
     *
     * <p>CSV format:
     * {@code id,name,type,baseHp,baseAttack,baseDefense,compatibleEquipmentIds}
     * </p>
     *
     * @throws IOException if an I/O error occurs
     */
    public void saveHeroes() throws IOException {
        File file = new File(dataDirectory, "heroes.csv");
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file))) {
            writer.write("id,name,type,baseHp,baseAttack,baseDefense,"
                    + "compatibleEquipmentIds");
            writer.newLine();

            for (Hero h : dataManager.getAllHeroes()) {
                writer.write(String.join(",",
                        escape(h.getId()),
                        escape(h.getName()),
                        escape(h.getType().name()),
                        String.valueOf(h.getBaseHp()),
                        String.valueOf(h.getBaseAttack()),
                        String.valueOf(h.getBaseDefense()),
                        escape(formatList(h.getCompatibleEquipmentIds()))
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Loads heroes from {@code heroes.csv}.
     * Silently returns if the file does not exist.
     *
     * @throws IOException if the file exists but contains invalid data
     */
    public void loadHeroes() throws IOException {
        File file = new File(dataDirectory, "heroes.csv");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) {
                return;
            }

            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length < 7) {
                    throw new IOException(String.format(
                            "heroes.csv line %d: expected 7 fields, got %d",
                            lineNumber, fields.length));
                }

                try {
                    String id = fields[0];
                    String name = fields[1];
                    HeroType type = HeroType.valueOf(fields[2]);
                    int baseHp = Integer.parseInt(fields[3]);
                    int baseAttack = Integer.parseInt(fields[4]);
                    int baseDefense = Integer.parseInt(fields[5]);

                    Hero hero = new Hero(id, name, type,
                            baseHp, baseAttack, baseDefense);

                    List<String> equipIds = parseList(fields[6]);
                    if (!equipIds.isEmpty()) {
                        hero.setCompatibleEquipmentIds(equipIds);
                    }

                    dataManager.addHero(hero);
                } catch (NumberFormatException e) {
                    throw new IOException(String.format(
                            "heroes.csv line %d: invalid number format",
                            lineNumber), e);
                } catch (IllegalArgumentException e) {
                    throw new IOException(String.format(
                            "heroes.csv line %d: %s",
                            lineNumber, e.getMessage()), e);
                }
            }
        }
    }

    // ========================================================================
    // Equipment save / load
    // ========================================================================

    /**
     * Saves all equipment to {@code equipment.csv}.
     *
     * <p>CSV format:
     * {@code id,name,type,attackBonus,defenseBonus,hpBonus,usageCount,averageRating,heroUsageCount}
     * </p>
     *
     * @throws IOException if an I/O error occurs
     */
    public void saveEquipment() throws IOException {
        File file = new File(dataDirectory, "equipment.csv");
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file))) {
            writer.write("id,name,type,attackBonus,defenseBonus,hpBonus,"
                    + "usageCount,averageRating,heroUsageCount");
            writer.newLine();

            for (Equipment e : dataManager.getAllEquipments()) {
                writer.write(String.join(",",
                        escape(e.getId()),
                        escape(e.getName()),
                        escape(e.getType().name()),
                        String.valueOf(e.getAttackBonus()),
                        String.valueOf(e.getDefenseBonus()),
                        String.valueOf(e.getHpBonus()),
                        String.valueOf(e.getUsageCount()),
                        String.valueOf(e.getAverageRating()),
                        String.valueOf(e.getHeroUsageCount())
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Loads equipment from {@code equipment.csv}.
     * Silently returns if the file does not exist.
     *
     * @throws IOException if the file exists but contains invalid data
     */
    public void loadEquipment() throws IOException {
        File file = new File(dataDirectory, "equipment.csv");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) {
                return;
            }

            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length < 9) {
                    throw new IOException(String.format(
                            "equipment.csv line %d: expected 9 fields, got %d",
                            lineNumber, fields.length));
                }

                try {
                    String id = fields[0];
                    String name = fields[1];
                    EquipmentType type = EquipmentType.valueOf(fields[2]);
                    int attackBonus = Integer.parseInt(fields[3]);
                    int defenseBonus = Integer.parseInt(fields[4]);
                    int hpBonus = Integer.parseInt(fields[5]);
                    int usageCount = Integer.parseInt(fields[6]);
                    double averageRating = Double.parseDouble(fields[7]);
                    int heroUsageCount = Integer.parseInt(fields[8]);

                    Equipment equipment = new Equipment(id, name, type,
                            attackBonus, defenseBonus, hpBonus,
                            usageCount, averageRating, heroUsageCount);

                    dataManager.addEquipment(equipment);
                } catch (NumberFormatException e) {
                    throw new IOException(String.format(
                            "equipment.csv line %d: invalid number format",
                            lineNumber), e);
                } catch (IllegalArgumentException e) {
                    throw new IOException(String.format(
                            "equipment.csv line %d: %s",
                            lineNumber, e.getMessage()), e);
                }
            }
        }
    }

    // ========================================================================
    // Team save / load
    // ========================================================================

    /**
     * Saves all teams to {@code teams.csv}.
     *
     * <p>CSV format: {@code id,name,totalMatches,wins,memberIds}</p>
     *
     * @throws IOException if an I/O error occurs
     */
    public void saveTeams() throws IOException {
        File file = new File(dataDirectory, "teams.csv");
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file))) {
            writer.write("id,name,totalMatches,wins,memberIds");
            writer.newLine();

            for (Team t : dataManager.getAllTeams()) {
                writer.write(String.join(",",
                        escape(t.getId()),
                        escape(t.getName()),
                        String.valueOf(t.getTotalMatches()),
                        String.valueOf(t.getWins()),
                        escape(formatList(t.getMemberIds()))
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Loads teams from {@code teams.csv}.
     * Silently returns if the file does not exist.
     *
     * @throws IOException if the file exists but contains invalid data
     */
    public void loadTeams() throws IOException {
        File file = new File(dataDirectory, "teams.csv");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) {
                return;
            }

            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length < 5) {
                    throw new IOException(String.format(
                            "teams.csv line %d: expected 5 fields, got %d",
                            lineNumber, fields.length));
                }

                try {
                    String id = fields[0];
                    String name = fields[1];
                    int totalMatches = Integer.parseInt(fields[2]);
                    int wins = Integer.parseInt(fields[3]);

                    Team team = new Team(id, name, totalMatches, wins);

                    List<String> memberIds = parseList(fields[4]);
                    if (!memberIds.isEmpty()) {
                        team.setMemberIds(memberIds);
                    }

                    dataManager.addTeam(team);
                } catch (NumberFormatException e) {
                    throw new IOException(String.format(
                            "teams.csv line %d: invalid number format",
                            lineNumber), e);
                } catch (IllegalArgumentException e) {
                    throw new IOException(String.format(
                            "teams.csv line %d: %s",
                            lineNumber, e.getMessage()), e);
                }
            }
        }
    }

    // ========================================================================
    // MatchRecord save / load
    // ========================================================================

    /**
     * Saves all match records to {@code matches.csv}.
     *
     * <p>CSV format:
     * {@code id,date,teamId,opponentTeamName,result,playerIds,heroIds}</p>
     *
     * @throws IOException if an I/O error occurs
     */
    public void saveMatchRecords() throws IOException {
        File file = new File(dataDirectory, "matches.csv");
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file))) {
            writer.write("id,date,teamId,opponentTeamName,result,"
                    + "playerIds,heroIds");
            writer.newLine();

            for (MatchRecord m : dataManager.getAllMatchRecords()) {
                String dateStr = m.getMatchDate() != null
                        ? m.getMatchDate().format(DATE_FORMATTER)
                        : "";

                writer.write(String.join(",",
                        escape(m.getId()),
                        escape(dateStr),
                        escape(m.getTeamId()),
                        escape(m.getOpponentTeamName()),
                        escape(m.getResult().name()),
                        escape(formatList(m.getPlayerIds())),
                        escape(formatList(m.getHeroIds()))
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Loads match records from {@code matches.csv}.
     * Silently returns if the file does not exist.
     *
     * @throws IOException if the file exists but contains invalid data
     */
    public void loadMatchRecords() throws IOException {
        File file = new File(dataDirectory, "matches.csv");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) {
                return;
            }

            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length < 7) {
                    throw new IOException(String.format(
                            "matches.csv line %d: expected 7 fields, got %d",
                            lineNumber, fields.length));
                }

                try {
                    String id = fields[0];
                    LocalDate date = LocalDate.parse(fields[1],
                            DATE_FORMATTER);
                    String teamId = fields[2];
                    String opponentTeamName = fields[3];
                    MatchResult result = MatchResult.valueOf(fields[4]);

                    MatchRecord match = new MatchRecord(id, date, teamId,
                            opponentTeamName, result);

                    List<String> playerIds = parseList(fields[5]);
                    if (!playerIds.isEmpty()) {
                        match.setPlayerIds(playerIds);
                    }

                    List<String> heroIds = parseList(fields[6]);
                    if (!heroIds.isEmpty()) {
                        match.setHeroIds(heroIds);
                    }

                    dataManager.addMatchRecord(match);
                } catch (DateTimeParseException e) {
                    throw new IOException(String.format(
                            "matches.csv line %d: invalid date format "
                                    + "(expected yyyy-MM-dd)",
                            lineNumber), e);
                } catch (NumberFormatException e) {
                    throw new IOException(String.format(
                            "matches.csv line %d: invalid number format",
                            lineNumber), e);
                } catch (IllegalArgumentException e) {
                    throw new IOException(String.format(
                            "matches.csv line %d: %s",
                            lineNumber, e.getMessage()), e);
                }
            }
        }
    }

    // ========================================================================
    // Private helpers
    // ========================================================================

    /**
     * Removes all entities from the GameDataManager in safe dependency order.
     */
    private void clearAllData() {
        List<MatchRecord> matchCopy =
                new ArrayList<>(dataManager.getAllMatchRecords());
        for (MatchRecord m : matchCopy) {
            dataManager.removeMatchRecord(m.getId());
        }

        List<Player> playerCopy =
                new ArrayList<>(dataManager.getAllPlayers());
        for (Player p : playerCopy) {
            dataManager.removePlayer(p.getId());
        }

        List<Team> teamCopy =
                new ArrayList<>(dataManager.getAllTeams());
        for (Team t : teamCopy) {
            dataManager.removeTeam(t.getId());
        }

        List<Hero> heroCopy =
                new ArrayList<>(dataManager.getAllHeroes());
        for (Hero h : heroCopy) {
            dataManager.removeHero(h.getId());
        }

        List<Equipment> equipCopy =
                new ArrayList<>(dataManager.getAllEquipments());
        for (Equipment e : equipCopy) {
            dataManager.removeEquipment(e.getId());
        }
    }

    /**
     * Joins a list of IDs into a semicolon-separated string.
     *
     * @param ids the ID list
     * @return semicolon-separated string, or empty string if the list is empty
     */
    private String formatList(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        return String.join(LIST_SEPARATOR, ids);
    }

    /**
     * Parses a semicolon-separated CSV field into a list of strings.
     *
     * @param csvValue the raw CSV field value
     * @return a list of trimmed, non-empty strings
     */
    private List<String> parseList(String csvValue) {
        List<String> result = new ArrayList<>();
        if (csvValue == null || csvValue.trim().isEmpty()) {
            return result;
        }
        for (String part : csvValue.split(LIST_SEPARATOR)) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * Returns the value unchanged. Used as a pass-through when CSV
     * escaping is not needed for simple values.
     *
     * @param value the string value
     * @return the same string
     */
    private String escape(String value) {
        return value;
    }

    /**
     * Converts null to empty string for CSV output.
     *
     * @param value the string, possibly null
     * @return the original string or empty string if null
     */
    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    /**
     * Converts empty string to null for model setter compatibility.
     *
     * @param value the string, possibly empty
     * @return null if the string is empty, otherwise the original value
     */
    private String emptyToNull(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : null;
    }
}
