import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class UserPredictionComputerTest {

    @Test
    void predictScoreFromClosetNeighbor() {
        Song song = new Song("song1");
        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<Username, UserProfile> rawByUser = new TreeMap<>();
        rawByUser.put(zach, new UserProfile(new TreeMap<>(Map.of(song, Double.NaN))));
        rawByUser.put(alex, new UserProfile(new TreeMap<>(Map.of(song, 4.0))));
        Profiles raw = new Profiles(rawByUser);

        Map<Username, UserProfile> normalizedByUser = new TreeMap<>();
        normalizedByUser.put(zach, new UserProfile(new TreeMap<>(Map.of(song, Double.NaN))));
        normalizedByUser.put(alex, new UserProfile(new TreeMap<>(Map.of(song, 1.0))));
        Profiles normalizedProfiles = new Profiles(normalizedByUser);

        Map<Username, Double> means = Map.of(zach, 3.0);
        Map<Username, Double> stdDevs = Map.of(zach, 1.0);
        ScoreNormalizer.UserNormalizationResult normalize = new ScoreNormalizer.UserNormalizationResult(normalizedProfiles, means, stdDevs);

        DistanceMatrix distances = new DistanceMatrix(Map.of(new UsernamePair(alex, zach), 0.5));
        List<PredictionRow> rows = new UserPredictionComputer().predict(raw, normalize, distances);
        
        assertEquals(1, rows.size());
        assertEquals("song1", rows.get(0).song());
        assertEquals("zach", rows.get(0).user());
        assertEquals(4.0, rows.get(0).prediction());
    }

    @Test
    void returnNaNForNoUsableNeighborScore() {
        Song song = new Song("song1");
        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<Username, UserProfile> rawByUser = new TreeMap<>();
        rawByUser.put(zach, new UserProfile(new TreeMap<>(Map.of(song, Double.NaN))));
        rawByUser.put(alex, new UserProfile(new TreeMap<>(Map.of(song, 5.0))));
        Profiles raw = new Profiles(rawByUser);

        Map<Username, UserProfile> normalizedByUser = new TreeMap<>();
        normalizedByUser.put(zach, new UserProfile(new TreeMap<>(Map.of(song, Double.NaN))));
        normalizedByUser.put(alex, new UserProfile(new TreeMap<>(Map.of(song, Double.NaN))));
        Profiles normalizedProfiles = new Profiles(normalizedByUser);

        Map<Username, Double> means = Map.of(zach, 3.0);
        Map<Username, Double> stdDevs = Map.of(zach, 1.0);
        ScoreNormalizer.UserNormalizationResult normalize = new ScoreNormalizer.UserNormalizationResult(normalizedProfiles, means, stdDevs);

        DistanceMatrix distances = new DistanceMatrix(Map.of(new UsernamePair(alex, zach), 0.5));
        List<PredictionRow> rows = new UserPredictionComputer().predict(raw, normalize, distances);

        assertEquals(1, rows.size());
        assertTrue(Double.isNaN(rows.get(0).prediction()));
    }

    @Test
    void skipRatedSongs() {
        Song song1 = new Song("song1");
        Username alex = new Username("alex");

        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(song1, 4.0);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Username, UserProfile> rawByUser = new TreeMap<>();
        rawByUser.put(alex, alexProfile);
        Profiles rawProfiles = new Profiles(rawByUser);

        Profiles normalizedProfiles = rawProfiles;

        Map<Username, Double> means = Map.of(alex, 3.0);
        Map<Username, Double> stdDevs = Map.of(alex, 1.0);
        ScoreNormalizer.UserNormalizationResult normalize = new ScoreNormalizer.UserNormalizationResult(normalizedProfiles, means, stdDevs);

        DistanceMatrix distances = new DistanceMatrix(new TreeMap<>());
        List<PredictionRow> rows = new UserPredictionComputer().predict(rawProfiles, normalize, distances);

        assertTrue(rows.isEmpty());
    }
}
