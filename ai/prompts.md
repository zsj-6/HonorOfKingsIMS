# Prompt Record

## Prompt 01 – Architecture Planning

- **Time:** 2026-06-02 16:54 (UTC+8)
- **Tool / Model:** ChatGPT / GPT-5.5
- **Agent Role:** Architect Agent
- **Related Commit:** `4b98058` ([AI-Architect] complete project architecture and UML planning)

### My Prompt

I am designing a Java OOP coursework project – an Honor of Kings
information management system. I need a class structure design first.

Required: at least 7 classes – Person, Player, Admin, Hero, Equipment,
Team, MatchRecord. Must use inheritance, interfaces, and collections.
Features needed: player lookup, team overview, hero details, equipment
statistics, match history, leaderboard, admin permissions, login system.

Do not give me full code. Focus on class responsibilities, relationships,
and which layer each class belongs to. Suggest a Model-Service-Presentation
layered architecture. What interfaces would be useful? Which classes should
be abstract?

### AI Response Summary

ChatGPT suggested a three-layer architecture:

- Model layer: Person (abstract), Player, Admin, Hero, Equipment, Team,
  MatchRecord. Enums for Role, HeroType, EquipmentType, MatchResult.
- Service layer: GameDataManager, AuthenticationService, SearchService,
  RankingService.
- Presentation layer: Main (menu system), InputHelper, DataInitializer.

Suggested interfaces: Searchable (for search operations) and Persistable
(for save/load). Recommended String-based cross-references using IDs
rather than direct object references to keep layers loosely coupled.

Also suggested a UML class diagram and a development timeline split into
stages: model -> services -> auth -> persistence -> testing -> docs.

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

This was the most important prompt in the project. I accepted the overall
three-layer architecture and class structure. Key decisions I made:

- Accepted Person as abstract with Player and Admin as subclasses – clean
  inheritance that avoids code duplication for shared user fields.
- Accepted String ID cross-references which kept the model layer simple
  and made CSV serialisation straightforward later.
- Kept both Searchable and Persistable in the plan, though only Persistable
  was eventually implemented as a formal interface. SearchService methods
  were implemented directly without an interface in the final code.
- The development stage breakdown (model -> service -> persistence ->
  menu -> testing) became the actual workflow I followed.

---

## Prompt 02 – Model Layer Implementation

- **Time:** 2026-06-02 19:51 (UTC+8)
- **Tool / Model:** Claude Opus 4.7
- **Agent Role:** Implementation Agent
- **Related Commit:** `bc7c8f5` ([AI-Implementation] implement complete model layer and core structures)

### My Prompt

I have the architecture planned out. Now I need to implement the model
layer for my Honor of Kings IMS. The required classes are:

- Person (abstract superclass)
- Player (extends Person)
- Admin (extends Person)
- Hero
- Equipment
- Team
- MatchRecord

Cross-references use String IDs. Use enums for Role, HeroType,
EquipmentType, MatchResult. Follow Java OOP best practices: private fields,
getters/setters with validation, meaningful toString(), equals() and
hashCode() based on ID. No service layer or menu code yet. Just the
model classes with JavaDoc comments.

### AI Response Summary

Claude generated all seven model classes plus four enums:

- Person: abstract base class with id, username, password, role. Includes
  validation in all setters (non-null, non-blank, password min length).
- Player: adds level, win/loss records, ownedHeroIds list, matchRecordIds
  list, teamId. Win rate computed dynamically. Level capped at 1-30.
- Admin: minimal subclass, inherits everything from Person.
- Hero: id, name, HeroType, base stats (HP/ATK/DEF), compatibleEquipmentIds.
- Equipment: id, name, EquipmentType, stat bonuses, usageCount,
  averageRating, heroUsageCount. getEquipmentScore() formula included.
- Team: id, name, memberIds list, match stats. MAX_MEMBERS not yet added.
- MatchRecord: id, date, teamId, opponent, result, playerIds, heroIds.

All collections return unmodifiable views. String ID fields validated for
non-null/non-blank. Numeric fields validated for non-negative where
appropriate.

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

Accepted the overall class structure and most implementation details.
Changes I made:

