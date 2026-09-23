import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/*
 * Layer: IO / output / PA4
 * Role: Write predicted ratings (song, user, predicted rating) to a CSV file.
 * Depends on: PredictionRow, Apache Commons CSV
 * Used by: UserPredictionsRunnerPA4
 */
public final class UserPredictionsWriter {

    //Writes all prediction rows to the output CSV 
    public void write(Path outputPath, List<PredictionRow> rows) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("song", "user", "predicted rating")
                .build();

        try (Writer writer = Files.newBufferedWriter(outputPath);
                CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (PredictionRow row : rows) {
                printer.printRecord(
                        row.song(),
                        row.user(),
                        formatPredictionValue(row.prediction()));

            }
        }
    }

    //Converts a prediction value to "NaN" or integer as a string
    private static String formatPredictionValue(Double value) {
        if (value.isNaN()) {
            return "NaN";
        }
        return Integer.toString(value.intValue());
    }
}
