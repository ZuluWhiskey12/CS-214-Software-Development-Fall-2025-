import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class SongSimilarityComputerTest {

    @Test
    void emptyProfilesProduceNoSongPairs() {
        Profiles normalized = new Profiles(new TreeMap<>());
        Map<SongPair, Double> got = new SongSimilarityComputer().similarities(normalized);

        assertTrue(got.isEmpty());
    }

    @Test
    void threeUsersOutputsThreePairs() {
        Song song1 = new Song("I Gotta a Feeling");
        Song song2 = new Song("Big Girls Don't Cry");
        Song song3 = new Song("Suit & Tie");

        Map<Song, Double> ratings = new TreeMap<>();
        ratings.put(song1, Double.NaN);
        ratings.put(song2, Double.NaN);
        ratings.put(song3, Double.NaN);
        UserProfile profile = new UserProfile(ratings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(new Username("zach"), profile);
        Profiles normalized = new Profiles(byUser);

        Map<SongPair, Double> got = new SongSimilarityComputer().similarities(normalized);
        
        assertEquals(3, got.size());
    }

    @Test
    void ignoreNansAndComputeDistance() {
        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(new Song("song1"), 3.0);
        alexRatings.put(new Song("song2"), Double.NaN);
        UserProfile alexProfile = new UserProfile(alexRatings);
        
        Map<Song, Double> zachRatings = new TreeMap<>();
        zachRatings.put(new Song("song1"), 4.0);
        zachRatings.put(new Song("song2"), 5.0);
        UserProfile zachProfile = new UserProfile(zachRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(new Username("alex"), alexProfile);
        byUser.put(new Username("zach"), zachProfile);
        Profiles normalized = new Profiles(byUser);

        Map<SongPair, Double> got = new SongSimilarityComputer().similarities(normalized);
        Double distance = got.get(new SongPair(new Song("song1"), new Song("song2")));

        assertEquals(1.0, distance);
    }

    @Test
    void songPairWithoutOverlapIsNaN() {
        Song song1 = new Song("song1");
        Song song2 = new Song("song2");

        Map<Song, Double> user1Ratings = new TreeMap<>();
        user1Ratings.put(song1, 1.0);
        user1Ratings.put(song2, Double.NaN);

        Map<Song, Double> user2Ratings = new TreeMap<>();
        user2Ratings.put(song1, Double.NaN);
        user2Ratings.put(song2, 2.0);

        UserProfile user1 = new UserProfile(user1Ratings);
        UserProfile user2 = new UserProfile(user2Ratings);

        Profiles normalizedProfiles = new Profiles(new TreeMap<>(Map.of(new Username("user1"), user1, new Username("user2"), user2)));
        Map<SongPair, Double> test = new SongSimilarityComputer().similarities(normalizedProfiles);

        assertTrue(Double.isNaN(test.get(new SongPair(song1, song2))));
    }    
}