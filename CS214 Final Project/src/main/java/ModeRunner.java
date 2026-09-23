import java.io.IOException;

/*
 * Layer: framework / orchestration
 * Role: Common interface for all PA mode runners with a single run(RunConfig) entry point.
 * Depends on: RunConfig
 * Implemented by: SongStatsRunnerPA1, UserAnalysisRunnerPA2, SongSimilarityRunnerPA3, UserPredictionsRunnerPA4
 */
public interface ModeRunner {
    // Entry point for the modes's logic
    void run(ModeConfig config) throws IOException;
}