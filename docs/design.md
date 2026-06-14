# System Design Document – Honor of Kings IMS

## 1. Architectural Overview

The system follows a **layered architecture** with four tiers:

```
┌─────────────────────────────────────────┐
│        Presentation Layer                │
│  Main.java, InputHelper, DataInitializer │
├─────────────────────────────────────────┤
│          Service Layer                   │
│  AuthenticationService, SearchService,   │
│  RankingService, FileStorageService,     │
│  GameDataManager                         │
├─────────────────────────────────────────┤
│          Model Layer                     │
│  Person, Player, Admin, Hero, Equipment, │
│  Team, MatchRecord, Enums                │
├─────────────────────────────────────────┤
│        Interface Layer                   │
│  Persistable                             │
└─────────────────────────────────────────┘
```

**Key design rule**: Layers only depend downward. Model classes have zero knowledge of services. Services never reference presentation code. `Main` acts as the composition root, instantiating `GameDataManager` first, then injecting it into all services via constructor injection (manual DI, no framework).

## 2. Class Design

### 2.1 Inheritance Hierarchy

```
Person (abstract)
 ├── Player
 └── Admin
```

- `Person` is abstract—it cannot be instantiated directly. It defines shared identity fields (`id`, `username`, `password`, `role`) with validated setters.
- `Player` adds match statistics, hero ownership, and team membership.
- `Admin` is a minimal subclass with no extra fields; all admin behaviour is handled by the menu system.

### 2.2 Entity Relationships (ID-Based)

All cross-entity relationships use **String ID references** rather than direct object references:

| Relationship | Direction | Cardinality |
|-------------|-----------|-------------|
| Team – Player | Bidirectional | Team 1..5 Players, Player 0..1 Team |
| Player – Hero | Player -> Hero only | Player owns 0..* Heroes |
| Hero – Equipment | Hero -> Equipment only | Hero compatible with 0..* Equipment |
| MatchRecord – Team | MatchRecord -> Team only | Match belongs to 1 Team |
| MatchRecord – Player | Bidirectional | Many-to-many |
| MatchRecord – Hero | MatchRecord -> Hero only | Many-to-many |

This design keeps entities loosely coupled and simplifies CSV serialisation, since only String IDs need to be written to files, not entire object graphs.

### 2.3 Enums

| Enum | Values |
|------|--------|
| `Role` | `ADMIN`, `PLAYER` |
| `HeroType` | `TANK`, `FIGHTER`, `ASSASSIN`, `MAGE`, `MARKSMAN`, `SUPPORT` |
| `EquipmentType` | `ATTACK`, `DEFENSE`, `MAGIC`, `MOVEMENT` |
| `MatchResult` | `WIN`, `LOSS` |

## 3. Service Layer Design

### 3.1 GameDataManager – Central Data Store

All six entity collections (`players`, `admins`, `heroes`, `equipments`, `teams`, `matchRecords`) are owned by a single `GameDataManager` instance. It provides:

- **CRUD with duplicate prevention**: All `add*()` methods reject duplicate IDs.
- **Referential integrity on delete**: When a hero is deleted, it is automatically removed from all players' `ownedHeroIds`. When a team is deleted, all members become Free Agents. When a player is deleted, they are removed from their team's roster.

### 3.2 AuthenticationService – Session Management

- `login(username, password)` validates credentials against stored users and returns the matching `Person`.
- `getCurrentUser()` returns the logged-in user. `isAdmin()` / `isPlayer()` drive menu routing.
- The service is stateless except for `currentUser`; logout clears the session.

### 3.3 SearchService – Lookup Operations

Provides case-insensitive name search and exact ID search for Players, Teams, and Heroes. All methods return `Optional` to avoid null returns. Clean separation from `GameDataManager`—no direct collection access from presentation code.

### 3.4 RankingService – Leaderboard & Statistics

- Player ranking with a **unified tiebreaker chain**: winRate DESC → level DESC → totalMatches DESC → id ASC.
- The tiebreaker is a single `Comparator<Player>` constant reused by three ranking methods (win rate, level, match count), following the DRY principle.
- Equipment ranking uses the coursework formula: `score = usageCount * 0.5 + averageRating * 0.3 + heroUsageCount * 0.2`.

### 3.5 FileStorageService – CSV Persistence

