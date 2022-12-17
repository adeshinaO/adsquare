
## Overview

An API for tic-tac-toe games. Every position on the 3x3 grid is assigned a number between 1 and 9 starting from left 
to right. No identity management for players is implemented. To create game pick any integer IDs to represent two 
players. The API exposes two endpoints:

- `POST /api/v1/game` - This creates a new game.

Sample Request Body:

```json
{
  "playerOneId":2,
  "playerTwoId":1
}
```

Sample Response Body:

```json
{
  "status":"IN_PROGRESS",
  "game_id":1,
  "cross_player_id":2,
  "circle_player_id":1,
  "occupied_positions":[]
}
```

- `POST /api/v1/game/{game_id}/mark` - Marks a position on the grid for a player.


```json
{
  "playerId":1,
  "position":6
}
```

Sample Response Body:

```json
{
  "status":"CIRCLE_PLAYER_VICTORY",
  "game_id":1,
  "cross_player_id":2,
  "circle_player_id":1,
  "occupied_positions":[
    {"position":1,"position_marker":"CROSS"},
    {"position":3,"position_marker":"CROSS"},
    {"position":4,"position_marker":"CIRCLE"},
    {"position":5,"position_marker":"CIRCLE"},
    {"position":6,"position_marker":"CIRCLE"}
  ]
}
```

In the event of logic errors both endpoints return a representation that includes a machine-readable error code and 
a human-readable description. An example is shown below:

```json
{
  "code":"ALREADY_MARKED_POSITION",
  "description":"This position is already marked"
}
```

## Tech Stack

- Java 11
- Spring Boot
- H2 In-memory Relational Database.

_NOTE: Game state is stored in a RDBMS to because their ACID compliance + Spring's implicit pessimistic locking 
during transactions ensures atomic read/write operations_

### How to run this locally

- Use the Gradle wrapper. At the project root, enter command `./gradlew bootRun`. This would start up the service and 
bind it to port 8080. This also starts up the H2 GUI which can be accessed at `http://localhost:8080/h2-console`.

- The database connection properties are defined in `src/main/resources/application.properties`. 

### Tests

Integration tests are included for six(6) basic game scenarios. To run all tests use the following command at the 
root of the project: `./gradlew test`
