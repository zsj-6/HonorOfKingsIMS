# Design Decisions

## 1. Why Three Layers

I split the system into three layers: model, service, and presentation.

The model layer only holds data and basic validation. Classes like Player and
Hero do not know anything about menus or file loading.

The service layer does all the real work — searching, ranking, login, saving
files. But it never prints to the console or reads user input.

The presentation layer (Main.java and InputHelper) is just menus and display.
It calls services but never touches model collections directly.

If I wanted to add a GUI later, I could swap out the presentation layer
without rewriting any service code. That is the main benefit of separating
them.

## 2. Why String IDs Instead of Object References

A Player does not hold a list of Hero objects. It holds a list of hero ID
strings. Same for Team members and MatchRecord participants.

This avoids circular references. If Player holds Hero objects and Hero holds
Equipment objects, CSV serialisation becomes a nightmare. With ID strings, I
just write "H001;H002;H003" into one CSV cell.

GameDataManager acts as a lookup service. When I need a hero's name, I call
dataManager.findHeroById(id). This keeps everything loosely connected.

## 3. Why CSV Instead of JSON

I chose CSV because the coursework says we can only use standard Java I/O.
No external libraries like Gson or Jackson.

CSV is also really easy to debug. I can open players.csv in any text editor
or Excel and see all 15 players immediately. If I used JSON, one missing
bracket would break the whole file.

For list fields like team member IDs, I use semicolons inside a cell:
"P001;P002;P003". This avoids conflict with the CSV comma delimiter.

JSON would be better for nested data, and a database would be better for a
real system. But for this coursework, CSV is the right fit — simple,
transparent, and zero dependencies.

## 4. Why Person Is Abstract

Person holds id, username, password, and role. Player and Admin both extend
it. I did not want to write the same getters and setters twice.

AuthenticationService.login() returns a Person reference, not caring whether
it is a Player or Admin. The menu checks the role later with isAdmin(). That
is polymorphism in practice — one variable type handling two different
objects.

## 5. Why a Central GameDataManager

All data lives in one GameDataManager instance. Every service gets it through
the constructor.

This prevents inconsistency. When deleteHero() is called, it also removes
that hero from every player's owned hero list. If each service kept its own
copy of the data, these cleanups would not happen.

One source of truth. One place to debug.

## 6. Why Defensive Copies

All collection getters return Collections.unmodifiableList(). This stops
external code from accidentally changing internal lists.

RankingService creates a new ArrayList before sorting. If it sorted the
original list directly, the leaderboard view would permanently scramble the
player order for every other feature. Defensive copying prevents that.

## 7. Why a TIEBREAKER Constant

The coursework requires a specific tiebreaker order: win rate, then level,
then matches, then player ID.

Instead of writing this comparison three times (once per leaderboard view),
I put it in a single static final TIEBREAKER constant. All three ranking
methods call sorted.sort(TIEBREAKER).

One place to read the tiebreaker rules. One place to fix them if they change.

## 8. Why Loops Instead of Recursion

Every menu uses while(true) with return to go back. No method calls itself.

If menus used recursion, navigating back and forth for a long time would
eventually cause a StackOverflowError. Loops use the same amount of memory
no matter how long the session lasts.

---

# System Architecture

## 1. Architectural Overview

The system follows a **layered architecture** with four tiers:

```
┌───────────────────────────────────┐
│       Presentation Layer          │
│ Main.java, InputHelper,           │
│ DataInitializer                   │
├───────────────────────────────────┤
│         Service Layer             │
│ AuthenticationService,            │
│ SearchService, RankingService,    │
│ FileStorageService,               │
│ GameDataManager                   │
├───────────────────────────────────┤
│         Model Layer               │
│ Person, Player, Admin, Hero,      │
│ Equipment, Team, MatchRecord,     │
│ Enums                             │
├───────────────────────────────────┤
│       Interface Layer             │
│ Persistable                       │
└───────────────────────────────────┘
```

**Key design rule**: Layers only depend downward. Model classes have zero
knowledge of services. Services never reference presentation code. `Main`
acts as the composition root, instantiating `GameDataManager` first, then
injecting it into all services via constructor injection (manual DI, no
framework).

## 2. Class Design

### 2.1 Inheritance Hierarchy

```
Person (abstract)
 ├── Player
 └── Admin
```

- `Person` is abstract — it cannot be instantiated directly. It defines
  shared identity fields (`id`, `username`, `password`, `role`) with
  validated setters.
- `Player` adds match statistics, hero ownership, and team membership.
- `Admin` is a minimal subclass with no extra fields; all admin behaviour
  is handled by the menu system.

### 2.2 Entity Relationships (ID-Based)

All cross-entity relationships use **String ID references** rather than
direct object references:

