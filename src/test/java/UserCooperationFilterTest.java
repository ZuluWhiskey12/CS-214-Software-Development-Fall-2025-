import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class UserCooperationFilterTest {

    @Test
    void removeUsersWithOneUniqueRating() {
        List<RatingRow> rows = List.of(
            new RatingRow("Song1", "droppedUser", 1),
            new RatingRow("Song2", "droppedUser", 1),
            new RatingRow("Song1", "keptUser", 1),
            new RatingRow("Song2", "keptUser", 2));

        List<RatingRow> kept = new UserCooperationFilter().filter(rows);

            assertEquals(2, kept.size());
            assertEquals("keptUser", kept.get(0).user());
            assertEquals("keptUser", kept.get(1).user());
    }

    @Test
    void dropSongsRatedByDroppedUsers() {
        List<RatingRow> rows = List.of(
            new RatingRow("Song1", "droppedUser", 1),
            new RatingRow("Song2", "droppedUser", 1),
            new RatingRow("Song3", "droppedUser", 1),
            new RatingRow("Song4", "keptUser", 3),
            new RatingRow("Song2", "keptUser", 2));

        List<RatingRow> kept = new UserCooperationFilter().filter(rows);

        assertEquals(2, kept.size());
        assertEquals("keptUser", kept.get(0).user());
        assertEquals("Song4", kept.get(0).song());

        assertEquals("keptUser", kept.get(1).user());
        assertEquals("Song2", kept.get(1).song());
    }

    @Test
    void allUsersUncooperative() {
        List<RatingRow> rows = List.of(
            new RatingRow("Song3", "droppedUser", 1),
            new RatingRow("Song4", "keptUser", 3));   

        List<RatingRow> kept = new UserCooperationFilter().filter(rows);

        assertTrue(kept.isEmpty());
    }

    @Test
    void emptyInputReturnsEmpty() {
        List<RatingRow> kept = new UserCooperationFilter().filter(List.of());

        assertTrue(kept.isEmpty());
    }

    @Test
    void keepsUserWithTwoUniqueRatings() {
        List<RatingRow> rows = List.of(
            new RatingRow("SongA", "bob", 3),
            new RatingRow("SongB", "bob", 4));

        List<RatingRow> kept = new UserCooperationFilter().filter(rows);

        assertEquals(2, kept.size());
        assertEquals("bob", kept.get(0).user());
        assertEquals("bob", kept.get(1).user());
    }

    @Test
    void duplicateRatingsNotCooperative() {
        List<RatingRow> rows = List.of(
            new RatingRow("SongA", "flat", 3),
            new RatingRow("SongA", "flat", 3),
            new RatingRow("SongB", "flat", 3));

        List<RatingRow> kept = new UserCooperationFilter().filter(rows);

        assertTrue(kept.isEmpty());
    }
}
