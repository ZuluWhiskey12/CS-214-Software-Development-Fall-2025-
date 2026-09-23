import java.io.IOException;
import java.util.List;

/*
 * Layer: application / runner / PA2
 * Role: Start to finish pipeline for user profiles:
 *       read ratings -> filter cooperative users -> build Profiles -> write user ratings CSV.
 * Depends on: RunConfig, CsvDataReader, UserCooperationFilter, UserProfileBuilder, CsvUserRatingsWriter
 * Used by: Mode.PA2
 */
public final class UserAnalysisRunnerPA2 implements ModeRunner {

    //read ratings -> filter cooperative users -> build Profiles -> write user by song ratings to output CSV.
    @Override
    public void run(ModeConfig config) throws IOException {
        List<RatingRow> rows = new RatingsReader().read(config.input());
        List<RatingRow> filteredRows = new UserCooperationFilter().filter(rows);
        Profiles profiles = new UserProfileBuilder().buildProfiles(filteredRows);

        new UserRatingsWriter().write(config.output(), profiles);
    }
}
