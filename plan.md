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

# 12. Final Reflection Placeholder

This section will be completed after implementation.

Topics:

- AI tools used
- Best prompt
- Incorrect AI suggestions
- Debugging experience
- Lessons learned
- Remaining uncertainties
- Human contribution analysis