- Added Team.MAX_MEMBERS = 5 later during testing when I discovered the
  capacity was not enforced (see Prompt 08).
- Verified the Equipment score formula matched the coursework spec:
  usageCount * 0.5 + averageRating * 0.3 + heroUsageCount * 0.2.
- Confirmed that equals()/hashCode() use only the ID field, which is the
  correct approach for entities stored in collections.

---

## Prompt 03 – Service Layer Implementation

- **Time:** 2026-06-02 22:29 (UTC+8)
- **Tool / Model:** Claude Opus 4.7
- **Agent Role:** Implementation Agent
- **Related Commit:** e016d69 ([AI-Implementation] implement core services: GameData, Auth, Search, Ranking)

### My Prompt

The model layer is done (Person, Player, Admin, Hero, Equipment, Team,
MatchRecord, and enums). Now I need the service layer:

- GameDataManager: CRUD for all entities, prevent duplicate IDs.
- AuthenticationService: login/logout, check Admin vs Player roles.
- SearchService: search by ID and name, return Optional.
- RankingService: leaderboards with tiebreaker, equipment ranking.

No console printing in services. Please write the Java code with JavaDoc.

### AI Response Summary

Claude generated a complete 12-case test plan with:

- Detailed step-by-step inputs referencing actual system IDs and usernames
  (admin/admin123, AlphaWolf/pass001, P003, T001, H011 Zhang Fei, etc.).
- Expected outputs matching the display format of the real application
  (exact table headers, separator lines, and field labels).
- Coverage across all functional areas: authentication, CRUD, search,
  ranking, CSV save/load, edge cases (invalid credentials, out-of-range
  menu choices, nonexistent team IDs), and normal exit flows.
- A summary table with 12 rows and Pass/Fail columns ready for filling.
- A Known Issues section for recording bugs found during testing.

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

Accepted the overall architecture and class structure as generated. Key
modifications I made during review:

- The TIEBREAKER constant in RankingService was well-designed – I kept it
  exactly as generated because it used defensive copying and explicit
  generic type declarations, avoiding the type-inference pitfall that can
  occur when chaining .reversed() without explicit types.

- I compared Claude's approach with a ChatGPT suggestion for the same
  leaderboard problem. ChatGPT used in-place sorting without defensive
  copying, which would have permanently altered the global player list.
  Claude's approach was clearly safer and more production-appropriate.

- The referential cleanup logic in GameDataManager (removing hero references
  from all players when a hero is deleted) was thorough and I kept it.

---

## Prompt 04 – CSV Persistence Layer

- **Time:** 2026-06-03 19:56 (UTC+8)
- **Tool / Model:** Claude Opus 4.7
- **Agent Role:** Implementation Agent
- **Related Commit:** c1bab5b ([AI-Implementation] implement robust CSV persistence with resource safety)

### My Prompt

The model and service layers are done. Now I need CSV persistence so data
can be saved and loaded. I need:

- A Persistable interface with save() and load().
- A FileStorageService that reads/writes five CSV files under data/:
  players.csv, heroes.csv, equipment.csv, teams.csv, matches.csv.

Use standard Java I/O only. Each file needs a header row. Handle missing
or malformed files gracefully. Keep cross-references consistent after
loading. Add saveAllData() and loadAllData() helper methods.

### AI Response Summary

Claude generated two files totalling approximately 800 lines:

- Persistable interface: a clean two-method contract (save/load) with
  IOException declared in the signature.

- FileStorageService: full CSV read/write for all five entity types.
  Key design details included:
  - Semicolon (;) as the intra-cell separator for list fields (e.g.
    memberIds) to avoid conflict with the CSV comma delimiter.
  - Silent skip for missing files on load (no crash on first run).
  - Strict field-count validation on each CSV row with line-number
    reporting for debugging.
  - Load order respecting dependencies: Equipment -> Heroes -> Teams ->
    Players -> MatchRecords.
  - Clear-all-before-load strategy to prevent duplicate data.
  - Helper methods for list serialisation (formatList, parseList) and
    null/empty conversion.

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

Accepted the overall design and most implementation details. Changes I made:

