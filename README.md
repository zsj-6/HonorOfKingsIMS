## 1. Project Overview
Name: Honor of Kings Information Management System

This is a console-based Java application developed for the Object-Oriented Programming coursework. The Honor of Kings IMS is designed to manage core game entities, including Players, Heroes, Equipment, Teams, and Match Records.

## 2. Core Features

1.Role-Based Authentication: Distinct menu systems and permissions for Administrators and standard Players.

2.Entity Management: Full CRUD (Create, Read, Update, Delete) operations for Heroes, Equipment, and Players.

3.Team Management & Validation: Supports team creation, roster viewing, and composition checks.

4.Ranking System: Dynamic ranking based on Player Win Rate, Player Level, and Equipment Usage Score.

5.Data Persistence: Automatic CSV data loading upon system cold-start, and manual/prompted saving before exit to ensure data integrity without relational databases.

## 3. Object-Oriented Programming (OOP) Applied
I designed this system strictly following core OOP principles taught in class:

Encapsulation: All fields in model classes (like `Team` and `Player`) are set to `private`. For example, team members can only be added via `team.addMember()`, allowing the system to check the 5-member limit before saving.

Inheritance & Abstraction: Created an abstract base class `Person`. Both `Admin` and `Player` inherit from `Person` because they share common fields like username and password. This avoids code duplication.

Polymorphism: Service methods use generalized parameters (like `Person`) so the same system logic can handle both admin and player objects flexibly.

## 4. Environment & How to Run

Prerequisites: JDK 8 (Java 8) or higher.

Data Storage: The system automatically initializes and reads from the `data/` directory located in the project root.

Execution in IDE (IntelliJ IDEA): 
    1. Open the project in IntelliJ IDEA.
    2. Locate `Main.java` in the `src` directory.
    3. Click the green 'Run' button. The system will automatically detect or create the `data/` folder and launch the console menu.

## 5. Login Accounts

**Administrator Account:**
Username: `admin`
Password: `admin123`

**Test Player Account:**
Username: `AlphaWolf` (or any username from `players.csv`) Password: `pass001`

## 6. Project Structure

HonorOfKingsIMS/
├── src/
│   ├── model/         
│   ├── service/       
│   ├── exception/     
│   ├── util/          
│   └── Main.java      
├── data/              
└── docs/
    ├── test-cases.md
    ├── uml-draft.md
    └── uml.png
