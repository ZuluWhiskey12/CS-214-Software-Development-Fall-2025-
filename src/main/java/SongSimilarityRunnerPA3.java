import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
 * Layer: application / runner / PA3
 * Role: Start to finish pipeline for song similarities:
 *       read -> filter cooperative users -> build Profiles -> normalize -> compute similarities -> write CSV.
 * Depends on: RunConfig, CsvDataReader, UserCooperationFilter, UserProfileBuilder, ScoreNormalizer, SongSimilarityLogic, SongSimilarityComputer, CsvSongSimilarityWriter, Errors
 * Used by: Mode.PA3
 */
public final class SongSimilarityRunnerPA3 implements ModeRunner {

    //read -> filter cooperative users -> build Profiles -> normalize -> compute similarities -> write CSV output.
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

        Map<SongPair, Double> scores = new SongSimilarityComputer().similarities(normalized);

        new SongSimilarityWriter().write(config.output(), scores);
    }
}