- The semicolon separator for list fields was a practical choice I kept
  because it avoids CSV comma escaping complexity while keeping files
  human-readable.

- I verified the load order logic carefully – loading Teams before Players
  is necessary because Players reference team IDs. The dependency-aware
  ordering was correct.

- I added the escape() method as a pass-through placeholder to make future
  CSV escaping (e.g. for names containing commas) easier to add without
  rewriting the entire persistence layer.

- Tested with real data: saved all entities, deleted the data/ folder,
  restarted, and confirmed all 15 players, 20 heroes, 25 equipment, 3 teams,
  and 20 match records were restored with correct cross-references.

---

## Prompt 05 – CLI Menu System & Utilities

- **Time:** 2026-06-03 20:04 (UTC+8)
- **Tool / Model:** Claude
- **Agent Role:** Implementation Agent
- **Related Commit:** 21a2704 ([AI-Implementation] implement CLI entry point, menu system and utilities)

### My Prompt

The backend is done (models, services, auth, CSV persistence). Now I need
the console menu and Main entry point:

- Main.java: startup flow (init data, try CSV load, show login menu),
  role-based menus for Admin and Player, all sub-menu options. Call
  existing services only, no duplicated logic.

- InputHelper.java: safe integer/string input, range validation, reject
  commas and semicolons, yes/no confirmations.

Use loops not recursion, handle all invalid input without crashing.

### AI Response Summary

Claude generated Main.java (~1250 lines) and InputHelper.java (~140 lines):

- Main.java: a console-driven menu system with role-based routing. Admin
  and Player menus are separate method groups. All business logic is
  delegated to existing services (SearchService, RankingService,
  FileStorageService, AuthenticationService). Display helpers format
  entity data as labelled key-value text blocks. The Team display includes
  a composition check that warns if a team lacks Tank or Support coverage.

- InputHelper.java: a static utility class wrapping a single Scanner
  instance. Methods include readInt (with optional range), readNonEmptyString,
  readLineSafe (rejects commas/semicolons), readPassword, and readConfirmation.
  All numeric parsing is done via Integer.parseInt() with try/catch to
  avoid InputMismatchException.

The overall structure follows the layered architecture: Main (presentation)
calls services (business logic) which call GameDataManager (data).

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

Accepted the overall menu structure and InputHelper. Key changes I made:

- I reviewed the team composition check in the displayTeam helper and
  confirmed it correctly iterates through all members' owned heroes to
  detect missing Tank or Support roles.

- The menu navigation uses while-loop-based return instead of recursion,
  which prevents stack overflow on long sessions – this was correctly
  implemented from the start.

- I verified all service calls are indirect (through SearchService,
  RankingService, etc.) rather than directly manipulating GameDataManager
  collections from Main, maintaining proper separation of concerns.

- Later, I discovered that Main.java grew quite large (~1250 lines). This
  is a known limitation I plan to address by extracting menu classes in a
  future refactoring pass.

---

## Prompt 06 – Test Plan Generation

- **Time:** 2026-06-03 21:12 (UTC+8)
- **Tool / Model:** Claude Opus 4.7
- **Agent Role:** Testing / Reviewer Agent
- **Related Commit:** 6aa29e3 ([AI-Review] generate comprehensive CLI test plan template for system validation)

### My Prompt

My Honor of Kings IMS project is almost done. It has authentication, player
management, hero management, team management, search, ranking, and CSV
save/load. I need a testing document at docs/testing.md with 12 test cases
covering every feature. Use real IDs from the default dataset (P001-P015,
T001-T003, H001-H020, E001-E025). Each test case needs: Test ID, Function
Tested, step-by-step Input, Expected Output. Leave Actual Output, Pass/Fail,
and Bug Found blank for manual testing. Add a summary table at the end.

### AI Response Summary

Claude generated a complete 12-case test plan with:

- Detailed step-by-step inputs referencing actual system IDs and usernames
  (admin/admin123, AlphaWolf/pass001, P003, T001, H011 Zhang Fei, etc.).
- Expected outputs matching the display format of the real application
  (exact table headers, separator lines, and field labels).
