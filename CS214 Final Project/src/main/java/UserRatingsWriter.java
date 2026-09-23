import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/*
 * Layer: IO / output / PA2
 * Role: Write user profiles to CSV (username, song, rating), including NaN for missing ratings.
 * Depends on: Profiles, Username, UserProfile, Song
 * Used by: UserAnalysisRunnerPA2
 */
public final class UserRatingsWriter {

    //Write the user by song ratings to CSV file
    public void write(Path outputPath, Profiles profiles) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("username", "song", "rating")
                .build();

        try (Writer writer = Files.newBufferedWriter(outputPath);
                CSVPrinter print = new CSVPrinter(writer, format)) {

            //Iterate users in sorted order
            for (Username username : new TreeSet<>(profiles.asMap().keySet())) {
                UserProfile profile = profiles.get(username);
                Map<Song, Double> row = profile.asMap();

                //Iterate songs in sorted order
                for (Song song : new TreeSet<>(row.keySet())) {
                    Double rating = row.get(song);
                    print.printRecord(
                            username,
                            song,
                            formatRating(rating));

                }
            }
        }
    }

    //Convert ratings to either "NaN" or integer value as a string
    private static String formatRating(Double ratingValue) {
        if (ratingValue.isNaN()) {
            return "NaN";
        }
        return Integer.toString(ratingValue.intValue());
    }
}
