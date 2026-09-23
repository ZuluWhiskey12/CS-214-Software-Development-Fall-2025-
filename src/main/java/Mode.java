/*
 * Layer: framework / configuration
 * Role: Enumerate program modes (PA1–PA4) and map each to its Runner.
 * Depends on: Runner, SongStatsRunnerPA1, UserAnalysisRunnerPA2, SongSimilarityRunnerPA3, UserPredictionsRunnerPA4, KmeansClusteringRunnerPA5
 * Used by: Cs214Project, ArgsValidator
 */

public enum Mode {
    // Each mode creates and stores its executable PA class
    PA1(new SongStatsRunnerPA1()),
    PA2(new UserAnalysisRunnerPA2()),
    PA3(new SongSimilarityRunnerPA3()),
    PA4(new UserPredictionsRunnerPA4()),
    PA5(new SongRecommendationsRunnerPA5()),
    PA6(new PlayListRunnerPA6());

    // Holds the executable that runs the mode
    private final ModeRunner executable;

    // Binds a Mode to its runner instance
    Mode(ModeRunner executable) {
        this.executable = executable;
    }

    // Returns the correct runner for selected Mode
    public ModeRunner getExecutable() {
        return executable;
    }
}
