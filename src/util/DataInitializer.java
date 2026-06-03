package util;

import model.Admin;
import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Player;
import model.Team;
import model.enums.EquipmentType;
import model.enums.HeroType;
import model.enums.MatchResult;
import service.GameDataManager;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Generates the minimum dataset for the Honor of Kings IMS.
 *
 * <p>Creates 2 admins, 3 teams, 15 players, 20 heroes, 25 equipment items,
 * and 20 match records with proper cross-references between entities.</p>
 */
public class DataInitializer {

    private DataInitializer() {
    }

    /**
     * Populates the given {@link GameDataManager} with the default dataset.
     * Entities are created in dependency order: equipment, heroes, teams,
     * players, admins, match records.
     *
     * @param dm the data manager to populate
     * @throws IllegalArgumentException if dm is null
     */
    public static void initialize(GameDataManager dm) {
        if (dm == null) {
            throw new IllegalArgumentException("GameDataManager cannot be null.");
        }
        createEquipment(dm);
        createHeroes(dm);
        createTeams(dm);
        createPlayers(dm);
        createAdmins(dm);
        createMatchRecords(dm);
    }

    // ========================================================================
    // Equipment (25 items)
    // ========================================================================

    private static void createEquipment(GameDataManager dm) {
        // ATTACK type (7)
        dm.addEquipment(new Equipment("E001", "Bloodthirsty Sword", EquipmentType.ATTACK,
                45, 0, 0, 150, 4.5, 12));
        dm.addEquipment(new Equipment("E002", "Infinity Edge", EquipmentType.ATTACK,
                60, 0, 0, 200, 4.8, 15));
        dm.addEquipment(new Equipment("E003", "Shadow Blade", EquipmentType.ATTACK,
                35, 0, 200, 180, 4.3, 14));
        dm.addEquipment(new Equipment("E004", "Lightning Dagger", EquipmentType.ATTACK,
                30, 0, 0, 160, 4.1, 11));
        dm.addEquipment(new Equipment("E005", "Armor Piercer", EquipmentType.ATTACK,
                50, 0, 0, 140, 4.0, 10));
        dm.addEquipment(new Equipment("E006", "Swift Edge", EquipmentType.ATTACK,
                40, 5, 0, 130, 3.9, 9));
        dm.addEquipment(new Equipment("E007", "Berserker Axe", EquipmentType.ATTACK,
                55, 0, 100, 120, 4.4, 13));

        // DEFENSE type (7)
        dm.addEquipment(new Equipment("E008", "Thorn Armor", EquipmentType.DEFENSE,
                0, 50, 300, 190, 4.6, 16));
        dm.addEquipment(new Equipment("E009", "Guardian Totem", EquipmentType.DEFENSE,
                0, 45, 400, 170, 4.5, 14));
        dm.addEquipment(new Equipment("E010", "Immortal Shield", EquipmentType.DEFENSE,
                0, 40, 500, 185, 4.7, 17));
        dm.addEquipment(new Equipment("E011", "Frost Armor", EquipmentType.DEFENSE,
                0, 35, 250, 155, 4.2, 12));
        dm.addEquipment(new Equipment("E012", "Phoenix Eye", EquipmentType.DEFENSE,
                0, 30, 350, 140, 4.0, 10));
        dm.addEquipment(new Equipment("E013", "Holy Grail", EquipmentType.DEFENSE,
                10, 25, 200, 125, 4.1, 11));
        dm.addEquipment(new Equipment("E014", "Magic Cloak", EquipmentType.DEFENSE,
                0, 20, 150, 110, 3.8, 8));

        // MAGIC type (7)
        dm.addEquipment(new Equipment("E015", "Scepter of Wisdom", EquipmentType.MAGIC,
                55, 0, 0, 175, 4.6, 15));
        dm.addEquipment(new Equipment("E016", "Tome of Agony", EquipmentType.MAGIC,
                50, 0, 100, 165, 4.4, 13));
        dm.addEquipment(new Equipment("E017", "Arcane Staff", EquipmentType.MAGIC,
                45, 5, 0, 155, 4.2, 12));
        dm.addEquipment(new Equipment("E018", "Magic Crystal", EquipmentType.MAGIC,
                35, 0, 200, 145, 4.0, 10));
        dm.addEquipment(new Equipment("E019", "Spell Codex", EquipmentType.MAGIC,
                40, 0, 0, 135, 3.9, 9));
        dm.addEquipment(new Equipment("E020", "Void Staff", EquipmentType.MAGIC,
                60, 0, 0, 160, 4.5, 14));
        dm.addEquipment(new Equipment("E021", "Soul Stone", EquipmentType.MAGIC,
                30, 0, 150, 115, 3.8, 7));

        // MOVEMENT type (4)
        dm.addEquipment(new Equipment("E022", "Boots of Speed", EquipmentType.MOVEMENT,
                0, 5, 100, 210, 4.2, 18));
        dm.addEquipment(new Equipment("E023", "Shadow Boots", EquipmentType.MOVEMENT,
                10, 5, 50, 195, 4.0, 16));
        dm.addEquipment(new Equipment("E024", "Ninja Tabi", EquipmentType.MOVEMENT,
                0, 15, 0, 180, 3.9, 15));
        dm.addEquipment(new Equipment("E025", "Wind Chaser", EquipmentType.MOVEMENT,
                5, 0, 100, 170, 4.1, 14));
    }

