import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class OneRatingSongFilterTest {
    
    @Test
    void removesSongsWithOneRating() {
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

        OneRatingSongFilter.FilterResults results = new OneRatingSongFilter().filter(profiles);
        Set<Song> droppedSongs = results.droppedSongs();

        assertTrue(droppedSongs.contains(song2));
        assertFalse(droppedSongs.contains(song1));
    }

    @Test
    void notDroppedSongs() {
        Song song1 = new Song("song1");

        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(song1, 4.0);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Song, Double> zachRatings = new TreeMap<>();
        zachRatings.put(song1, 2.0);
        UserProfile zachProfile = new UserProfile(zachRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(alex, alexProfile);
        byUser.put(zach, zachProfile);
        Profiles profiles = new Profiles(byUser);

        OneRatingSongFilter.FilterResults results = new OneRatingSongFilter().filter(profiles);
        Set<Song> droppedSongs = results.droppedSongs();

        assertFalse(droppedSongs.contains(song1));
    }
}
