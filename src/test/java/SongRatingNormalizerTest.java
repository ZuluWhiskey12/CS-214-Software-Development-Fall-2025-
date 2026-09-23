import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class SongRatingNormalizerTest {

    @Test
    void usersAndSongSorted() {
        Song song1 = new Song("song1");
        Song song2 = new Song("song2");

        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(song1, 4.0);
        alexRatings.put(song2, 3.0);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Song, Double> zachRatings = new TreeMap<>();
        zachRatings.put(song1, 2.0);
        zachRatings.put(song2, 3.0);
        UserProfile zachProfile = new UserProfile(zachRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(alex, alexProfile);
        byUser.put(zach, zachProfile);
        Profiles profiles = new Profiles(byUser);

        SongRatingNormalizer normalizer = new SongRatingNormalizer();
        SongRatingNormalizer.SongNormalizationResult result = normalizer.normalize(profiles);

        assertEquals(List.of(alex, zach), result.users());
        assertEquals(List.of(song1, song2), result.songs());
    }

    @Test
    void normalizesRatings() {
        Song song1 = new Song("song1");

        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(song1, 3.0);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Song, Double> zachRatings = new TreeMap<>();
        zachRatings.put(song1, 5.0);
        UserProfile zachProfile = new UserProfile(zachRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(alex, alexProfile);
        byUser.put(zach, zachProfile);
        Profiles profiles = new Profiles(byUser);

        SongRatingNormalizer normalizer = new SongRatingNormalizer();
        SongRatingNormalizer.SongNormalizationResult result = normalizer.normalize(profiles);
        double[][] ratings = result.normalizedRatings();

        assertEquals(-1.0, ratings[0][0]);
        assertEquals(1.0, ratings[0][1]);
    }
}
