import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SongStatsAggregatorTest {
    @Test
    void emptyInputGivesEmptyMap() {
        SongStatsAggregator songRows = new SongStatsAggregator();
        var map = songRows.aggregateBySong(List.of());
        
        assertTrue(map.isEmpty());
    }

    @Test
    void oneRowCreatesOneStatEntry() {
        RatingRow row = new RatingRow("All Star", "alex", 4);

        SongStatsAggregator songRows = new SongStatsAggregator();
        var map = songRows.aggregateBySong(List.of(row));

        assertEquals(1, map.get("All Star").getCount());
        assertEquals(4, map.get("All Star").getMean());
    }

    @Test 
    void multipleRowsWithSameSongAggregatedTogether() {
        RatingRow row1 = new RatingRow("All Star", "alex", 4);
        RatingRow row2 = new RatingRow("All Star", "charlie", 2);


        SongStatsAggregator songRows = new SongStatsAggregator();
        var map = songRows.aggregateBySong(List.of(row1, row2));
        SongStats songName = map.get("All Star");

        assertEquals(2, songName.getCount());
        assertEquals(3.0, songName.getMean());
        assertEquals(1.0, songName.getPopulationStdDev());
    }

    @Test
    void multipleSongsSortedAtoZ() {
        RatingRow row1 = new RatingRow("Sweet Home Alabama", "cameron", 2);
        RatingRow row2 = new RatingRow("All Star", "alex", 4);


        SongStatsAggregator songRows = new SongStatsAggregator();
        var map = songRows.aggregateBySong(List.of(row1, row2));
        var keys = new ArrayList<>(map.keySet());

        assertEquals(List.of("All Star", "Sweet Home Alabama"), keys);
    }
}
