# AI-Assisted Honor of Kings Information Management System

Author: ZhangShijie

---

# 1. Project Goal

## Project Overview

This project aims to develop an AI-assisted Information Management System (IMS) for Honor of Kings using Java Object-Oriented Programming principles.

The system manages:

- Players
- Heroes
- Equipment
- Teams
- Match Records

The system provides:

- Authentication
- Data Management
- Search Functions
- Team Statistics
- Match History
- Equipment Statistics
- Leaderboards
- CSV Data Persistence

The target users are:

1. Administrators
2. Players

Administrators have full management permissions while Players have limited access permissions.

---

# 2. Requirement Analysis

## Functional Requirements

### 2.1 Player Lookup

Users can search players by:

- Player ID
- Player Name

Displayed information:

- ID
- Name
- Team
- Level
- Win Rate
- Owned Heroes
- Equipped Items

### 2.2 Team Overview

### 2.2 Team Overview

Users can search teams by:

- Team ID
- Team Name

Displayed information:

- Team Name
- Members
- Average Level
- Total Matches
- Win Rate
- Top Player

Team Composition Check:

The system analyzes the heroes owned by team members and evaluates whether the team has a balanced hero pool.

The analysis checks for essential roles such as:

- Tank
- Fighter
- Assassin
- Mage
- Marksman
- Support

If important roles are missing, the system will display recommendations to improve team composition.

### 2.3 Hero Details

Users can search heroes by name.

Displayed information:

- Hero Name
- Hero Type
- Base Statistics
- Compatible Equipment
- Players Owning Hero

### 2.4 Equipment Statistics

The system ranks equipment based on:

Equipment Score =
Usage Count × 0.5 +
Average Rating × 0.3 +
Hero Usage Count × 0.2

Displayed information:

- Equipment Name
- Usage Count
- Average Rating
- Equipment Score

### 2.5 Match History

Users can retrieve recent matches.

Displayed information:

- Match Date
- Opponent
- Result
- Heroes Used
- Win/Loss Record

### 2.6 Leaderboard

Players are ranked by:

- Win Rate
- Level
- Match Count

Tie-breaking rules:

1. Higher Win Rate
2. Higher Level
3. More Matches
4. Player ID

### 2.7 Data Management

Admin Users:

- Add Player
- Delete Player
- Edit Player
- Add Hero
- Delete Hero
- Add Equipment
- Delete Equipment
- Manage Teams
- Manage Match Records

Player Users:

- View Own Information
- Edit Basic Profile
- View Match History
- View Public Information

### 2.8 Authentication

Role-based login:

- Admin
- Player

Permissions are enforced through Role checking.

---

# 3. Java Concepts Used

## Inheritance

Person (Abstract)
├── Player
└── Admin

Benefits:

- Shared user attributes
- Reduced code duplication
- Better extensibility

## Interfaces

### Searchable

Provides:

- searchById()
- searchByName()

Implementation:

- SearchService implements Searchable

Purpose:

Provides a unified interface for player, team, and hero search operations.

### Persistable

Provides:

- save()
- load()

Implementation:

- FileStorageService implements Persistable

Alternative Design:

If persistence logic is centralized, GameDataManager may implement Persistable and delegate file operations to FileStorageService.

Purpose:

Provides a standardized mechanism for saving and loading system data.
## Polymorphism

Examples:

Person user;

user = new Player();

user = new Admin();

The system processes different subclasses through common references.

## Collections

ArrayList:

- Players
- Heroes
- Equipment
- Match Records

HashMap:

- Fast ID lookup

HashSet:

- Prevent duplicate data

## Encapsulation

All attributes are private.

Access is controlled through:

- Getters
- Setters
- Validation Methods

## Exception Handling

Handle:

- Invalid Input
- Duplicate IDs
- Missing Records
- Login Failure
- File Read Errors
- File Write Errors

## File I/O

CSV files:

- players.csv
- heroes.csv
- equipment.csv
- teams.csv
- matches.csv

## Enums

Role

- ADMIN
- PLAYER

HeroType

- TANK
- FIGHTER
- ASSASSIN
- MAGE
- MARKSMAN
- SUPPORT

EquipmentType

- ATTACK
- DEFENSE
- MAGIC
- MOVEMENT

MatchResult

- WIN
- LOSS

---

# 4. Class Design

## Person (Abstract)


Responsibilities:

- User ID
- Username
- Password
- Role

## Player

Responsibilities:

- Level
- Win Rate
- Owned Heroes
- Match History

## Admin

Responsibilities:

- System Management Privileges

## Hero

Responsibilities:

- Hero Information
- Hero Statistics
- Equipment Compatibility

## Equipment

Responsibilities:

- Equipment Information
- Statistics

## Team

Responsibilities:

- Team Information
- Member Management

## MatchRecord

Responsibilities:

- Match Result
- Match Date
- Hero Selection
- Opponent Information

## GameDataManager

Responsibilities:

- Central Data Storage
- CRUD Operations

## AuthenticationService

Responsibilities:

- Login
- Logout
- Permission Validation

## SearchService

Responsibilities:

- Search Operations

## RankingService

Responsibilities:

- Equipment Ranking
- Player Ranking

## FileStorageService

Responsibilities:

- CSV Save
- CSV Load

## InputHelper

Responsibilities:

- Safe User Input

## DataInitializer

Responsibilities:

- Generate Initial Dataset

---

# 5. UML Draft

Person
|
+-- Player
|
+-- Admin

Player ---- owns ---- Hero

Hero ---- uses ---- Equipment

Team ---- contains ---- Player

MatchRecord ---- references ---- Player

MatchRecord ---- references ---- Hero

AuthenticationService
SearchService
RankingService
FileStorageService

all managed by

GameDataManager
Note:

A detailed visual UML diagram (uml.png) illustrating class attributes, methods, inheritance relationships, associations, and cardinality (for example, one-to-many relationships between Team and Player) will be provided in the docs/ directory.
---

# 6. Data Design

Minimum Dataset

Teams:
- 3

Players:
- 15

Heroes:
- 20

Equipment:
- 25

Match Records:
- 20

Storage Method:

Runtime:
- ArrayList
- HashMap

Persistence:
- CSV Files

---

# 7. AI Usage Plan

## Architect Agent (ChatGPT)

Responsibilities:

- Requirement Analysis
- UML Design
- OOP Architecture
- Documentation Planning

## Implementation Agent (Claude Code)

Responsibilities:

- Model Layer
- Service Layer
- Persistence Layer

## Reviewer Agent (Gemini)

Responsibilities:

- Code Review
- Bug Detection
- Edge Case Analysis
- Test Case Design

Human Responsibilities:

- Final Design Decisions
- Code Verification
- Git Management
- Documentation

---

# 8. Prompt Strategy

Prompt Structure:

1. Context
2. Existing Code
3. Objective
4. Constraints
5. Expected Output

Verification Rules:

- Compile Successfully
- Match Coursework Requirements
- Follow OOP Principles
- Pass Manual Tests
- Pass Reviewer Inspection

AI-generated code will not be accepted without manual verification.

---

# 9. Development Timeline

Stage 1

Repository Setup
Requirement Analysis

Stage 2

UML Design
Architecture Review

Stage 3

Model Layer Implementation

Stage 4

Service Layer Implementation

Stage 5

Authentication System

Stage 6

CSV Persistence

Stage 7

Testing and Debugging

Stage 8

Documentation Completion

Stage 9

Final Submission Preparation

---

# 10. Testing Plan

## Test Case Template

Each test case will contain the following fields:

- Test ID
- Function Tested
- Input
- Expected Output
- Actual Output
- Pass/Fail Result
- Bug Found (if any)

Example:

Test ID:
T01

Function Tested:
Player Search by ID

Input:
P001

Expected Output:
Player information is displayed correctly.

Actual Output:
Player information displayed correctly.

Pass/Fail Result:
PASS

Bug Found:
None

---

## Planned Test Cases

Test 01

Player Search by ID

Test 02

Player Search by Name

Test 03

Team Search by ID

Test 04

Team Search by Name

Test 05

Hero Search

Test 06

Equipment Ranking

Test 07

Leaderboard

Test 08

Admin Login

Test 09

Player Login

Test 10

CSV Save and Load

Test 11

Delete Player

Test 12

Invalid Input Handling

---

# 11. Risk Analysis

## Risk 1

AI-generated code contains bugs.

Mitigation:

- Gemini Review
- Manual Testing

## Risk 2

Poor OOP Design

Mitigation:

- UML Review Before Coding

## Risk 3

CSV Data Corruption

Mitigation:

- Exception Handling
- Backup Save

## Risk 4

Incomplete Documentation

Mitigation:

- Update Documentation After Every Stage

---

---

# 12. Final Reflection

## 12.1 AI Tools Used

I used three AI tools throughout this project:

- **ChatGPT (GPT-5.5)** - Architect Agent. Designed the system architecture, UML structure, and project documentation plan. Also helped draft the final reflection and submission checklist.
- **Claude (Opus 4.7)** - Implementation Agent. Generated model classes, service layer code, CSV persistence, CLI menu system, test plans, and UML documentation.
- **Gemini** - Reviewer Agent. Reviewed generated code, identified design issues (e.g. missing team capacity limit), and suggested improvements before I merged changes.

## 12.2 Most Useful Prompt

