import exception.AuthenticationException;
import model.Admin;
import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Person;
import model.Player;
import model.Team;
import model.enums.EquipmentType;
import model.enums.HeroType;
import model.enums.MatchResult;
import service.AuthenticationService;
import service.FileStorageService;
import service.GameDataManager;
import service.RankingService;
import service.SearchService;
import util.DataInitializer;
import util.InputHelper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * Console-based menu system for the Honor of Kings Information Management System.
 *
 * <p>Provides role-based menus for Administrators and Players, calling
 * existing service-layer methods for all business logic. All menus use
 * loop-based navigation (no recursion).</p>
 */
public class Main {

    private static final String DATA_DIR = "data";
    private static final String SEPARATOR = "========================================";
    private static final String THIN_SEPARATOR = "----------------------------------------";
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final GameDataManager dataManager;
    private final AuthenticationService authService;
    private final SearchService searchService;
    private final RankingService rankingService;
    private final FileStorageService fileStorageService;

    public Main() {
        this.dataManager = new GameDataManager();
        this.authService = new AuthenticationService(dataManager);
        this.searchService = new SearchService(dataManager);
        this.rankingService = new RankingService(dataManager);
        this.fileStorageService = new FileStorageService(dataManager, DATA_DIR);
    }

    // ========================================================================
    // Entry Point
    // ========================================================================

    /**
     * Application entry point. Initializes default data, attempts to load
     * persisted CSV data, then displays the login menu.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    // ========================================================================
    // Startup
    // ========================================================================

    /**
     * Starts the application: loads default data, attempts CSV restore,
     * and enters the login menu loop.
     */
    public void start() {
        System.out.println(SEPARATOR);
        System.out.println("  Honor of Kings IMS");
        System.out.println(SEPARATOR);

        DataInitializer.initialize(dataManager);
        System.out.println("Default data initialized.");

        tryLoadCsv();
        showLoginMenu();
    }

    private void tryLoadCsv() {
        File playersFile = new File(DATA_DIR, "players.csv");
        if (playersFile.exists()) {
            try {
                fileStorageService.loadAllData();
                System.out.println("Loaded persisted data from CSV files.");
            } catch (IOException e) {
                System.out.println("Warning: Failed to load CSV data ("
                        + e.getMessage() + "). Using default data.");
            }
        } else {
            System.out.println("No saved data found. Using default data.");
        }
    }

    // ========================================================================
    // Login Menu
    // ========================================================================