| Relationship | Direction | Cardinality |
|---|---|---|
| Team ↔ Player | Bidirectional | Team 1..5 Players, Player 0..1 Team |
| Player → Hero | Player → Hero only | Player owns 0..* Heroes |
| Hero → Equipment | Hero → Equipment only | Hero compatible with 0..* Equipment |
| MatchRecord → Team | MatchRecord → Team only | Match belongs to 1 Team |
| MatchRecord ↔ Player | Bidirectional | Many-to-many |
| MatchRecord → Hero | MatchRecord → Hero only | Many-to-many |

### 2.3 Enums

| Enum | Values |
|---|---|
| `Role` | `ADMIN`, `PLAYER` |
| `HeroType` | `TANK`, `FIGHTER`, `ASSASSIN`, `MAGE`, `MARKSMAN`, `SUPPORT` |
| `EquipmentType` | `ATTACK`, `DEFENSE`, `MAGIC`, `MOVEMENT` |
| `MatchResult` | `WIN`, `LOSS` |

## 3. Service Layer Design

### 3.1 GameDataManager — Central Data Store

All six entity collections (`players`, `admins`, `heroes`, `equipments`,
`teams`, `matchRecords`) are owned by a single `GameDataManager` instance.
It provides:

- **CRUD with duplicate prevention**: All `add*()` methods reject duplicate IDs.
- **Referential integrity on delete**: When a hero is deleted, it is
  automatically removed from all players' `ownedHeroIds`. When a team is
  deleted, all members become Free Agents. When a player is deleted, they
  are removed from their team's roster.

### 3.2 AuthenticationService — Session Management

- `login(username, password)` validates credentials against stored users
  and returns the matching `Person`.
- `getCurrentUser()` returns the logged-in user. `isAdmin()` / `isPlayer()`
  drive menu routing.
- The service is stateless except for `currentUser`; logout clears the session.

### 3.3 SearchService — Lookup Operations

Provides case-insensitive name search and exact ID search for Players,
Teams, and Heroes. All methods return `Optional` to avoid null returns.

### 3.4 RankingService — Leaderboard & Statistics

- Player ranking with a **unified tiebreaker chain**:
  winRate DESC → level DESC → totalMatches DESC → id ASC.
- Equipment ranking uses the coursework formula:
  `score = usageCount * 0.5 + averageRating * 0.3 + heroUsageCount * 0.2`.

### 3.5 FileStorageService — CSV Persistence

- Implements the `Persistable` interface (`save()` / `load()`).
- Five CSV files: `players.csv`, `heroes.csv`, `equipment.csv`, `teams.csv`,
  `matches.csv`.
- Semicolon (`;`) as intra-cell separator for list fields.
- Load order respects dependencies: Equipment → Heroes → Teams → Players →
  MatchRecords.

## 4. OOP Principles Applied

### Encapsulation

All model fields are `private`. Getters return unmodifiable views. Setters
enforce validation invariants:

- `Player.setLevel()` enforces [1, 30].
- `Player.setWins()` + `setLosses()` enforce wins + losses ≤ totalMatches.
- `Team.addMember()` enforces MAX_MEMBERS = 5.
- `Equipment.setAverageRating()` enforces [0.0, 5.0].

### Inheritance

`Person` is the abstract base class. `Player` and `Admin` inherit shared
identity fields and validation logic. `AuthenticationService.login()`
returns `Person`, and the presentation layer uses `isAdmin()` / `isPlayer()`
to branch menus — Liskov substitution in practice.

### Polymorphism

- `currentUser` in `AuthenticationService` is typed `Person` but holds
  `Player` or `Admin` at runtime.
- `RankingService` uses a single `Comparator<Player>` constant for all three
  ranking dimensions.

### Separation of Concerns

| Concern | Layer |
|---|---|
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
  → DataInitializer.initialize(dataManager)  // 2 admins, 3 teams, 15
                                             // players, 20 heroes, 25
                                             // equipment, 20 matches
  → FileStorageService.loadAllData()         // if CSV files exist,
                                             // overwrite defaults
  → showLoginMenu()                          // enter main loop
```

### User Login

```
InputHelper → AuthenticationService.login(username, password)
  → searches all Admins, then all Players
  → returns Person (throws AuthenticationException on failure)
  → Main routes to showAdminMenu() or showPlayerMenu()
```

### Persistence

```
Main.handleSaveData()
  → FileStorageService.saveAllData()
    → saveHeroes() → saveEquipment() → saveTeams()
    → savePlayers() → saveMatchRecords()
```

## 6. Dataset

The default dataset (generated by `DataInitializer`) provides:

| Entity | Count |
|---|---|
| Admins | 2 |
| Teams | 3 |
| Players | 15 (5 per team) |
| Heroes | 20 |
| Equipment | 25 |
| Match Records | 20 |

All cross-references (team memberships, hero ownership, match participation)
are fully wired.
