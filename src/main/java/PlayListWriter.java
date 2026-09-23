import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PlayListWriter {

    public void write(Path outputPath, List<Song> songs) throws IOException {
        try (Writer writer = Files.newBufferedWriter(outputPath)) {
                for (Song song : songs) {
                        writer.write(song.value());
                        writer.write(System.lineSeparator());;
                }
        }
    }
}
