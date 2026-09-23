import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class UserProfileTest {
    
    @Test
    void songsAndAsMap() {
        Song song1 = new Song("song1");
        Song song2 = new Song("song2");

        Map<Song, Double> map = new TreeMap<>();
        map.put(song1, 4.0);
        map.put(song2, Double.NaN);

        UserProfile profile = new UserProfile(map);

        assertEquals(4.0, profile.rating(song1));
        assertTrue(Double.isNaN(profile.rating(song2)));

        assertEquals(map, profile.asMap());
    }
}
