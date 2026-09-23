import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class UserProfileBuilderTest {

    @Test
    void buildsUserSongTableWithNaN() {
        List<RatingRow> rows = List.of(
            new RatingRow("Song1", "alex", 5),
            new RatingRow("Song2", "alex", 3),
            new RatingRow("Song1", "charlie", 4));

        UserProfileBuilder builder = new UserProfileBuilder();
        Profiles profiles = builder.buildProfiles(rows);
        Map<Username, UserProfile> byUser = profiles.asMap();

        assertEquals(2, byUser.size());
        assertTrue(byUser.containsKey(new Username("alex")));
        assertTrue(byUser.containsKey(new Username("charlie")));

        UserProfile alex = byUser.get(new Username("alex"));
        UserProfile charlie = byUser.get(new Username("charlie"));

        assertTrue(alex.asMap().containsKey(new Song("Song1")));
        assertTrue(alex.asMap().containsKey(new Song("Song2")));
        assertTrue(charlie.asMap().containsKey(new Song("Song1")));
        assertTrue(charlie.asMap().containsKey(new Song("Song2")));

        assertEquals(5.0, alex.asMap().get(new Song("Song1")));
        assertEquals(3.0, alex.asMap().get(new Song("Song2")));
        assertEquals(4.0, charlie.asMap().get(new Song("Song1")));
        assertTrue(Double.isNaN(charlie.asMap().get(new Song("Song2"))));
    }

    @Test
    void duplicateUserSonglastRatingKept() {
        List<RatingRow> rows = List.of(
            new RatingRow("Song1", "alex", 2),
            new RatingRow("Song1", "alex", 5));

        UserProfileBuilder builder = new UserProfileBuilder();
        Profiles profiles = builder.buildProfiles(rows);
        UserProfile alex = profiles.asMap().get(new Username("alex"));
        
        assertEquals(5.0, alex.asMap().get(new Song("Song1")));
    }
}
