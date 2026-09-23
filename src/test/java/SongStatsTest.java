import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SongStatsTest {
    @Test
    void countValueTest() {
        SongStats ratingStats = new SongStats();
        ratingStats.add(4);

        assertEquals(1, ratingStats.getCount());
    }

    @Test
    void standardDeviationNoRating() {
        SongStats ratingStats = new SongStats();

        assertEquals(0.0, ratingStats.getPopulationStdDev());
    }

    @Test
    void meanOfSingleRating() {
        SongStats ratingStats = new SongStats();
        ratingStats.add(4);

        assertEquals(4.0, ratingStats.getMean());
    }

    @Test
    void standardDeviationOneRating() {
        SongStats ratingStats = new SongStats();
        ratingStats.add(4);

        assertEquals(0.0, ratingStats.getPopulationStdDev());
    }

    @Test 
    void meanOfTwoRatings() {
        SongStats ratingStats = new SongStats();
        ratingStats.add(4);
        ratingStats.add(2);

        assertEquals(3, ratingStats.getMean());
    }

    @Test
    void standardDeviationTwoRating() {
        SongStats ratingStats = new SongStats();
        ratingStats.add(4);
        ratingStats.add(2);

        assertEquals(1.0, ratingStats.getPopulationStdDev());
    }

    @Test 
    void meanAndStdDevThreeRating() {
        SongStats ratingStats = new SongStats();
        ratingStats.add(4);
        ratingStats.add(4);
        ratingStats.add(1);

        assertEquals(3.0, ratingStats.getMean());
        assertEquals(1.4142, ratingStats.getPopulationStdDev(), 0.0001);
    }
}
