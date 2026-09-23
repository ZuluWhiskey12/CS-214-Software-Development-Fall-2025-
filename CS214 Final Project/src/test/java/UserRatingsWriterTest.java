import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class UserRatingsWriterTest {

    @TempDir Path tmp;
    @Test
    void headerOnly() throws IOException {
        Path output = tmp.resolve("userAnalysis.csv");

        Profiles emptyProfiles = new Profiles(new TreeMap<>());
        new UserRatingsWriter().write(output, emptyProfiles);

        String got = Files.readString(output).stripTrailing();

        assertEquals("username,song,rating", got);
    }

    @Test
    void integerRow() throws IOException {
        Profiles profiles = singleRatingProfiles("alex", "All Star", 2.0);

        Path output = tmp.resolve("userAnalysis.csv");
        new UserRatingsWriter().write(output, profiles);

        String got = Files.readString(output).replace("\r\n", "\n").stripTrailing();
        String want = "username,song,rating\n" + "alex,All Star,2";

        assertEquals(want, got);
    }

    @Test
    void nonIntegerRow() throws IOException {
        Profiles profiles = singleRatingProfiles("alex", "All Star", 3.0);

        Path output = tmp.resolve("userAnalysis.csv");
        new UserRatingsWriter().write(output, profiles);

        String got = Files.readString(output).replace("\r\n", "\n").stripTrailing();
        String want = "username,song,rating\n" + "alex,All Star,3";

        assertEquals(want, got);
    }

    @Test
    void NaNRow() throws IOException {
        Profiles profiles = singleRatingProfiles("alex", "All Star", Double.NaN);

        Path output = tmp.resolve("userAnalysis.csv");
        new UserRatingsWriter().write(output, profiles);

        String got = Files.readString(output).replace("\r\n", "\n").stripTrailing();
        String want = "username,song,rating\n" + "alex,All Star,NaN";

        assertEquals(want, got);
    }

    private static Profiles singleRatingProfiles(String user, String song, double rating) {
        Map<Song, Double> row = new TreeMap<>();
        row.put(new Song(song), rating);

        UserProfile profile = new UserProfile(row);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(new Username(user), profile);

        return new Profiles(byUser);
    }
}
