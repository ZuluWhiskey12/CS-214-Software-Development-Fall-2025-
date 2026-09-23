import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
/*
 * Layer: framework / input
 * Role: Validate input arguments, check file paths, and build a RunConfig (input, output, mode).
 * Depends on: Mode, RunConfig, Errors
 * Used by: Cs214Project
 */
import java.util.List;

public final class ArgumentValidator {
    private ArgumentValidator() {
    }

    // Main method: parses given args into ModeConfig and validates paths
    public static ModeConfig validate(String[] args) throws IOException {
        if (args.length < 2) {
            throw Errors.incorrectArgCount();
        }

        Path inputPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);

        ParsedArgs parsed = parseModeAndSelections(args);

        checkCsvExtension(inputPath, outputPath);
        checkInputPath(inputPath);
        checkOutputPath(outputPath);

        return new ModeConfig(inputPath, outputPath, parsed.mode, parsed.songSelections, parsed.k);
    }

    // Decide which mode we're in and collect selections / K if needed
    private static ParsedArgs parseModeAndSelections(String[] args) {
        if (args.length == 2) {
            // Just input + output -> PA1
            return new ParsedArgs(Mode.PA1, List.of(), 0);
        }

        if (args.length < 3) {
            throw Errors.incorrectArgCount();
        }

        String flag = args[2];

        return switch (flag) {
            case "-r" -> parsePA5(args);
            case "-s" -> parsePA6(args);
            default -> parsePA2ThroughPA4(args, flag);
        };
    }

    private static ParsedArgs parsePA5(String[] args) {
        // PA5: recommendations around selected songs
        List<String> selections = parseSelections(args, 3);
        return new ParsedArgs(Mode.PA5, selections, 0);
    }

    private static ParsedArgs parsePA6(String[] args) {
        // PA6: playlist generation
        if (args.length < 5) {
            throw Errors.incorrectArgCount();
        }

        int k = parseK(args[3]);
        List<String> selections = parseSelections(args, 4);

        if (selections.size() < k) {
            throw Errors.incorrectArgCount();
        }

        return new ParsedArgs(Mode.PA6, selections, k);
    }

    private static ParsedArgs parsePA2ThroughPA4(String[] args, String flag) {
        // PA2–PA4: single flag only
        if (args.length != 3) {
            throw Errors.incorrectArgCount();
        }
        Mode mode = parseModeFlag(flag);
        return new ParsedArgs(mode, List.of(), 0);
    }

    // Maps flags for PA2 through PA4
    private static Mode parseModeFlag(String flag) {
        return switch (flag) {
            case "-a" -> Mode.PA2;
            case "-u" -> Mode.PA3;
            case "-p" -> Mode.PA4;
            default -> throw Errors.unsupportedArgs(flag);
        };
    }

    private static int parseK(String value) {
        final int k;
        try {
            k = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw Errors.incorrectArgCount();
        }

        if (k < 1) {
            throw Errors.incorrectArgCount();
        }
        return k;
    }

    // Collects and validates song selctions for PA5
    private static List<String> parseSelections(String[] args, int startIndex) {
        List<String> songSelections = new ArrayList<>();

        if (args.length <= startIndex) {
            throw Errors.noSongs();
        }

        for (int i = startIndex; i < args.length; i++) {
            String song = args[i];

            if (songSelections.contains(song)) {
                throw Errors.duplicateUserSelection();
            }
            songSelections.add(song);
        }
        return List.copyOf(songSelections);
    }

    // Ensures input and output are CSV files
    private static void checkCsvExtension(Path inputPath, Path outputPath) {
        String in = inputPath.toString();
        String out = outputPath.toString();

        if (!in.endsWith(".csv") || !out.endsWith(".csv")) {
            throw Errors.badCsvExtension();
        }
    }

    // Checks if input file exists and is not empty
    private static void checkInputPath(Path inputPath) throws IOException {
        if (!Files.exists(inputPath)) {
            throw Errors.inputNotFound(inputPath);
        }
        if (Files.size(inputPath) == 0) {
            throw Errors.inputEmpty(inputPath);
        }
    }

    // Checks if output file exists and can be written to.
    private static void checkOutputPath(Path outputPath) throws IOException {
        Path parent = outputPath.getParent();

        if (parent != null && (!Files.isDirectory(parent) || !Files.isWritable(parent))) {
            throw Errors.outputNotWritable(parent);
        }
    }

    private static record ParsedArgs(Mode mode, List<String> songSelections, int k) {
    }
}