- Coverage across all functional areas: authentication, CRUD, search,
  ranking, CSV save/load, edge cases (invalid credentials, out-of-range
  menu choices, nonexistent team IDs), and normal exit flows.
- A summary table with 12 rows and Pass/Fail columns ready for filling.
- A Known Issues section for recording bugs found during testing.

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

Accepted the overall structure and most test cases. Changes I made:

- I added a 13th test case (T13 Team Roster Capacity Limit Enforcement)
  after discovering during manual testing that the system initially allowed
  unlimited team members. This test validates the MAX_MEMBERS = 5 cap that
  I later added in Team.java.

- I adjusted some expected outputs to match the actual display format –
  the AI assumed certain labels that differed slightly from what Main.java
  actually prints.

- I filled in all Actual Output and Pass/Fail columns after running each
  test manually. All 13 tests eventually passed after fixing the team
  capacity bug.

- The test plan was renamed to test-cases.md in the final submission to
  match the coursework naming convention.

---

## Prompt 07 – UML Documentation Finalisation

- **Time:** 2026-06-08 15:08 (UTC+8)
- **Tool / Model:** Claude
- **Agent Role:** Implementation Agent
- **Related Commit:** `c83d3f3` ([AI-Implementation] finalize UML documentation)

### My Prompt

The project is nearly complete. I need to finalize the UML documentation
in docs/uml-draft.md to reflect the actual codebase. It should cover:

- All layers: presentation (Main, InputHelper, DataInitializer), service
  (AuthenticationService, SearchService, RankingService, GameDataManager,
  FileStorageService), model (Person, Player, Admin, Hero, Equipment, Team,
  MatchRecord), interfaces (Persistable), and enums.
- Inheritance, interface implementations, associations, cardinalities
  (e.g. Team 1..5 Players), and service dependencies.
- A Mermaid class diagram and explanations of OOP principles used.

Do not invent classes that do not exist in the repo. Verify everything
against the actual source code.

### AI Response Summary

Claude analyzed the codebase and updated uml-draft.md with:

- A layered architecture overview mapping each class to its layer.
- A Mermaid class diagram showing inheritance (Person -> Player/Admin),
  interface implementation (FileStorageService implements Persistable),
  associations (Team contains Players, Player owns Heroes, Hero uses
  Equipment, MatchRecord references Players and Heroes), and dependency
  arrows from Main to all services.
- Cardinality annotations: Team 1--5 Player, Player 0..* Hero,
  Hero 0..* Equipment.
- An OOP principles section explaining where inheritance, encapsulation,
  polymorphism, and interfaces are demonstrated in the code.
- A review section confirming the UML matches the actual implementation
  and noting that a visual UML image (uml.png) is still needed.

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

Accepted the UML draft content. Changes I made:

- The AI included Searchable as an interface, but SearchService does not
  actually implement it in the codebase. I noted this discrepancy.
- The Mermaid diagram was useful but I still need to generate a proper
  uml.png image file for the final submission as required by the coursework.
- I verified each relationship against GameDataManager to ensure the
  cardinalities were correct (e.g. Team.addMember enforces the 5-player cap).

---

## Prompt 08 – Team Member Capacity Fix

- **Time:** 2026-06-04 10:34 (UTC+8)
- **Tool / Model:** Claude
- **Agent Role:** Implementation Agent
- **Related Commit:** `016ad85` ([Fix] Prevent team member overloading)
- **Related Commit:** `89baa0b` ([Test] reveal vulnerability: team member overflow)

### My Prompt

In my Honor of Kings IMS project, teams should have a maximum of 5 players.
Right now, there is no limit -- I can add a 6th player to a team without
any error. How do I fix this? Which files need to be changed, and what
validation logic should I add?

### AI Response Summary

Claude identified that the validation should be added in two places:

1. Team.addMember() -- add a MAX_MEMBERS = 5 constant and throw
   IllegalStateException if the team is full before adding.

2. Main.addPlayer() -- wrap the team assignment in a try/catch to display
   the error message to the user and cancel the player registration instead
   of crashing the application.

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

I implemented the fix myself based on Claude's diagnosis. Steps I took:

- Added private static final int MAX_MEMBERS = 5 to Team.java.
- Added a size check in addMember() that throws IllegalStateException with
  the exact message specified.
