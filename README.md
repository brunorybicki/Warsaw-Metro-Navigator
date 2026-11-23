# Warsaw Metro Navigator

A command-line route planner for the Warsaw Metro, written in Java. Choose a starting station and a destination on lines M1 and M2, and the program prints the route with the travel direction, the transfer, and the number of stops.

```text
YOUR ROUTE:
Centrum
↓ M1 towards Młociny
Świętokrzyska

TRANSFER: M1 → M2

Świętokrzyska
↓ M2 towards Bródno
Nowy Świat-Uniwersytet
Centrum Nauki Kopernik
Stadion Narodowy
Dworzec Wileński
Szwedzka
Targówek Mieszkaniowy
Trocka

Stops: 8
Transfers: 1

Have a nice journey!
```

## Features

- Lines M1 (21 stations) and M2 (18 stations) with the transfer at Świętokrzyska
- Stations and lines are chosen from numbered menus
- Every route shows the travel direction (`towards …`), transfers, and totals
- Special cases: the same station on both lines (a transfer only) and identical start and destination
- Invalid input (text, symbols, empty lines, numbers out of range) is rejected with a message and asked again; the program never crashes on input

## Requirements

- Java 21 or newer
- Maven

## Build and run

Build the program (this also runs all tests):

```bash
mvn package
```

Run it:

```bash
java -jar target/warsaw-metro-navigator.jar
```

Answer each question with a number and press Enter. Press Ctrl+D (Ctrl+Z and Enter on Windows) to quit early.

To run only the tests:

```bash
mvn test
```

## Project structure

```text
src/main/java/
├── Main.java                  builds the objects and starts the application
├── app/                       MetroNavigatorApp: runs the dialogue from welcome to route
├── model/                     Station, MetroLine, StationNode, Connection, Route
├── network/                   MetroNetwork: a generic network of lines and connections
│   └── data/                  WarsawMetroData: the only place that knows Warsaw stations
├── routing/                   RouteFinder and RouteCost
└── ui/                        ConsoleUI, InputReader, RouteFormatter
```

The network and routing code knows nothing about Warsaw. All Warsaw stations, the line order, and the transfer are defined in `WarsawMetroData`, so the rest of the program could work with another city's data.

## How it works

**The network as a graph.** Every station on a line is a node, so Świętokrzyska on M1 and Świętokrzyska on M2 are two different nodes. Neighboring stations on a line are connected by rides, and the two Świętokrzyska nodes are connected by a transfer. A ride counts as one stop; a transfer counts as one transfer and no stops.

**Route search.** `RouteFinder` uses depth-first search with backtracking: it follows connections from the start, steps back from dead ends, and never returns to a station it has already visited. It runs in O(V + E) time for V nodes and E connections.

**Travel direction.** The direction is derived from the order of the stations on a line: a ride towards a later station heads towards the last station of the line, a ride towards an earlier one heads towards the first. No line or station is special-cased.

**Testable input and output.** `InputReader` and `ConsoleUI` receive their input (`Scanner`) and output (`PrintStream`) through the constructor. The program passes the keyboard and the screen; the tests pass prepared answers and capture the output in memory.

## Tests

Over 100 JUnit 5 tests cover the domain model, the network, the Warsaw data, route finding on a small synthetic network and on the real network, the route text, input validation, and complete runs of the application whose output is compared character by character with the expected dialogue.

## Design decisions and limitations

- **Depth-first search instead of a shortest-path algorithm.** With two lines and one transfer station, there is exactly one way between any two stations without going back, so the first route found is also the best one. A network with alternative ways (for example, after adding line M3) would need a search that compares route costs, such as Dijkstra's algorithm.
- **Warsaw only.** The generic network and the city data are separated, but choosing a city is not part of this version.

## Development

Developed with [Claude Code](https://claude.com/claude-code) as an AI pair programmer. I wrote the specification, made the design decisions, and reviewed every change.