    // ========================================================================
    // Heroes (20)
    // ========================================================================

    private static void createHeroes(GameDataManager dm) {
        // TANK (3)
        Hero h1 = new Hero("H001", "Zhang Fei", HeroType.TANK, 1500, 30, 60);
        h1.setCompatibleEquipmentIds(Arrays.asList("E008", "E009", "E010", "E011", "E022", "E024"));
        dm.addHero(h1);

        Hero h2 = new Hero("H002", "Xiang Yu", HeroType.TANK, 1400, 35, 55);
        h2.setCompatibleEquipmentIds(Arrays.asList("E008", "E010", "E011", "E013", "E022"));
        dm.addHero(h2);

        Hero h3 = new Hero("H003", "Lian Po", HeroType.TANK, 1450, 25, 65);
        h3.setCompatibleEquipmentIds(Arrays.asList("E009", "E010", "E012", "E014", "E024"));
        dm.addHero(h3);

        // FIGHTER (4)
        Hero h4 = new Hero("H004", "Lu Bu", HeroType.FIGHTER, 1000, 70, 35);
        h4.setCompatibleEquipmentIds(Arrays.asList("E001", "E003", "E006", "E011", "E022", "E023"));
        dm.addHero(h4);

        Hero h5 = new Hero("H005", "Arthur", HeroType.FIGHTER, 1100, 55, 45);
        h5.setCompatibleEquipmentIds(Arrays.asList("E001", "E006", "E007", "E011", "E022"));
        dm.addHero(h5);

        Hero h6 = new Hero("H006", "Zhao Yun", HeroType.FIGHTER, 1050, 60, 40);
        h6.setCompatibleEquipmentIds(Arrays.asList("E002", "E003", "E007", "E012", "E023"));
        dm.addHero(h6);

        Hero h7 = new Hero("H007", "Luna", HeroType.FIGHTER, 950, 75, 30);
        h7.setCompatibleEquipmentIds(Arrays.asList("E001", "E002", "E004", "E023", "E025"));
        dm.addHero(h7);

        // ASSASSIN (3)
        Hero h8 = new Hero("H008", "Li Bai", HeroType.ASSASSIN, 800, 90, 20);
        h8.setCompatibleEquipmentIds(Arrays.asList("E002", "E003", "E004", "E023", "E025"));
        dm.addHero(h8);

        Hero h9 = new Hero("H009", "Han Xin", HeroType.ASSASSIN, 750, 85, 25);
        h9.setCompatibleEquipmentIds(Arrays.asList("E002", "E004", "E005", "E023", "E025"));
        dm.addHero(h9);

        Hero h10 = new Hero("H010", "Prince of Lanling", HeroType.ASSASSIN, 700, 95, 15);
        h10.setCompatibleEquipmentIds(Arrays.asList("E002", "E003", "E005", "E023"));
        dm.addHero(h10);

        // MAGE (4)
        Hero h11 = new Hero("H011", "Diao Chan", HeroType.MAGE, 650, 95, 10);
        h11.setCompatibleEquipmentIds(Arrays.asList("E015", "E016", "E017", "E019", "E020"));
        dm.addHero(h11);

        Hero h12 = new Hero("H012", "Angela", HeroType.MAGE, 700, 88, 12);
        h12.setCompatibleEquipmentIds(Arrays.asList("E015", "E017", "E018", "E020", "E021"));
        dm.addHero(h12);

        Hero h13 = new Hero("H013", "Zhen Ji", HeroType.MAGE, 680, 90, 10);
        h13.setCompatibleEquipmentIds(Arrays.asList("E016", "E017", "E019", "E020", "E021"));
        dm.addHero(h13);

        Hero h14 = new Hero("H014", "Xiao Qiao", HeroType.MAGE, 620, 92, 8);
        h14.setCompatibleEquipmentIds(Arrays.asList("E015", "E016", "E018", "E019"));
        dm.addHero(h14);

        // MARKSMAN (4)
        Hero h15 = new Hero("H015", "Hou Yi", HeroType.MARKSMAN, 750, 80, 18);
        h15.setCompatibleEquipmentIds(Arrays.asList("E001", "E002", "E004", "E005", "E022"));
        dm.addHero(h15);

        Hero h16 = new Hero("H016", "Sun Shangxiang", HeroType.MARKSMAN, 700, 85, 15);
        h16.setCompatibleEquipmentIds(Arrays.asList("E001", "E002", "E004", "E005", "E006"));
        dm.addHero(h16);

        Hero h17 = new Hero("H017", "Marco Polo", HeroType.MARKSMAN, 680, 88, 12);
        h17.setCompatibleEquipmentIds(Arrays.asList("E002", "E003", "E005", "E007", "E023"));
        dm.addHero(h17);

        Hero h18 = new Hero("H018", "Yi Xing", HeroType.MARKSMAN, 720, 82, 16);
        h18.setCompatibleEquipmentIds(Arrays.asList("E001", "E003", "E004", "E006", "E025"));
        dm.addHero(h18);

        // SUPPORT (2)
        Hero h19 = new Hero("H019", "Cai Wenji", HeroType.SUPPORT, 600, 40, 15);
        h19.setCompatibleEquipmentIds(Arrays.asList("E009", "E013", "E014", "E021", "E022"));
        dm.addHero(h19);

        Hero h20 = new Hero("H020", "Da Qiao", HeroType.SUPPORT, 580, 45, 12);
        h20.setCompatibleEquipmentIds(Arrays.asList("E008", "E013", "E014", "E018", "E022"));
        dm.addHero(h20);
    }

