import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/*
 * Layer: IO / output / PA5
 * ROle: Write the starting song to recommendation to CSV (user choice, recommendation).
 * Depends on: SongRecommendation
 * Used by: SongRecommendationsRunnerPA5
 */
public final class SongRecommendationsWriter {

    //Writes all the starting song, and recommendation song pairs to output csv
    public void write(Path outputPath, List<SongRecommendation> songRecommendations) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("user choice", "recommendation")
                .build();

        try (Writer writer = Files.newBufferedWriter(outputPath);
                CSVPrinter print = new CSVPrinter(writer, format)) {

            for (SongRecommendation song : songRecommendations) {
                print.printRecord(
                        song.startingSong(),
                        song.recommendedSong());
            }
        }
    }
}