- Implements the `Persistable` interface (`save()` / `load()`).
- Five CSV files: `players.csv`, `heroes.csv`, `equipment.csv`, `teams.csv`, `matches.csv`.
- Semicolon (`;`) as intra-cell separator for list fields to avoid comma collisions.
- Load order respects dependencies: Equipment → Heroes → Teams → Players → MatchRecords.
- Graceful handling: missing files on first run are silently skipped; malformed rows throw descriptive `IOException` with line numbers.

## 4. OOP Principles Applied

### Encapsulation

All model fields are `private`. Getters return unmodifiable views (`Collections.unmodifiableList()`). Setters enforce validation invariants at every mutation point:

- `Player.setLevel()` enforces [1, 30].
- `Player.setWins()` + `setLosses()` enforce wins + losses ≤ totalMatches.
- `Team.addMember()` enforces MAX_MEMBERS = 5.
- `Equipment.setAverageRating()` enforces [0.0, 5.0].

### Inheritance

`Person` is the abstract base class. `Player` and `Admin` inherit shared identity fields and validation logic. `AuthenticationService.login()` returns `Person`, and the presentation layer uses `isAdmin()` / `isPlayer()` to branch menus—this is Liskov substitution in practice.

### Abstraction

- **`Person` (abstract)**: Hides user identity behind a base type. Clients work with `Person` references without knowing the concrete subtype.
- **`Persistable` (interface)**: Declares `save()` / `load()`. The persistence mechanism (CSV) can be swapped without changing any service code.

### Polymorphism

- `currentUser` in `AuthenticationService` is typed `Person` but holds `Player` or `Admin` at runtime. `getRole()` returns the correct enum value.
- `RankingService` uses a single `Comparator<Player>` constant for all three ranking dimensions—polymorphic reuse of comparison logic.

### Separation of Concerns

| Concern | Layer |
|---------|-------|
| Console UI, menus | Presentation (`Main`) |
| Safe input parsing | Presentation (`InputHelper`) |
| Default data generation | Presentation (`DataInitializer`) |
| Authentication, session | Service (`AuthenticationService`) |
| Search operations | Service (`SearchService`) |
| Ranking algorithms | Service (`RankingService`) |
| Data storage, referential integrity | Service (`GameDataManager`) |
| CSV file I/O | Service (`FileStorageService`) |
| Domain entities, validation | Model (7 classes + 4 enums) |

## 5. Data Flow

### Startup

```
Main.start()
  → DataInitializer.initialize(dataManager)     // 2 admins, 3 teams, 15 players, 20 heroes, 25 equipment, 20 matches
  → FileStorageService.loadAllData()             // if CSV files exist, overwrite defaults
  → showLoginMenu()                              // enter main loop
```

### User Login

```
InputHelper → AuthenticationService.login(username, password)
  → searches all Admins, then all Players
  → returns Person (throws AuthenticationException on failure)
  → Main routes to showAdminMenu() or showPlayerMenu()
```

### CRUD Operation (e.g. Add Player)

```
Main.addPlayer()
  → InputHelper for ID, username, password, level, teamId
  → GameDataManager.findTeamById(teamId)          // validate team exists
  → Team.addMember(playerId)                      // enforce 5-member cap
  → GameDataManager.addPlayer(player)
```

### Persistence

```
Main.handleSaveData()
  → FileStorageService.saveAllData()
    → saveHeroes() → saveEquipment() → saveTeams() → savePlayers() → saveMatchRecords()
```

## 6. Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| String ID cross-references | Decouples entities, simplifies CSV serialisation |
| Defensive copying in RankingService | Prevents sorting from mutating global player list |
| While-loop-based menu navigation | No recursion—prevents stack overflow on long sessions |
| Semicolon list separator in CSV | Avoids comma escaping complexity |
| Clear-all-before-load in FileStorageService | Prevents duplicate data on repeated loads |
| Constructor injection (manual DI) | Keeps dependencies explicit without framework overhead |

## 7. Dataset

The default dataset (generated by `DataInitializer`) provides:

| Entity | Count |
|--------|-------|
| Admins | 2 |
| Teams | 3 |
| Players | 15 (5 per team) |
| Heroes | 20 |
| Equipment | 25 |
| Match Records | 20 |

All cross-references (team memberships, hero ownership, match participation) are fully wired.
