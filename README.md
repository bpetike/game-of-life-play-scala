# Game of Life

This project is the Scala implementation of an interesting problem.

## Appendix

Description of the game: [Wikipedia - Game of Life](http://en.wikipedia.org/wiki/Conway's_Game_of_Life)

The user can issue HTTP requests for each action.

The following endpoints are implemented:
* The user can query the name of the Worlds (/game/worlds)
* The user can load a selected World (/game/world/:worldName)
* The user can issue a command to create the next generation of a World (/game/step)
* The user can reset a world (/game/reset/:worldName)
* The user can query the number of the current generation (/game/stepsnumber)

### Running

You need to download and install sbt for this application to run.
Also, you need to have Java 1.8 installed.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

```bash
sbt run
```

Play will start up on the HTTP port at <http://localhost:9000/>.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request. 

### Run tests

```bash
sbt test
```