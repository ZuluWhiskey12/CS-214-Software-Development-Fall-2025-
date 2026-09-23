import java.io.IOException;
import java.util.List;

/*
 * Layer: application / runner / PA4
 * Role: Start to finish pipeline for predictions:
 *       read -> filter cooperative users -> build Profiles -> normalize -> compute user similarities -> predict missing ratings -> write predictions CSV.
 * Depends on: RunConfig, CsvDataReader, UserCooperationFilter, UserProfileBuilder, ScoreNormalizer, UserSimilarityComputer, DistanceMatrix, UserPredictionComputer, CsvUserPredictionsWriter, Errors
 * Used by: Mode.PA4
 */
public final class UserPredictionsRunnerPA4 implements ModeRunner {

    // read -> filter cooperative users -> build Profiles -> normalize -> compute user similarities (distances) -> predict missing ratings -> write predictions CSV.
    @Override
    public void run(ModeConfig config) throws IOException {

        List<RatingRow> rows = new RatingsReader().read(config.input());
        List<RatingRow> filteredRows = new UserCooperationFilter().filter(rows);

        Profiles profiles = new UserProfileBuilder().buildProfiles(filteredRows);
        if (profiles.users().size() < 2) {
            throw Errors.tooFewCooperativeUsers();
        }

        ScoreNormalizer.UserNormalizationResult result = new ScoreNormalizer().normalize(profiles);
        Profiles normalized = result.normalized();
        DistanceMatrix distances = new UserSimilarityComputer().distances(normalized);

        List<PredictionRow> predictions = new UserPredictionComputer().predict(profiles, result, distances);
        if (predictions.isEmpty()) {
            throw Errors.noPredictions();
        }

        new UserPredictionsWriter().write(config.output(), predictions);
    }
}
