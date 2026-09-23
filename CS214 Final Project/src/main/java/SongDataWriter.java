import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/*
 * Layer: IO / output / PA1
 * Role: Write per song statistics (count, mean, population std dev) to a CSV file.
 * Depends on: SongStats, Apache Commons CSV
 * Used by: SongStatsRunnerPA1
 */

public final class SongDataWriter {

    //Print one CSV row per song with its count, mean and std dev
    public void write(Path outputPath, Map<String, SongStats> bySong) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("song", "number of ratings", "mean", "standard deviation")
                .build();

        try (Writer writer = Files.newBufferedWriter(outputPath);
                CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (String song : bySong.keySet()) {
                SongStats songStats = bySong.get(song);

                printer.printRecord(
                        song,
                        songStats.getCount(),
                        songStats.getMean(),
                        songStats.getPopulationStdDev());
            }
        }
    }
}
