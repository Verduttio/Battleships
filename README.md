# Battleships
Multiplayer game using LAN

Application connects with other application and plays battleship.

### Run parameters
* `-mode [server|client]` - working mode (as server: establish connection, as client: connects with a server)
* `-port N` - port communication.
* `-address X` - server address (only in client mode)
* `-map map-file` - path to the file containing the map with the location of ships (the format is described in the Map section).

### Map
* See maps directory.

### Communication protocol
* TCP protocol with UTF-8 coding.
* Client and server send each other _message_, which consists of two parts: _command_ and _coordinates_, seperated by `;`, and ended with `end of line` char `\n`.
  * Message format: `command;coordinates\n`
  * Message example: `missed;D6\n`
* Commands explanation:
  * _start_
    * command inititating the game. 
    * Sended by client, only at the beginning.
    * Example: `start;A1\n`
  * _missed_
    * response sending when there is no ship at coordinates received from an opponent.
    * Example: `missed;A1\n`
  * _hit_
    * response sending when there is a ship at coordinates received from an opponent but it is not the last part of the ship.
    * Example: `hit;A1\n`
  * _downed_
    * response sending when there is a ship at coordinates received from an opponent and it is the last part of the ship.
    * Example: `downed;A1\n`
  * _last downed_
    * response sending when there is a ship at coordinates received from an opponent and it is the last part of the last active ship on the map.
    * This is the last command in the game.
    * Example: `last downed\n`

### Application 
* After start-up application displays user's map.
* During the game application displays all sent and received commands.
* After the end of the game application displays:
  * `You won!!!\n` in case of win or `You lost!!!\n` in case of defeat,

Example of an opponent map from lost session:
```
..#..??.?.
#.????.#..
#....??...
..##....?.
?.....##..
??#??.....
..?......#
..##...#..
.##....#.#
.......#..
```

Example of user's map from won session:
```
~~@~~.~~~.
@..~.~.@.~
#.~#..~.~.
..##..~..~
..~.~.@@..
.#@~..~...
.~.~.~.~.@
~.##.~.#~~
.##~..~~~~
..~.~.~~~.
```