    // ========================================================================
    // Teams (3)
    // ========================================================================

    private static void createTeams(GameDataManager dm) {
        dm.addTeam(new Team("T001", "Team Alpha", 45, 28));
        dm.addTeam(new Team("T002", "Team Beta", 38, 20));
        dm.addTeam(new Team("T003", "Team Gamma", 42, 25));
    }

    // ========================================================================
    // Players (15 — 5 per team)
    // ========================================================================

    private static void createPlayers(GameDataManager dm) {
        // Team Alpha (T001)
        Player p1 = new Player("P001", "AlphaWolf", "pass001", 25, 120, 78, 42, "T001");
        p1.setOwnedHeroIds(Arrays.asList("H001", "H004", "H008", "H011", "H015", "H019"));
        p1.setMatchRecordIds(Arrays.asList(
                "M001", "M002", "M003", "M004", "M005", "M006", "M007", "M008"));
        dm.addPlayer(p1);

        Player p2 = new Player("P002", "AlphaTiger", "pass002", 18, 85, 50, 35, "T001");
        p2.setOwnedHeroIds(Arrays.asList("H002", "H005", "H009", "H012", "H016"));
        p2.setMatchRecordIds(Arrays.asList("M001", "M003", "M005", "M007", "M009", "M010"));
        dm.addPlayer(p2);

        Player p3 = new Player("P003", "AlphaDragon", "pass003", 30, 200, 135, 65, "T001");
        p3.setOwnedHeroIds(Arrays.asList("H003", "H006", "H007", "H010", "H017", "H020"));
        p3.setMatchRecordIds(Arrays.asList(
                "M001", "M002", "M004", "M006", "M008", "M009", "M011",
                "M012", "M013", "M014"));
        dm.addPlayer(p3);

        Player p4 = new Player("P004", "AlphaPhoenix", "pass004", 15, 60, 32, 28, "T001");
        p4.setOwnedHeroIds(Arrays.asList("H001", "H008", "H013", "H015", "H018"));
        p4.setMatchRecordIds(Arrays.asList("M002", "M004", "M006", "M008", "M010"));
        dm.addPlayer(p4);

        Player p5 = new Player("P005", "AlphaEagle", "pass005", 22, 95, 58, 37, "T001");
        p5.setOwnedHeroIds(Arrays.asList("H004", "H007", "H011", "H014", "H016", "H019"));
        p5.setMatchRecordIds(Arrays.asList("M003", "M005", "M007", "M009", "M011", "M012"));
        dm.addPlayer(p5);

        // Team Beta (T002)
        Player p6 = new Player("P006", "BetaStorm", "pass006", 20, 90, 48, 42, "T002");
        p6.setOwnedHeroIds(Arrays.asList("H002", "H005", "H009", "H012", "H016", "H020"));
        p6.setMatchRecordIds(Arrays.asList("M013", "M014", "M015", "M016", "M017"));
        dm.addPlayer(p6);

        Player p7 = new Player("P007", "BetaShadow", "pass007", 28, 150, 95, 55, "T002");
        p7.setOwnedHeroIds(Arrays.asList("H003", "H006", "H010", "H013", "H017", "H018"));
        p7.setMatchRecordIds(Arrays.asList(
                "M013", "M015", "M016", "M017", "M018", "M019", "M020"));
        dm.addPlayer(p7);

        Player p8 = new Player("P008", "BetaBlade", "pass008", 12, 40, 18, 22, "T002");
        p8.setOwnedHeroIds(Arrays.asList("H001", "H004", "H008", "H014", "H015"));
        p8.setMatchRecordIds(Arrays.asList("M014", "M016", "M018", "M020"));
        dm.addPlayer(p8);

        Player p9 = new Player("P009", "BetaFrost", "pass009", 16, 70, 35, 35, "T002");
        p9.setOwnedHeroIds(Arrays.asList("H005", "H007", "H009", "H011", "H019"));
        p9.setMatchRecordIds(Arrays.asList("M013", "M015", "M017", "M019", "M020"));
        dm.addPlayer(p9);

        Player p10 = new Player("P010", "BetaFlame", "pass010", 24, 110, 72, 38, "T002");
        p10.setOwnedHeroIds(Arrays.asList("H002", "H006", "H010", "H012", "H016", "H020"));
        p10.setMatchRecordIds(Arrays.asList(
                "M014", "M015", "M016", "M018", "M019", "M020"));
        dm.addPlayer(p10);

        // Team Gamma (T003)
        Player p11 = new Player("P011", "GammaRay", "pass011", 27, 140, 90, 50, "T003");
        p11.setOwnedHeroIds(Arrays.asList("H003", "H007", "H010", "H013", "H017", "H020"));
        p11.setMatchRecordIds(Arrays.asList(
                "M005", "M006", "M011", "M012", "M017", "M018"));
        dm.addPlayer(p11);

        Player p12 = new Player("P012", "GammaKnight", "pass012", 19, 80, 45, 35, "T003");
        p12.setOwnedHeroIds(Arrays.asList("H001", "H004", "H008", "H011", "H015"));
        p12.setMatchRecordIds(Arrays.asList("M006", "M008", "M012", "M014", "M019"));
        dm.addPlayer(p12);

        Player p13 = new Player("P013", "GammaMage", "pass013", 14, 55, 28, 27, "T003");
        p13.setOwnedHeroIds(Arrays.asList("H011", "H012", "H013", "H014", "H019"));
        p13.setMatchRecordIds(Arrays.asList("M007", "M009", "M013", "M015", "M020"));
        dm.addPlayer(p13);

        Player p14 = new Player("P014", "GammaArrow", "pass014", 21, 100, 62, 38, "T003");
        p14.setOwnedHeroIds(Arrays.asList("H005", "H009", "H015", "H016", "H017", "H018"));
        p14.setMatchRecordIds(Arrays.asList("M005", "M010", "M011", "M016", "M018", "M019"));
        dm.addPlayer(p14);

        Player p15 = new Player("P015", "GammaShield", "pass015", 17, 75, 40, 35, "T003");
        p15.setOwnedHeroIds(Arrays.asList("H001", "H002", "H003", "H006", "H020"));
        p15.setMatchRecordIds(Arrays.asList("M008", "M010", "M012", "M015", "M017", "M020"));
        dm.addPlayer(p15);

        // Add members to teams
        for (Team t : dm.getAllTeams()) {
            for (Player p : dm.getAllPlayers()) {
                if (t.getId().equals(p.getTeamId())) {
                    t.addMember(p.getId());
                }
            }
        }
    }

