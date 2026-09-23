/*
 * Layer: framework / entry point
 * Role: Main entry point; validates args via ArgsValidator and dispatches to the selected Runner.
 * Depends on: ArgsValidator, RunConfig, Mode, Runner, Errors
 * Used by: Autograder
 */

import java.io.IOException;

    //Entry point of the program: runs the seleted mode based on given arguments
public class Cs214Project {
    //Parses the args into ModeConfig, runs the mode, and prints and error messages
    public static void main(String[] args) {
        try {
            ModeConfig config = ArgumentValidator.validate(args);
            config.mode().getExecutable().run(config);

        } catch (IllegalArgumentException | IOException error) {
            System.err.println("Error: " + error.getMessage());
        }
    }
}