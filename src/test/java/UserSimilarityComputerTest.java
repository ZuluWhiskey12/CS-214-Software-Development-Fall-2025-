import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class UserSimilarityComputerTest {

    void produceDistancesForEachPair() {
        Song song1 = new Song("song1");
        Song song2 = new Song("song2");

        Map<Song, Double> ratingsA = new TreeMap<>();
        ratingsA.put(song1, 3.0);
        ratingsA.put(song2, 4.0);
        UserProfile profileA = new UserProfile(ratingsA);

        Map<Song, Double> ratingsB = new TreeMap<>();
        ratingsB.put(song1, 3.0);
        ratingsB.put(song2, 4.0);
        UserProfile profileB = new UserProfile(ratingsB);

        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(alex, profileA);
        byUser.put(zach, profileB);

        Profiles normalized = new Profiles(byUser);
        DistanceMatrix distances = new UserSimilarityComputer().distances(normalized);

        assertEquals(0.0, distances.get(alex, zach));
    }

    @Test
    void ignoreNansAndComputeDistance() {
        Song song1 = new Song("song1");
        Song song2 = new Song("song2");

        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(song1, 3.0);
        alexRatings.put(song2, Double.NaN);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Song, Double> zachRatings = new TreeMap<>();
        zachRatings.put(song1, 4.0);
        zachRatings.put(song2, 5.0);
        UserProfile zachProfile = new UserProfile(zachRatings);

        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(alex, alexProfile);
        byUser.put(zach, zachProfile);
        Profiles normalized = new Profiles(byUser);

        DistanceMatrix distances = new UserSimilarityComputer().distances(normalized);
        double distance = distances.get(alex, zach);

        assertEquals(1.0, distance);
    }

    @Test
    void noOverlappingNonNaNRatingsGivesNaN() {
        Song song1 = new Song("song1");
        Song song2 = new Song("song2");

        Map<Song, Double> user1Ratings = new TreeMap<>();
        user1Ratings.put(song1, 1.0);
        user1Ratings.put(song2, Double.NaN);

        Map<Song, Double> user2Ratings = new TreeMap<>();
        user2Ratings.put(song1, Double.NaN);
        user2Ratings.put(song2, 2.0);

        UserProfile user1Profile = new UserProfile(user1Ratings);
        UserProfile user2Profile = new UserProfile(user2Ratings);

        Username user1 = new Username("user1");
        Username user2 = new Username("user2");

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(user1, user1Profile);
        byUser.put(user2, user2Profile);

        Profiles normalized = new Profiles(byUser);

        DistanceMatrix distances = new UserSimilarityComputer().distances(normalized);

        double distance = distances.get(user1, user2);

        assertTrue(Double.isNaN(distance));
    }
}
