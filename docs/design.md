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
