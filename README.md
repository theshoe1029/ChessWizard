# Running Chess Wizard UCI

The `run_chess_wizard` script in the root project directory will start Chess Wizard in UCI mode. If you are using a chess GUI software like Scid, select this script as the run command for the Chess Wizard engine.

# Running Chess Wizard Web

The `run_chess_wizard_web` script in the root project directory will start Chess Wizard in web mode. Once the web application is started, you can connect to it at `localhost:8080`.

# Running Chess Wizard JAR

If you wish to start the Chess Wizard UCI using the `ChessWizard.jar` file in the `out` directory, simply use the `java -jar out/artifacts/ChessWizard/ChessWizard.jar` command in the terminal.

If you wish to start the Chess Wizard web application, use the `java -jar out/artifacts/ChessWizard/ChessWizard.jar server` command. Adding `server` to the jar command line arguments lets Chess Wizard know to run the web application.