- Updated Main.addPlayer() to catch IllegalStateException and display
  "Operation canceled. Player registration rejected."

Before this fix, I first wrote a test case (T13 in test-cases.md) to confirm
the vulnerability existed -- I successfully added a 6th player to Team Alpha.
After the fix, the same test correctly blocks the operation.

This was the most important bug I fixed manually in the project.

---

## Prompt 09 – Ghost Team Validation Fix

- **Time:** 2026-06-05 09:22 (UTC+8)
- **Tool / Model:** Claude
- **Agent Role:** Implementation Agent
- **Related Commit:** `1b439ca` ([Fix] validate team existence during player creation to prevent ghost team assignment)

### My Prompt

In my Honor of Kings IMS project, I have a bug where a player can be
created with a team ID that does not exist in the system. For example,
I can register a player with team ID 'T999' even though no such team
exists. The player ends up with a broken reference. What is the best
way to fix this? I want the system to warn that the team does not
exist but still allow the player to be created as a Free Agent. How
would you implement this?

### AI Response Summary

Claude suggested a two-part fix:

1. In Main.addPlayer(), before creating the Player object, check if
   the team exists using GameDataManager.findTeamById(). If the team
   is not found, display a warning message and set the teamId to null
   so the player becomes a Free Agent instead of storing a broken ref.

2. In GameDataManager, ensure that removeTeam() sets all member players'
   teamId to null to prevent dangling references when a team is deleted.

Claude also suggested that the warning message should clearly tell
the user what happened: "Team ID 'T999' does not exist! Player will
be created as a Free Agent (No Team)."

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

I implemented the fix myself based on Claude guidance. Steps I took:

- In Main.addPlayer(), I added a check using findTeamById() before
  creating the player. If the team ID is provided but not found, I
  display the warning and set teamId to empty so the player becomes
  a Free Agent. This followed Claude suggestion exactly.

- I also kept teamId as null in the Player constructor when no
  valid team is assigned, making the distinction between 'no team'
  and 'invalid team' clear in the codebase.

- I verified that removeTeam() already cleaned up player references
  properly — this was part of the original GameDataManager design.

I tested this by creating P099 with T999 (nonexistent) and P100
with T001 (existing). P099 correctly showed 'Team: -' as a Free
Agent, while P100 correctly showed 'Team: Team Alpha'.

---

## Prompt 10 – Leaderboard Comparator Design (Cross-Model Comparison)

- **Time:** 2026-06-05 14:43 (UTC+8)
- **Tool / Model:** ChatGPT & Claude (comparison study)
- **Agent Role:** Architect / Implementation Agent
- **Related Commit:** `e016d69` ([AI-Implementation] implement core services)

### My Prompt

In my Honor of Kings Java information management system, I need to
implement a Leaderboard feature. I have a List<Player>. We need to
first sort players in descending order by rank; if ranks are the
same, then sort them in descending order by win rate. How can this
multi-sorting functionality be implemented in Java? Please provide
the core code snippet and explain your design approach.

### AI Response Summary

I asked the same question to both ChatGPT and Claude to compare
their approaches.

ChatGPT provided two approaches: Java 8 Comparator chaining with
.reversed() and traditional lambda notation with manual if-else
comparisons. It assumed Rank was a simple int primitive and used
players.sort() for in-place sorting.

Claude provided a more enterprise-oriented analysis:
- Suggested upgrading Rank to an enum with explicit ordering.
- Pointed out generic type inference pitfalls when chaining .reversed()
  and recommended explicit type declarations in Comparator generics.
- Emphasised using new ArrayList<>(...) to defensively copy before
  sorting, so the original global player list is never mutated.

### My Decision

- [x] Accepted (Claude approach) with modifications
- [ ] Rejected

I chose Claude approach. Key reasons:

- ChatGPT used in-place sorting (players.sort()) which would permanently
  alter the global player list in GameDataManager. In an IMS system, this
  could cause bugs where other parts of the system see a mutated order.

- Claude approach used new ArrayList<>(...) to create a defensive copy,
  leaving the original data intact. This is the correct behaviour.

