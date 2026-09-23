import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/*
 * Layer: IO / output / PA3
 * Role: Write song similarity scores to a CSV (name1, name2, similarity).
 * Depends on: SongIdPair, Apache Commons CSV
 * Used by: SongSimilarityRunnerPA3
 */
public final class SongSimilarityWriter {

    //Writes all song to song similarity values to a CSV file
    public void write(Path outputPath, Map<SongPair, Double> similarities) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("name1", "name2", "similarity")
                .build();

        try (Writer writer = Files.newBufferedWriter(outputPath);
                CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (SongPair pair : similarities.keySet()) {
                Double similarity = similarities.get(pair);
                printer.printRecord(
                        pair.song1(),
                        pair.song2(),
                        similarity);
            }
        }
    }
}
