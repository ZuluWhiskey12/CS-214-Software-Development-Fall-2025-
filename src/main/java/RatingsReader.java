import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/*
 * Layer: IO / input
 * Role: Read the input ratings CSV and parse each row into a validated RatingRow.
 * Depends on: RatingRow, Errors, Apache Commons CSV
 * Used by: SongStatsRunnerPA1, UserAnalysisRunnerPA2, SongSimilarityRunnerPA3, UserPredictionsRunnerPA4
 */

public final class RatingsReader {
    //Formating for reading CSV: Trims whitespace and ignores empty lines
    private static final CSVFormat format = CSVFormat.DEFAULT.builder()
            .setTrim(true)
            .setIgnoreEmptyLines(true)
            .build();

    //Open the input file and parse all the rows in to a list of RatingRows
    public List<RatingRow> read(Path inputPath) throws IOException {
        try (Reader reader = Files.newBufferedReader(inputPath);
                CSVParser parser = new CSVParser(reader, format)) {

            List<RatingRow> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                rows.add(parseRow(record));
            }
            return rows;
        }
    }

    //Validate each line of the CSV and build a RatingRow with (song, user, and rating between 1 - 5)
    private static RatingRow parseRow(CSVRecord record) {
        if (record.size() != 3) {
            throw Errors.malformedRow();
        }

        String song = record.get(0);
        String user = record.get(1);
        String ratingStr = record.get(2);

        final int rating;
        try {
            rating = Integer.parseInt(ratingStr);
        } catch (NumberFormatException e) {
            throw Errors.nonIntegerRating();
        }
        if (rating < 1 || rating > 5) {
            throw Errors.ratingOutOfRange();
        }

        return new RatingRow(song, user, rating);
    }
}