    // ========================================================================
    // Admins (2)
    // ========================================================================

    private static void createAdmins(GameDataManager dm) {
        dm.addAdmin(new Admin("ADM001", "admin", "admin123"));
        dm.addAdmin(new Admin("ADM002", "superadmin", "super123"));
    }

    // ========================================================================
    // Match Records (20)
    // ========================================================================

    private static void createMatchRecords(GameDataManager dm) {
        LocalDate base = LocalDate.of(2026, 3, 1);

        // Team Alpha matches (M001-M008)
        MatchRecord m1 = new MatchRecord("M001", base.plusDays(0), "T001",
                "Team Vortex", MatchResult.WIN);
        m1.setPlayerIds(Arrays.asList("P001", "P002", "P003"));
        m1.setHeroIds(Arrays.asList("H001", "H008", "H015"));
        dm.addMatchRecord(m1);

        MatchRecord m2 = new MatchRecord("M002", base.plusDays(3), "T001",
                "Team Thunder", MatchResult.WIN);
        m2.setPlayerIds(Arrays.asList("P001", "P003", "P004"));
        m2.setHeroIds(Arrays.asList("H004", "H011", "H013"));
        dm.addMatchRecord(m2);

        MatchRecord m3 = new MatchRecord("M003", base.plusDays(7), "T001",
                "Team Cyclone", MatchResult.LOSS);
        m3.setPlayerIds(Arrays.asList("P001", "P002", "P005"));
        m3.setHeroIds(Arrays.asList("H002", "H005", "H009"));
        dm.addMatchRecord(m3);

        MatchRecord m4 = new MatchRecord("M004", base.plusDays(10), "T001",
                "Team Phantom", MatchResult.WIN);
        m4.setPlayerIds(Arrays.asList("P001", "P003", "P004"));
        m4.setHeroIds(Arrays.asList("H003", "H006", "H017"));
        dm.addMatchRecord(m4);

        MatchRecord m5 = new MatchRecord("M005", base.plusDays(14), "T001",
                "Team Gamma", MatchResult.WIN);
        m5.setPlayerIds(Arrays.asList("P002", "P011", "P014"));
        m5.setHeroIds(Arrays.asList("H012", "H016", "H009"));
        dm.addMatchRecord(m5);

        MatchRecord m6 = new MatchRecord("M006", base.plusDays(17), "T001",
                "Team Gamma", MatchResult.LOSS);
        m6.setPlayerIds(Arrays.asList("P003", "P004", "P011", "P012"));
        m6.setHeroIds(Arrays.asList("H007", "H010", "H004", "H008"));
        dm.addMatchRecord(m6);

        MatchRecord m7 = new MatchRecord("M007", base.plusDays(21), "T001",
                "Team Nova", MatchResult.WIN);
        m7.setPlayerIds(Arrays.asList("P001", "P002", "P005", "P013"));
        m7.setHeroIds(Arrays.asList("H014", "H019", "H005", "H009"));
        dm.addMatchRecord(m7);

        MatchRecord m8 = new MatchRecord("M008", base.plusDays(25), "T001",
                "Team Eclipse", MatchResult.LOSS);
        m8.setPlayerIds(Arrays.asList("P001", "P003", "P004", "P012", "P015"));
        m8.setHeroIds(Arrays.asList("H015", "H018", "H001", "H003", "H020"));
        dm.addMatchRecord(m8);

        // Team Beta matches (M009-M014)
        MatchRecord m9 = new MatchRecord("M009", base.plusDays(5), "T002",
                "Team Blizzard", MatchResult.WIN);
        m9.setPlayerIds(Arrays.asList("P002", "P003", "P005", "P013"));
        m9.setHeroIds(Arrays.asList("H005", "H016", "H011"));
        dm.addMatchRecord(m9);

        MatchRecord m10 = new MatchRecord("M010", base.plusDays(9), "T002",
                "Team Inferno", MatchResult.LOSS);
        m10.setPlayerIds(Arrays.asList("P002", "P004", "P014", "P015"));
        m10.setHeroIds(Arrays.asList("H002", "H008", "H015", "H006"));
        dm.addMatchRecord(m10);

        MatchRecord m11 = new MatchRecord("M011", base.plusDays(13), "T002",
                "Team Gamma", MatchResult.WIN);
        m11.setPlayerIds(Arrays.asList("P003", "P005", "P011", "P014"));
        m11.setHeroIds(Arrays.asList("H007", "H006", "H017", "H013"));
        dm.addMatchRecord(m11);

        MatchRecord m12 = new MatchRecord("M012", base.plusDays(18), "T002",
                "Team Gamma", MatchResult.LOSS);
        m12.setPlayerIds(Arrays.asList("P003", "P005", "P011", "P012", "P015"));
        m12.setHeroIds(Arrays.asList("H004", "H010", "H012", "H001", "H020"));
        dm.addMatchRecord(m12);

        MatchRecord m13 = new MatchRecord("M013", base.plusDays(30), "T002",
                "Team Alpha", MatchResult.WIN);
        m13.setPlayerIds(Arrays.asList("P003", "P006", "P007", "P009", "P013"));
        m13.setHeroIds(Arrays.asList("H009", "H006", "H002", "H013", "H019"));
        dm.addMatchRecord(m13);

        MatchRecord m14 = new MatchRecord("M014", base.plusDays(35), "T002",
                "Team Alpha", MatchResult.LOSS);
        m14.setPlayerIds(Arrays.asList("P003", "P006", "P008", "P010", "P012"));
        m14.setHeroIds(Arrays.asList("H016", "H012", "H005", "H008", "H015"));
        dm.addMatchRecord(m14);

        // Team Gamma matches with other opponents (M015-M020)
        MatchRecord m15 = new MatchRecord("M015", base.plusDays(2), "T003",
                "Team Storm", MatchResult.WIN);
        m15.setPlayerIds(Arrays.asList("P006", "P007", "P009", "P013", "P015"));
        m15.setHeroIds(Arrays.asList("H017", "H007", "H010", "H003", "H020"));
        dm.addMatchRecord(m15);

        MatchRecord m16 = new MatchRecord("M016", base.plusDays(6), "T003",
                "Team Tempest", MatchResult.WIN);
        m16.setPlayerIds(Arrays.asList("P006", "P007", "P008", "P010", "P014"));
        m16.setHeroIds(Arrays.asList("H018", "H006", "H005", "H002", "H012"));
        dm.addMatchRecord(m16);

        MatchRecord m17 = new MatchRecord("M017", base.plusDays(12), "T003",
                "Team Beta", MatchResult.LOSS);
        m17.setPlayerIds(Arrays.asList("P006", "P007", "P009", "P011", "P015"));
        m17.setHeroIds(Arrays.asList("H013", "H003", "H010", "H007", "H017"));
        dm.addMatchRecord(m17);

        MatchRecord m18 = new MatchRecord("M018", base.plusDays(16), "T003",
                "Team Beta", MatchResult.WIN);
        m18.setPlayerIds(Arrays.asList("P007", "P008", "P010", "P011", "P014"));
        m18.setHeroIds(Arrays.asList("H011", "H018", "H014", "H006", "H002"));
        dm.addMatchRecord(m18);

        MatchRecord m19 = new MatchRecord("M019", base.plusDays(20), "T003",
                "Team Alpha", MatchResult.LOSS);
        m19.setPlayerIds(Arrays.asList("P007", "P009", "P010", "P012", "P014"));
        m19.setHeroIds(Arrays.asList("H009", "H012", "H005", "H015", "H019"));
        dm.addMatchRecord(m19);

        MatchRecord m20 = new MatchRecord("M020", base.plusDays(24), "T003",
                "Team Alpha", MatchResult.WIN);
        m20.setPlayerIds(Arrays.asList("P007", "P008", "P009", "P010", "P013", "P015"));
        m20.setHeroIds(Arrays.asList("H016", "H003", "H020", "H007", "H010", "H002"));
        dm.addMatchRecord(m20);
    }
}