    private void showLoginMenu() {
        while (true) {
            System.out.println();
            System.out.println(SEPARATOR);
            System.out.println("  LOGIN MENU");
            System.out.println(SEPARATOR);
            System.out.println("  1. Login");
            System.out.println("  2. Exit");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 2);

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleExit();
                    return;
                default:
                    break;
            }
        }
    }

    private void handleLogin() {
        System.out.println();
        System.out.println(THIN_SEPARATOR);
        System.out.println("  LOGIN");
        System.out.println(THIN_SEPARATOR);

        String username = InputHelper.readLineSafe("Username: ");
        String password = InputHelper.readPassword("Password: ");

        try {
            Person user = authService.login(username, password);
            System.out.println("Login successful. Welcome, " + user.getUsername() + "!");

            if (authService.isAdmin()) {
                showAdminMenu();
            } else {
                showPlayerMenu();
            }
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void handleExit() {
        System.out.println();
        System.out.println(THIN_SEPARATOR);
        System.out.println("  EXIT");
        System.out.println(THIN_SEPARATOR);

        boolean save = InputHelper.readConfirmation("Save data before exit? (Y/N): ");
        if (save) {
            try {
                fileStorageService.saveAllData();
                System.out.println("Data saved successfully.");
            } catch (IOException e) {
                System.out.println("Error saving data: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    // ========================================================================
    // Admin Menu
    // ========================================================================

    private void showAdminMenu() {
        while (true) {
            System.out.println();
            System.out.println(SEPARATOR);
            System.out.println("  ADMIN MENU  —  Logged in as: "
                    + authService.getCurrentUser().getUsername());
            System.out.println(SEPARATOR);
            System.out.println("  1. Player Management");
            System.out.println("  2. Hero Management");
            System.out.println("  3. Equipment Management");
            System.out.println("  4. Team Overview");
            System.out.println("  5. Match Records");
            System.out.println("  6. Search");
            System.out.println("  7. Leaderboard");
            System.out.println("  8. Save Data");
            System.out.println("  9. Logout");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 9);

            switch (choice) {
                case 1:
                    handlePlayerManagement();
                    break;
                case 2:
                    handleHeroManagement();
                    break;
                case 3:
                    handleEquipmentManagement();
                    break;
                case 4:
                    handleTeamOverview();
                    break;
                case 5:
                    handleMatchRecords();
                    break;
                case 6:
                    handleSearch();
                    break;
                case 7:
                    handleLeaderboard();
                    break;
                case 8:
                    handleSaveData();
                    break;
                case 9:
                    authService.logout();
                    System.out.println("Logged out.");
                    return;
                default:
                    break;
            }
        }
    }

    // ========================================================================
    // Admin — Player Management
    // ========================================================================

    private void handlePlayerManagement() {
        while (true) {
            System.out.println();
            System.out.println(THIN_SEPARATOR);
            System.out.println("  PLAYER MANAGEMENT");
            System.out.println(THIN_SEPARATOR);
            System.out.println("  1. Add Player");
            System.out.println("  2. Delete Player");
            System.out.println("  3. Update Player");
            System.out.println("  4. View Player");
            System.out.println("  5. View All Players");
            System.out.println("  6. Back");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 6);

            switch (choice) {
                case 1:
                    addPlayer();
                    break;
                case 2:
                    deletePlayer();
                    break;
                case 3:
                    updatePlayer();
                    break;
                case 4:
                    viewPlayer();
                    break;
                case 5:
                    viewAllPlayers();
                    break;
                case 6:
                    return;
                default:
                    break;
            }
        }
    }

    private void addPlayer() {
        System.out.println();
        System.out.println("-- Add Player --");

        try {
            String id = InputHelper.readLineSafe("ID: ");
            String username = InputHelper.readLineSafe("Username: ");
            String password = InputHelper.readPassword("Password: ");
            int level = InputHelper.readInt("Level (1-30): ", 1, 30);
            String teamId = InputHelper.readString("Team ID (or blank): ");

            Player player = new Player(id, username, password, level,
                    teamId.isEmpty() ? null : teamId);
            dataManager.addPlayer(player);

            if (!teamId.isEmpty()) {
                findTeamById(teamId).ifPresent(t -> t.addMember(id));
            }

            System.out.println("Player added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deletePlayer() {
        System.out.println();
        System.out.println("-- Delete Player --");

        String id = InputHelper.readLineSafe("Player ID to delete: ");
        Optional<Player> playerOpt = dataManager.findPlayerById(id);
        if (!playerOpt.isPresent()) {
            System.out.println("Player not found.");
            return;
        }

        displayPlayer(playerOpt.get());
        boolean confirm = InputHelper.readConfirmation("Confirm delete? (Y/N): ");
        if (confirm) {
            try {
                dataManager.removePlayer(id);
                System.out.println("Player deleted.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void updatePlayer() {
        System.out.println();
        System.out.println("-- Update Player --");

        String id = InputHelper.readLineSafe("Player ID to update: ");
        Optional<Player> playerOpt = dataManager.findPlayerById(id);
        if (!playerOpt.isPresent()) {
            System.out.println("Player not found.");
            return;
        }

        Player player = playerOpt.get();
        while (true) {
            System.out.println();
            displayPlayer(player);
            System.out.println();
            System.out.println("  Fields to update:");
            System.out.println("  1. Username");
            System.out.println("  2. Password");
            System.out.println("  3. Level");
            System.out.println("  4. Team ID");
            System.out.println("  5. Done (save changes)");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Choose field: ", 1, 5);
            switch (choice) {
                case 1:
                    player.setUsername(InputHelper.readLineSafe("New username: "));
                    break;
                case 2:
                    player.setPassword(InputHelper.readPassword("New password: "));
                    break;
                case 3:
                    player.setLevel(InputHelper.readInt("New level (1-30): ", 1, 30));
                    break;
                case 4:
                    String newTeamId = InputHelper.readString("New team ID (or blank): ");
                    String oldTeamId = player.getTeamId();
                    player.setTeamId(newTeamId.isEmpty() ? null : newTeamId);
                    if (oldTeamId != null) {
                        findTeamById(oldTeamId).ifPresent(t -> t.removeMember(id));
                    }
                    if (!newTeamId.isEmpty()) {
                        findTeamById(newTeamId).ifPresent(t -> t.addMember(id));
                    }
                    System.out.println("Team updated.");
                    break;
                case 5:
                    try {
                        dataManager.updatePlayer(player);
                        System.out.println("Player updated successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    return;
                default:
                    break;
            }
        }
    }

    private void viewPlayer() {
        System.out.println();
        System.out.println("-- View Player --");
        String id = InputHelper.readLineSafe("Player ID: ");
        Optional<Player> playerOpt = dataManager.findPlayerById(id);
        if (!playerOpt.isPresent()) {
            System.out.println("Player not found.");
            return;
        }
        displayPlayer(playerOpt.get());
    }

    private void viewAllPlayers() {
        System.out.println();
        System.out.println("-- All Players (" + dataManager.getAllPlayers().size()
                + " total) --");
        for (Player p : dataManager.getAllPlayers()) {
            System.out.println(THIN_SEPARATOR);
            displayPlayer(p);
        }
    }

    // ========================================================================
    // Admin — Hero Management
    // ========================================================================

    private void handleHeroManagement() {
        while (true) {
            System.out.println();
            System.out.println(THIN_SEPARATOR);
            System.out.println("  HERO MANAGEMENT");
            System.out.println(THIN_SEPARATOR);
            System.out.println("  1. Add Hero");
            System.out.println("  2. Delete Hero");
            System.out.println("  3. View All Heroes");
            System.out.println("  4. Back");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 4);

            switch (choice) {
                case 1:
                    addHero();
                    break;
                case 2:
                    deleteHero();
                    break;
                case 3:
                    viewAllHeroes();
                    break;
                case 4:
                    return;
                default:
                    break;
            }
        }
    }

    private void addHero() {
        System.out.println();
        System.out.println("-- Add Hero --");

        try {
            String id = InputHelper.readLineSafe("ID: ");
            String name = InputHelper.readLineSafe("Name: ");
            System.out.println("Types: TANK, FIGHTER, ASSASSIN, MAGE, MARKSMAN, SUPPORT");
            String typeStr = InputHelper.readNonEmptyString("Type: ");
            HeroType type = HeroType.valueOf(typeStr.toUpperCase());
            int hp = InputHelper.readInt("Base HP: ");
            int atk = InputHelper.readInt("Base Attack: ");
            int def = InputHelper.readInt("Base Defense: ");

            Hero hero = new Hero(id, name, type, hp, atk, def);
            dataManager.addHero(hero);
            System.out.println("Hero added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteHero() {
        System.out.println();
        System.out.println("-- Delete Hero --");
        String id = InputHelper.readLineSafe("Hero ID to delete: ");
        Optional<Hero> heroOpt = dataManager.findHeroById(id);
        if (!heroOpt.isPresent()) {
            System.out.println("Hero not found.");
            return;
        }
        displayHero(heroOpt.get());
        if (InputHelper.readConfirmation("Confirm delete? (Y/N): ")) {
            try {
                dataManager.removeHero(id);
                System.out.println("Hero deleted.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewAllHeroes() {
        System.out.println();
        System.out.println("-- All Heroes (" + dataManager.getAllHeroes().size()
                + " total) --");
        for (Hero h : dataManager.getAllHeroes()) {
            System.out.println(THIN_SEPARATOR);
            displayHero(h);
        }
    }

    // ========================================================================
    // Admin — Equipment Management
    // ========================================================================

    private void handleEquipmentManagement() {
        while (true) {
            System.out.println();
            System.out.println(THIN_SEPARATOR);
            System.out.println("  EQUIPMENT MANAGEMENT");
            System.out.println(THIN_SEPARATOR);
            System.out.println("  1. Add Equipment");
            System.out.println("  2. Delete Equipment");
            System.out.println("  3. View All Equipment");
            System.out.println("  4. Equipment Ranking");
            System.out.println("  5. Back");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 5);

            switch (choice) {
                case 1:
                    addEquipment();
                    break;
                case 2:
                    deleteEquipment();
                    break;
                case 3:
                    viewAllEquipment();
                    break;
                case 4:
                    displayEquipmentRanking();
                    break;
                case 5:
                    return;
                default:
                    break;
            }
        }
    }

    private void addEquipment() {
        System.out.println();
        System.out.println("-- Add Equipment --");

        try {
            String id = InputHelper.readLineSafe("ID: ");
            String name = InputHelper.readLineSafe("Name: ");
            System.out.println("Types: ATTACK, DEFENSE, MAGIC, MOVEMENT");
            String typeStr = InputHelper.readNonEmptyString("Type: ");
            EquipmentType type = EquipmentType.valueOf(typeStr.toUpperCase());
            int atk = InputHelper.readInt("Attack Bonus: ");
            int def = InputHelper.readInt("Defense Bonus: ");
            int hp = InputHelper.readInt("HP Bonus: ");

            Equipment eq = new Equipment(id, name, type, atk, def, hp);
            dataManager.addEquipment(eq);
            System.out.println("Equipment added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteEquipment() {
        System.out.println();
        System.out.println("-- Delete Equipment --");
        String id = InputHelper.readLineSafe("Equipment ID to delete: ");
        Optional<Equipment> eqOpt = dataManager.findEquipmentById(id);
        if (!eqOpt.isPresent()) {
            System.out.println("Equipment not found.");
            return;
        }
        displayEquipment(eqOpt.get());
        if (InputHelper.readConfirmation("Confirm delete? (Y/N): ")) {
            try {
                dataManager.removeEquipment(id);
                System.out.println("Equipment deleted.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewAllEquipment() {
        System.out.println();
        System.out.println("-- All Equipment (" + dataManager.getAllEquipments().size()
                + " total) --");
        for (Equipment e : dataManager.getAllEquipments()) {
            System.out.println(THIN_SEPARATOR);
            displayEquipment(e);
        }
    }

    // ========================================================================
    // Admin — Team Overview
    // ========================================================================

    private void handleTeamOverview() {
        while (true) {
            System.out.println();
            System.out.println(THIN_SEPARATOR);
            System.out.println("  TEAM OVERVIEW");
            System.out.println(THIN_SEPARATOR);
            System.out.println("  1. View All Teams");
            System.out.println("  2. Search Team by ID");
            System.out.println("  3. Search Team by Name");
            System.out.println("  4. Back");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 4);

            switch (choice) {
                case 1:
                    viewAllTeams();
                    break;
                case 2:
                    searchTeamById();
                    break;
                case 3:
                    searchTeamByName();
                    break;
                case 4:
                    return;
                default:
                    break;
            }
        }
    }

    private void viewAllTeams() {
        System.out.println();
        System.out.println("-- All Teams (" + dataManager.getAllTeams().size()
                + " total) --");
        for (Team t : dataManager.getAllTeams()) {
            System.out.println(THIN_SEPARATOR);
            displayTeam(t);
        }
    }

    private void searchTeamById() {
        System.out.println();
        String id = InputHelper.readLineSafe("Team ID: ");
        Optional<Team> result = searchService.searchTeamById(id);
        if (!result.isPresent()) {
            System.out.println("Team not found.");
        } else {
            displayTeam(result.get());
        }
    }

    private void searchTeamByName() {
        System.out.println();
        String name = InputHelper.readLineSafe("Team name: ");
        Optional<Team> result = searchService.searchTeamByName(name);
        if (!result.isPresent()) {
            System.out.println("Team not found.");
        } else {
            displayTeam(result.get());
        }
    }

    // ========================================================================
    // Admin — Match Records
    // ========================================================================

    private void handleMatchRecords() {
        while (true) {
            System.out.println();
            System.out.println(THIN_SEPARATOR);
            System.out.println("  MATCH RECORDS");
            System.out.println(THIN_SEPARATOR);
            System.out.println("  1. View All Matches");
            System.out.println("  2. View Matches by Team");
            System.out.println("  3. View Match by ID");
            System.out.println("  4. Add Match Record");
            System.out.println("  5. Back");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 5);

            switch (choice) {
                case 1:
                    viewAllMatches();
                    break;
                case 2:
                    viewMatchesByTeam();
                    break;
                case 3:
                    viewMatchById();
                    break;
                case 4:
                    addMatchRecord();
                    break;
                case 5:
                    return;
                default:
                    break;
            }
        }
    }

    private void viewAllMatches() {
        System.out.println();
        List<MatchRecord> matches = dataManager.getAllMatchRecords();
        System.out.println("-- All Matches (" + matches.size() + " total) --");
        for (MatchRecord m : matches) {
            System.out.println(THIN_SEPARATOR);
            displayMatch(m);
        }
    }

    private void viewMatchesByTeam() {
        System.out.println();
        String teamId = InputHelper.readLineSafe("Team ID: ");
        List<MatchRecord> matches = dataManager.getAllMatchRecords();
        int count = 0;
        for (MatchRecord m : matches) {
            if (teamId.equals(m.getTeamId())) {
                if (count == 0) {
                    System.out.println("-- Matches for Team " + teamId + " --");
                }
                System.out.println(THIN_SEPARATOR);
                displayMatch(m);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("No matches found for team " + teamId + ".");
        }
    }

    private void viewMatchById() {
        System.out.println();
        String id = InputHelper.readLineSafe("Match ID: ");
        Optional<MatchRecord> result = dataManager.findMatchRecordById(id);
        if (!result.isPresent()) {
            System.out.println("Match not found.");
        } else {
            displayMatch(result.get());
        }
    }

    private void addMatchRecord() {
        System.out.println();
        System.out.println("-- Add Match Record --");

        try {
            String id = InputHelper.readLineSafe("ID: ");
            String dateStr = InputHelper.readNonEmptyString("Date (yyyy-MM-dd): ");
            LocalDate date = LocalDate.parse(dateStr, DATE_FMT);
            String teamId = InputHelper.readLineSafe("Team ID: ");
            String opponent = InputHelper.readLineSafe("Opponent team name: ");
            String resultStr = InputHelper.readNonEmptyString("Result (WIN/LOSS): ");
            MatchResult result = MatchResult.valueOf(resultStr.toUpperCase());

            MatchRecord match = new MatchRecord(id, date, teamId, opponent, result);
            dataManager.addMatchRecord(match);
            System.out.println("Match record added successfully.");
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format. Use yyyy-MM-dd.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ========================================================================
    // Admin — Search
    // ========================================================================

    private void handleSearch() {
        while (true) {
            System.out.println();
            System.out.println(THIN_SEPARATOR);
            System.out.println("  SEARCH");
            System.out.println(THIN_SEPARATOR);
            System.out.println("  1. Search Player by ID");
            System.out.println("  2. Search Player by Name");
            System.out.println("  3. Search Team by ID");
            System.out.println("  4. Search Team by Name");
            System.out.println("  5. Search Hero by Name");
            System.out.println("  6. Back");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 6);

            switch (choice) {
                case 1:
                    searchPlayerById();
                    break;
                case 2:
                    searchPlayerByName();
                    break;
                case 3:
                    searchTeamById();
                    break;
                case 4:
                    searchTeamByName();
                    break;
                case 5:
                    searchHeroByName();
                    break;
                case 6:
                    return;
                default:
                    break;
            }
        }
    }

    private void searchPlayerById() {
        System.out.println();
        String id = InputHelper.readLineSafe("Player ID: ");
        Optional<Player> result = searchService.searchPlayerById(id);
        if (!result.isPresent()) {
            System.out.println("Player not found.");
        } else {
            displayPlayer(result.get());
        }
    }

    private void searchPlayerByName() {
        System.out.println();
        String name = InputHelper.readLineSafe("Player name: ");
        Optional<Player> result = searchService.searchPlayerByName(name);
        if (!result.isPresent()) {
            System.out.println("Player not found.");
        } else {
            displayPlayer(result.get());
        }
    }

    private void searchHeroByName() {
        System.out.println();
        String name = InputHelper.readLineSafe("Hero name: ");
        Optional<Hero> result = searchService.searchHeroByName(name);
        if (!result.isPresent()) {
            System.out.println("Hero not found.");
        } else {
            displayHero(result.get());
        }
    }

    // ========================================================================
    // Admin — Leaderboard
    // ========================================================================

    private void handleLeaderboard() {
        while (true) {
            System.out.println();
            System.out.println(THIN_SEPARATOR);
            System.out.println("  LEADERBOARD");
            System.out.println(THIN_SEPARATOR);
            System.out.println("  1. Top Players by Win Rate");
            System.out.println("  2. Top Players by Level");
            System.out.println("  3. Top Players by Match Count");
            System.out.println("  4. Equipment Ranking");
            System.out.println("  5. Back");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 5);

            switch (choice) {
                case 1:
                    displayTopByWinRate();
                    break;
                case 2:
                    displayTopByLevel();
                    break;
                case 3:
                    displayTopByMatchCount();
                    break;
                case 4:
                    displayEquipmentRanking();
                    break;
                case 5:
                    return;
                default:
                    break;
            }
        }
    }

    private void displayTopByWinRate() {
        System.out.println();
        System.out.println("-- Top Players by Win Rate --");
        List<Player> ranked = rankingService.getTopPlayersByWinRate();
        displayPlayerRanking(ranked, "Win Rate");
    }

    private void displayTopByLevel() {
        System.out.println();
        System.out.println("-- Top Players by Level --");
        List<Player> ranked = rankingService.getTopPlayersByLevel();
        displayPlayerRanking(ranked, "Level");
    }

    private void displayTopByMatchCount() {
        System.out.println();
        System.out.println("-- Top Players by Match Count --");
        List<Player> ranked = rankingService.getTopPlayersByMatchCount();
        displayPlayerRanking(ranked, "Matches");
    }

    private void displayPlayerRanking(List<Player> players, String metric) {
        if (players.isEmpty()) {
            System.out.println("No players to display.");
            return;
        }
        System.out.printf("  %-4s %-6s %-18s %-8s %-8s %-8s %-8s%n",
                "Rank", "ID", "Name", "Level", "Matches", metric, "WinRate");
        System.out.println(THIN_SEPARATOR);
        int rank = 1;
        for (Player p : players) {
            int metricValue;
            switch (metric) {
                case "Win Rate":
                    metricValue = (int) (p.getWinRate() * 100);
                    break;
                case "Level":
                    metricValue = p.getLevel();
                    break;
                case "Matches":
                    metricValue = p.getTotalMatches();
                    break;
                default:
                    metricValue = 0;
            }
            System.out.printf("  %-4d %-6s %-18s %-8d %-8d ", rank, p.getId(),
                    truncate(p.getUsername(), 18), p.getLevel(),
                    p.getTotalMatches());
            if (metric.equals("Win Rate")) {
                System.out.printf("%-8s ", metricValue + "%");
            } else {
                System.out.printf("%-8d ", metricValue);
            }
            System.out.printf("%-8s%n", p.getFormattedWinRate());
            rank++;
            if (rank > 15) {
                break;
            }
        }
    }

    private void displayEquipmentRanking() {
        System.out.println();
        System.out.println("-- Equipment Ranking (by Score) --");
        List<Equipment> ranked = rankingService.getEquipmentRanking();
        if (ranked.isEmpty()) {
            System.out.println("No equipment to display.");
            return;
        }
        System.out.printf("  %-4s %-6s %-20s %-10s %-8s %-8s %-8s%n",
                "Rank", "ID", "Name", "Type", "Usage", "Rating", "Score");
        System.out.println(THIN_SEPARATOR);
        int rank = 1;
        for (Equipment e : ranked) {
            System.out.printf("  %-4d %-6s %-20s %-10s %-8d %-8.1f %-8.2f%n",
                    rank++, e.getId(), truncate(e.getName(), 20),
                    e.getType().getDisplayName(), e.getUsageCount(),
                    e.getAverageRating(), e.getEquipmentScore());
        }
    }

    // ========================================================================
    // Admin — Save Data
    // ========================================================================

    private void handleSaveData() {
        System.out.println();
        System.out.println("-- Save Data --");
        try {
            fileStorageService.saveAllData();
            System.out.println("All data saved to CSV files successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // ========================================================================
    // Player Menu
    // ========================================================================

    private void showPlayerMenu() {
        while (true) {
            System.out.println();
            System.out.println(SEPARATOR);
            System.out.println("  PLAYER MENU  —  Logged in as: "
                    + authService.getCurrentUser().getUsername());
            System.out.println(SEPARATOR);
            System.out.println("  1. View Profile");
            System.out.println("  2. View Owned Heroes");
            System.out.println("  3. View Match History");
            System.out.println("  4. Search Hero");
            System.out.println("  5. View Leaderboard");
            System.out.println("  6. Save Data");
            System.out.println("  7. Logout");
            System.out.println(THIN_SEPARATOR);

            int choice = InputHelper.readInt("Enter choice: ", 1, 7);

            switch (choice) {
                case 1:
                    handleViewProfile();
                    break;
                case 2:
                    handleViewOwnedHeroes();
                    break;
                case 3:
                    handleViewMatchHistory();
                    break;
                case 4:
                    searchHeroByName();
                    break;
                case 5:
                    handleViewLeaderboard();
                    break;
                case 6:
                    handleSaveData();
                    break;
                case 7:
                    authService.logout();
                    System.out.println("Logged out.");
                    return;
                default:
                    break;
            }
        }
    }

    private void handleViewProfile() {
        System.out.println();
        System.out.println("-- Your Profile --");
        Player player = (Player) authService.getCurrentUser();
        displayPlayer(player);
    }

    private void handleViewOwnedHeroes() {
        System.out.println();
        Player player = (Player) authService.getCurrentUser();
        List<String> heroIds = player.getOwnedHeroIds();
        System.out.println("-- Owned Heroes (" + heroIds.size() + ") --");
        if (heroIds.isEmpty()) {
            System.out.println("You do not own any heroes.");
            return;
        }
        for (String heroId : heroIds) {
            Optional<Hero> heroOpt = dataManager.findHeroById(heroId);
            if (heroOpt.isPresent()) {
                System.out.println(THIN_SEPARATOR);
                displayHero(heroOpt.get());
            }
        }
    }

    private void handleViewMatchHistory() {
        System.out.println();
        Player player = (Player) authService.getCurrentUser();
        List<String> matchIds = player.getMatchRecordIds();
        System.out.println("-- Match History (" + matchIds.size() + " matches) --");
        if (matchIds.isEmpty()) {
            System.out.println("No match history.");
            return;
        }
        int wins = 0;
        int losses = 0;
        for (String matchId : matchIds) {
            Optional<MatchRecord> matchOpt = dataManager.findMatchRecordById(matchId);
            if (matchOpt.isPresent()) {
                System.out.println(THIN_SEPARATOR);
                displayMatch(matchOpt.get());
                if (matchOpt.get().isWin()) {
                    wins++;
                } else {
                    losses++;
                }
            }
        }
        System.out.println(THIN_SEPARATOR);
        System.out.printf("Record: %dW / %dL (Win Rate: %.1f%%)%n",
                wins, losses,
                (wins + losses) > 0 ? (double) wins / (wins + losses) * 100 : 0.0);
    }

    private void handleViewLeaderboard() {
        System.out.println();
        System.out.println(THIN_SEPARATOR);
        System.out.println("  LEADERBOARD");
        System.out.println(THIN_SEPARATOR);
        System.out.println("  1. Top Players by Win Rate");
        System.out.println("  2. Top Players by Level");
        System.out.println("  3. Back");
        System.out.println(THIN_SEPARATOR);

        int choice = InputHelper.readInt("Enter choice: ", 1, 3);
        switch (choice) {
            case 1:
                displayTopByWinRate();
                break;
            case 2:
                displayTopByLevel();
                break;
            default:
                break;
        }
    }

    // ========================================================================
    // Display Helpers
    // ========================================================================

    private void displayPlayer(Player p) {
        String teamName = "-";
        if (p.getTeamId() != null) {
            Optional<Team> team = dataManager.findTeamById(p.getTeamId());
            teamName = team.map(Team::getName).orElse(p.getTeamId());
        }
        System.out.println("  ID:        " + p.getId());
        System.out.println("  Username:  " + p.getUsername());
        System.out.println("  Level:     " + p.getLevel());
        System.out.println("  Team:      " + teamName);
        System.out.println("  Matches:   " + p.getTotalMatches());
        System.out.println("  Wins:      " + p.getWins());
        System.out.println("  Losses:    " + p.getLosses());
        System.out.println("  Win Rate:  " + p.getFormattedWinRate());
        System.out.println("  Heroes:    " + p.getOwnedHeroIds().size());
        System.out.println("  Matches:   " + p.getMatchRecordIds().size() + " on record");
    }

    private void displayHero(Hero h) {
        System.out.println("  ID:        " + h.getId());
        System.out.println("  Name:      " + h.getName());
        System.out.println("  Type:      " + h.getType().getDisplayName());
        System.out.println("  Base HP:   " + h.getBaseHp());
        System.out.println("  Base ATK:  " + h.getBaseAttack());
        System.out.println("  Base DEF:  " + h.getBaseDefense());
        System.out.println("  Equipment: " + h.getCompatibleEquipmentIds().size() + " compatible");
    }

    private void displayEquipment(Equipment e) {
        System.out.println("  ID:        " + e.getId());
        System.out.println("  Name:      " + e.getName());
        System.out.println("  Type:      " + e.getType().getDisplayName());
        System.out.println("  ATK Bonus: " + e.getAttackBonus());
        System.out.println("  DEF Bonus: " + e.getDefenseBonus());
        System.out.println("  HP Bonus:  " + e.getHpBonus());
        System.out.println("  Usage:     " + e.getUsageCount());
        System.out.println("  Rating:    " + String.format("%.1f", e.getAverageRating()));
        System.out.println("  Score:     " + String.format("%.2f", e.getEquipmentScore()));
    }

    private void displayTeam(Team t) {
        System.out.println("  ID:        " + t.getId());
        System.out.println("  Name:      " + t.getName());
        System.out.println("  Members:   " + t.getMemberCount());
        System.out.print("  Roster:    ");
        for (int i = 0; i < t.getMemberIds().size(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            String mid = t.getMemberIds().get(i);
            Optional<Player> mp = dataManager.findPlayerById(mid);
            System.out.print(mp.map(Player::getUsername).orElse(mid));
        }
        System.out.println();
        System.out.println("  Matches:   " + t.getTotalMatches());
        System.out.println("  Wins:      " + t.getWins());
        System.out.println("  Losses:    " + t.getLosses());
        System.out.println("  Win Rate:  " + t.getFormattedWinRate());

        boolean hasTank = false;
        boolean hasSupport = false;
        for (String memberId : t.getMemberIds()) {
            Optional<Player> playerOpt = dataManager.findPlayerById(memberId);
            if (playerOpt.isPresent()) {
                for (String heroId : playerOpt.get().getOwnedHeroIds()) {
                    Optional<Hero> heroOpt = dataManager.findHeroById(heroId);
                    if (heroOpt.isPresent()) {
                        HeroType type = heroOpt.get().getType();
                        if (type == HeroType.TANK) {
                            hasTank = true;
                        }
                        if (type == HeroType.SUPPORT) {
                            hasSupport = true;
                        }
                    }
                }
            }
        }
        if (!hasTank || !hasSupport) {
            System.out.println("  [Warning] This team composition is suboptimal: Lacks a Tank or Support role!");
        }
    }

    private void displayMatch(MatchRecord m) {
        String teamName = "-";
        Optional<Team> team = dataManager.findTeamById(m.getTeamId());
        teamName = team.map(Team::getName).orElse(m.getTeamId());

        System.out.println("  ID:        " + m.getId());
        System.out.println("  Date:      " + (m.getMatchDate() != null
                ? m.getMatchDate().format(DATE_FMT) : "-"));
        System.out.println("  Team:      " + teamName);
        System.out.println("  Opponent:  " + m.getOpponentTeamName());
        System.out.println("  Result:    " + m.getResult().getDisplayName());
        System.out.print("  Players:   ");
        for (int i = 0; i < m.getPlayerIds().size(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            String pid = m.getPlayerIds().get(i);
            Optional<Player> pp = dataManager.findPlayerById(pid);
            System.out.print(pp.map(Player::getUsername).orElse(pid));
        }
        System.out.println();
        System.out.print("  Heroes:    ");
        for (int i = 0; i < m.getHeroIds().size(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            String hid = m.getHeroIds().get(i);
            Optional<Hero> hh = dataManager.findHeroById(hid);
            System.out.print(hh.map(Hero::getName).orElse(hid));
        }
        System.out.println();
    }

    // ========================================================================
    // Private Helpers
    // ========================================================================

    private Optional<Team> findTeamById(String teamId) {
        return dataManager.findTeamById(teamId);
    }

    private String truncate(String s, int maxLen) {
        if (s.length() <= maxLen) {
            return s;
        }
        return s.substring(0, maxLen - 1) + ".";
    }
}
