import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ArgumentValidatorTest {
    @TempDir
    Path tmp;

    @Test
    void givenTwoArgsRunPA1() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        ModeConfig cfg = ArgumentValidator.validate(new String[] { input.toString(), output.toString() });

        assertEquals(Mode.PA1, cfg.mode());
        assertEquals(input, cfg.input());
        assertEquals(output, cfg.output());
    }

    @Test
    void givenFlagARunPA2() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        ModeConfig cfg = ArgumentValidator.validate(new String[] { input.toString(), output.toString(), "-a" });

        assertEquals(Mode.PA2, cfg.mode());
    }

    @Test
    void givenFlagURunPA3() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        ModeConfig cfg = ArgumentValidator.validate(new String[] { input.toString(), output.toString(), "-u" });

        assertEquals(Mode.PA3, cfg.mode());
    }

    @Test
    void givenFlagPRunPA4() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        ModeConfig cfg = ArgumentValidator.validate(new String[] { input.toString(), output.toString(), "-p" });

        assertEquals(Mode.PA4, cfg.mode());
    }

    @Test
    void givenFlagRRunPA5() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        ModeConfig cfg = ArgumentValidator
                .validate(new String[] { input.toString(), output.toString(), "-r", "song1", "song2" });

        assertEquals(Mode.PA5, cfg.mode());
    }

    @Test
    void givenFlagRWithoutSongSelections() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(new String[] { input.toString(), output.toString(), "-r" }));
    }

    @Test
    void givenFlagRWithDuplicateSongs() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator
                        .validate(new String[] { input.toString(), output.toString(), "-r", "song1", "song1" }));
    }

    @Test
    void givenIncorrectThirdArgThrowsException() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(new String[] { input.toString(), output.toString(), "-z" }));
    }

    @Test
    void givenOneArgThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(new String[] { "oneArg" }));
    }

    @Test
    void nonCsvInput() {
        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(new String[] { "input.txt", "output.csv" }));
    }

    @Test
    void nonCsvOutput() {
        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(new String[] { "input.csv", "output.txt" }));
    }

    @Test
    void missingInput() throws IOException {
        Path missing = tmp.resolve("no_such.csv");
        Path out = tmp.resolve("out.csv");

        assertThrows(IOException.class,
                () -> ArgumentValidator.validate(new String[] { missing.toString(), out.toString() }));
    }

    @Test
    void emptyInput() throws IOException {
        Path input = tmp.resolve("in.csv");
        Files.writeString(input, "");
        Path output = tmp.resolve("out.csv");

        assertThrows(IOException.class,
                () -> ArgumentValidator.validate(new String[] { input.toString(), output.toString() }));
    }

    @Test
    void outputMissingDirectory() throws IOException {
        Path input = tmp.resolve("in.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("missingDir").resolve("out.csv");

        assertThrows(IOException.class,
                () -> ArgumentValidator.validate(new String[] { input.toString(), output.toString() }));
    }

    @Test
    void givenFlagSRunPA6() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        ModeConfig cfg = ArgumentValidator.validate(
                new String[] { input.toString(), output.toString(), "-s", "2", "song1", "song2", "song3" });

        assertEquals(Mode.PA6, cfg.mode());
        assertEquals(2, cfg.k());
        assertEquals(3, cfg.songSelections().size());
    }

    @Test
    void givenFlagSWithTooFewArgsThrows() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        // args length < 5 for -s
        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(new String[] { input.toString(), output.toString(), "-s", "1" }));
    }

    @Test
    void givenFlagSWithNonNumericKThrows() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(
                        new String[] { input.toString(), output.toString(), "-s", "notANumber", "song1" }));
    }

    @Test
    void givenFlagSWithZeroKThrows() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(
                        new String[] { input.toString(), output.toString(), "-s", "0", "song1" }));
    }

    @Test
    void givenFlagSWithFewerSelectionsThanKThrows() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        // k = 3 but only 2 selections
        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(
                        new String[] { input.toString(), output.toString(), "-s", "3", "song1", "song2" }));
    }

    @Test
    void givenPA2FlagWithExtraArgThrowsIncorrectCount() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "song,user,5");
        Path output = tmp.resolve("output.csv");

        // -a but too many args for PA2–PA4
        assertThrows(IllegalArgumentException.class,
                () -> ArgumentValidator.validate(
                        new String[] { input.toString(), output.toString(), "-a", "extra" }));
    }
}
