import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class ScoreNormalizerTest {
    
    @Test
    void normalizeTwoRatings() {
        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(new Song("song1"), 2.0);
        alexRatings.put(new Song("song2"), 4.0);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(new Username("alex"), alexProfile);
        Profiles profiles = new Profiles(byUser);

        ScoreNormalizer.UserNormalizationResult result = new ScoreNormalizer().normalize(profiles);
        Profiles normalized = result.normalized();

        UserProfile alexNorm = normalized.asMap().get(new Username("alex"));

        assertEquals(-1.0, alexNorm.asMap().get(new Song("song1")));
        assertEquals(1.0, alexNorm.asMap().get(new Song("song2")));
    }

    @Test
    void identicalRatingsEqualZero() {
        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(new Song("song1"), 3.0);
        alexRatings.put(new Song("song2"), 3.0);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(new Username("alex"), alexProfile);
        Profiles profiles = new Profiles(byUser);

        ScoreNormalizer.UserNormalizationResult result = new ScoreNormalizer().normalize(profiles);
        Profiles normalized = result.normalized();

        UserProfile alexNorm = normalized.asMap().get(new Username("alex"));

        assertEquals(0.0, alexNorm.asMap().get(new Song("song1")));
        assertEquals(0.0, alexNorm.asMap().get(new Song("song2")));
    }

    @Test
    void preserveAndSkipNaN() {
        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(new Song("song1"), 2.0);
        alexRatings.put(new Song("song2"), 4.0);
        alexRatings.put(new Song("song3"), Double.NaN);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(new Username("alex"), alexProfile);
        Profiles profiles = new Profiles(byUser);

        ScoreNormalizer.UserNormalizationResult result = new ScoreNormalizer().normalize(profiles);
        Profiles normalized = result.normalized();

        UserProfile alexNorm = normalized.asMap().get(new Username("alex"));

        assertEquals(-1.0, alexNorm.asMap().get(new Song("song1")));
        assertEquals(1.0, alexNorm.asMap().get(new Song("song2")));
        assertTrue(Double.isNaN(alexNorm.asMap().get(new Song("song3"))));
    }

    @Test
    void normalizeEachUserIndependently() {
        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(new Song("song1"), 2.0);
        alexRatings.put(new Song("song2"), 4.0);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Song, Double> zachRatings = new TreeMap<>();
        zachRatings.put(new Song("song1"), 3.0);
        zachRatings.put(new Song("song2"), 3.0);
        UserProfile zachProfile = new UserProfile(zachRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(new Username("alex"), alexProfile);
        byUser.put(new Username("zach"), zachProfile);
        Profiles profiles = new Profiles(byUser);

        ScoreNormalizer.UserNormalizationResult result = new ScoreNormalizer().normalize(profiles);
        Profiles normalized = result.normalized();

        UserProfile alexNorm = normalized.asMap().get(new Username("alex"));
        UserProfile zachNorm = normalized.asMap().get(new Username("zach"));

        assertEquals(-1.0, alexNorm.asMap().get(new Song("song1")));
        assertEquals(1.0, alexNorm.asMap().get(new Song("song2")));

        assertEquals(0.0, zachNorm.asMap().get(new Song("song1")));
        assertEquals(0.0, zachNorm.asMap().get(new Song("song2")));
    }

    @Test
    void emptyRowReturnsEmptyRow() {
        Map<Song, Double> alexRatings = new TreeMap<>();
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(new Username("alex"), alexProfile);
        Profiles profiles = new Profiles(byUser);

        ScoreNormalizer.UserNormalizationResult result = new ScoreNormalizer().normalize(profiles);
        Profiles normalized = result.normalized();

        UserProfile alexNorm = normalized.asMap().get(new Username("alex"));

        assertEquals(0, alexNorm.asMap().size());
    }
}