- I further improved the design by extracting the complex tiebreaker logic
  into a constant `TIEBREAKER` in RankingService. This follows the
  Composition pattern and the DRY principle – the same tiebreaker logic
  is reused across all three ranking methods (win rate, level, matches)
  rather than duplicated.

---

## Prompt 11 – Test Execution & Documentation Finalisation

- **Time:** 2026-06-09 10:15 (UTC+8)
- **Tool / Model:** Claude
- **Agent Role:** Reviewer Agent
- **Related Commit:** `c83d3f3` ([AI-Implementation] finalize UML documentation)

### My Prompt

My project code is complete and the test plan template is written. I
have now manually executed all 13 test cases and filled in the Actual
Output columns. I need to finalise the test documentation. Can you
help me review the test results and make sure the documentation is
consistent? Also, I renamed the file from testing.md to test-cases.md
to match the coursework naming convention. Please suggest any
improvements to the test case descriptions, expected outputs, or the
overall structure before final submission.

### AI Response Summary

Claude reviewed the completed test-cases.md and suggested:

- The test document is comprehensive and covers all functional areas.
- Verified that each Actual Output matches the corresponding Expected
  Output in structure and content.
- Noted that the Pass/Fail column and Bug Found columns were filled
  correctly after my manual execution.
- Suggested adding the T13 test case explicitly in the summary table
  since it was my addition to the original AI-generated plan.
- Recommended highlighting the Known Issues resolution for T13 as a
  learning point – showing that manual testing caught a real bug.

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

I accepted the AI review suggestions. Changes I made:

- Updated the summary table to include all 13 test cases instead of 12.
- Added a note in the Known Issues section clarifying that T13 was
  originally a failing test that passed after I fixed the team capacity
  bug. This demonstrates the value of human testing in AI-generated code.
- Renamed the file from testing.md to test-cases.md as required and
  updated the prerequisite description to match the default dataset.

---

## Prompt 12 – Final Reflection & Submission Preparation

- **Time:** 2026-06-13 20:40 (UTC+8)
- **Tool / Model:** ChatGPT (GPT-5.5)
- **Agent Role:** Architect Agent
- **Related Commit:** (final documentation stage)

### My Prompt

I have completed my Honor of Kings IMS coursework project. Now I need
to prepare the final submission materials. I need help writing a
reflection document covering:

1. Which AI tools I used and how
2. Which prompt was the most useful
3. Any AI suggestions that were wrong
4. How I verified AI-generated code
5. What bugs I fixed myself
6. What Java concepts I learned
7. What I am still unsure about
8. Whether AI made the project easier or harder
9. Which parts were mainly written by me
10. Which parts were mainly generated by AI

Please help me draft the reflection in English. Also, what other
documents do I need to check before the final submission?

### AI Response Summary

ChatGPT helped me draft the reflection by asking follow-up questions
about each point. It didn't write the reflection for me – instead, it
guided me through the process of self-reflection. For example, it asked
me to think about: which prompt I kept coming back to, which bug took
the most debugging effort, and where I felt AI truly added value.

ChatGPT also provided a submission checklist:
- All AI-generated code must be manually verified and tested.
- Git commits must use the approved tags: [Human], [AI-Architect],
  [AI-Implementation], [AI-Review], [Fix], [Test], [Docs].
- The final submission must include: src/, docs/, ai/, plan.md,
  README.md, and git-history.txt.
- The submission format requires a zipped project folder with all
  source code and documentation.

### My Decision

- [x] Accepted with modifications
- [ ] Rejected

I used ChatGPT scaffolding to write the reflection myself. Decisions:

- I wrote every answer in my own words. ChatGPT helped with structure
  but the reflections are genuinely mine.

- For the submission checklist, I followed ChatGPT guidance to verify:
  All 12 git commits use the approved tags
  All source code compiles and runs with javac/java
  All documentation files exist (plan.md, design.md, test-cases.md,
    uml.png, prompts.md, reflection.md, agent-log.md, README.md)
  git-history.txt is generated

- I also wrote an Advanced AI Reflection comparing ChatGPT and Claude
  on the same leaderboard design question, which highlights the
  importance of understanding AI limitations.