The most useful prompt was my initial architectural query to ChatGPT. By asking it to focus purely on class boundaries, layer responsibilities, and interfaces *without* generating code, I got a clean Model-Service-Presentation architecture before a single line was written. This prevented the common mistake of receiving a single-file monolithic block that would be impossible to debug. The layered architecture became the foundation for every subsequent implementation step.

## 12.3 Incorrect AI Suggestions

**Team capacity bug**: Claude's initial model layer implementation did not enforce a maximum team size. The Team class had no MAX_MEMBERS limit, allowing unlimited players per team. I discovered this during manual testing when I successfully added a 6th player to Team Alpha (T13 test case). I fixed this myself by adding MAX_MEMBERS = 5 to Team.java and wrapping the assignment logic in Main.addPlayer() with a try/catch to display a friendly error message instead of crashing.

**ChatGPT vs Claude on Leaderboard**: When I asked both models to design a multi-sort leaderboard, ChatGPT used players.sort() for in-place sorting, which would permanently mutate the global player list. Claude used new ArrayList<>(...) for defensive copying. Claude's approach was safer and more production-appropriate, and I adopted it.

## 12.4 Code Verification Process

All AI-generated code went through a three-step verification pipeline:

1. **Compilation**: Every generated class was compiled in IntelliJ IDEA before being accepted.
2. **Manual testing**: I ran 13 structured test cases covering authentication, CRUD, search, ranking, CSV save/load, edge cases, and exit flows. All 13 passed.
3. **Peer AI review**: I used Gemini to independently review generated code for design issues before merging changes. This caught the team capacity vulnerability.

## 12.5 Bugs I Fixed Myself

**Team capacity overflow (T13)**: The most significant bug. The system allowed unlimited players per team. I added MAX_MEMBERS = 5, threw IllegalStateException on overflow, and updated the CLI to catch and display the error gracefully.

**Ghost team assignment (T11)**: Players could be assigned to nonexistent team IDs. I added validation in Main.addPlayer() using findTeamById(), displaying a warning and creating the player as a Free Agent when the team does not exist.

## 12.6 Java Concepts Learned

This project deepened my understanding of:

- **Encapsulation**: Private fields, validated setters, unmodifiable collection views.
- **Inheritance and Abstraction**: Person as abstract base, polymorphic menu routing via isAdmin() / isPlayer().
- **Interfaces**: Persistable contract decoupling persistence from business logic.
- **Collections**: ArrayList, HashMap, Collections.unmodifiableList() for defensive programming.
- **Comparator composition**: Extracting a reusable TIEBREAKER constant for multi-dimensional ranking.
- **Exception handling**: Custom AuthenticationException, IllegalStateException for business rule violations, try/catch in CLI for graceful error display.
- **File I/O**: BufferedReader/BufferedWriter for CSV persistence with proper resource management.

## 12.7 Remaining Uncertainties

I am still unsure about designing larger-scale systems independently. Throughout this project, I relied on AI to propose the layered architecture and service responsibilities. While I understand the final structure, I want to gain more experience making architectural decisions without external scaffolding.

I also recognise that Main.java grew too large (~1250 lines). In a future iteration, I would extract menu handlers into separate classes, but I lacked the time to refactor within the coursework deadline.

## 12.8 Did AI Make the Project Easier or Harder?

**Easier**: AI dramatically accelerated implementation. Writing 7 model classes, 5 services, 800 lines of CSV parsing, and a 1250-line CLI from scratch would have taken weeks. With Claude, I had compilable code within hours. ChatGPT's architecture plan gave me a clear roadmap from day one.

**Harder**: AI-generated code is not production-ready out of the box. The team capacity bug could have gone unnoticed without manual testing. Gemini's review was helpful but not exhaustive. Every generated class required careful reading, and I spent significant time tracing cross-references across 30 source files to verify correctness.

**Overall**: AI saved time on boilerplate but demanded more time on verification. The net result was positive - I could not have built this system in two weeks without AI assistance.

## 12.9 Human vs AI Contribution

**Primarily written by me**:
- Project planning and task decomposition
- Git history management (12 commits, approved tagging convention)
- All documentation (plan.md, design.md, test-cases.md, README.md, uml-draft.md, prompts.md, agent-log.md, reflection.md)
- Manual testing of all 13 test cases
- Bug identification and fixes (team capacity, ghost team assignment)
- Prompt engineering and AI coordination
- Final code review and verification

**Primarily generated by AI**:
- Model layer boilerplate (7 classes + 4 enums)
- Service layer implementation (5 services + 1 interface)
- CSV persistence code (~800 lines)
- CLI menu system (~1250 lines)
- Test plan template (12 test cases)
- UML diagram source (Mermaid)

All AI-generated code was reviewed, tested, and modified by me before being committed to the repository. No code was accepted without verification.
