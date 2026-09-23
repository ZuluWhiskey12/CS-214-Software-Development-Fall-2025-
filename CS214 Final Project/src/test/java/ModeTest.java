import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ModeTest {
    @Test
    void pa1ReturnsSongStatsRunnerPA1() {
        ModeRunner run = Mode.PA1.getExecutable();

        assertEquals(SongStatsRunnerPA1.class, run.getClass());
    }

    @Test 
    void pa2ReturnsUserAnalysisRunnerPA2() {
        ModeRunner run = Mode.PA2.getExecutable();

        assertEquals(UserAnalysisRunnerPA2.class, run.getClass());
    }

    @Test
    void pa3ReturnsUserSimilarityRunnerPA3() {
        ModeRunner run = Mode.PA3.getExecutable();

        assertEquals(SongSimilarityRunnerPA3.class, run.getClass());
    }

    @Test
    void pa4ReturnsUserSimilarityRunnerPA4() {
        ModeRunner run = Mode.PA4.getExecutable();

        assertEquals(UserPredictionsRunnerPA4.class, run.getClass());
    }

    @Test
    void pa5ReturnsKMeansClusteringRunnerPA5() {
        ModeRunner run = Mode.PA5.getExecutable();
        
        assertEquals(SongRecommendationsRunnerPA5.class, run.getClass());
    }
}
