<div align="center">

# Pokédex JavaFX Application

A desktop Pokédex management application built with **Java, JavaFX, FXML, Gson, and Maven**.

![Java](https://img.shields.io/badge/Java-17-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-Desktop-blue)
![Maven](https://img.shields.io/badge/Maven-Build_Tool-red)
![Gson](https://img.shields.io/badge/Gson-JSON-yellow)

</div>

## Preview

<p align="center">
  <img src="docs/images/start-screen.png" alt="Pokédex Start Screen" width="750">
</p>

## About the Project

This Pokédex application was developed as a **CCPROG3 machine project**. It provides an interactive graphical interface for managing Pokémon, moves, items, and trainer profiles while storing application data through JSON files.

## Features

* Search and view Pokémon information
* Add new Pokémon with stats, types, and evolution details
* Search, view, and add Pokémon moves
* Search, view, and add items
* Create and manage trainer profiles
* Manage trainer Pokémon lineups and storage
* Buy, sell, give, remove, and use items
* Assign and manage held items
* Use Rare Candies and evolution stones
* View trainer information, inventory, lineup, and storage
* Save and load data using JSON

## Tech Stack

| Technology  | Purpose                         |
| ----------- | ------------------------------- |
| **Java 17** | Core application logic          |
| **JavaFX**  | Desktop user interface          |
| **FXML**    | UI layout and screen structure  |
| **CSS**     | Application styling             |
| **Gson**    | JSON data handling              |
| **Maven**   | Dependency and build management |

## Project Structure

```text
.
├── src/
│   └── main/
│       ├── java/
│       │   └── pokedex/
│       │       ├── app/
│       │       ├── controllers/
│       │       ├── managers/
│       │       ├── models/
│       │       └── ui/
│       │
│       └── resources/
│           ├── assets/
│           ├── css/
│           └── fxml/
│
├── items.json
├── moves.json
├── pokemons.json
├── trainers.json
├── pom.xml
└── README.md
```

## Getting Started

### Requirements

Make sure you have the following installed:

* **Java 17 or later**
* **Apache Maven**

Verify your installation:

```bash
java --version
mvn --version
```

### Run the Application

Clone the repository:

```bash
git clone <https://github.com/KirstenPalomo/machine-project.git>
```

Navigate to the project directory:

```bash
cd machine-project
```

Compile the project:

```bash
mvn clean compile
```

Run the application:

```bash
mvn javafx:run
```

## Authors

**Kirsten Palomo**
**Erylle Galinato**

---

<div align="center">

Developed as a CCPROG3 Machine Project.

</div>
