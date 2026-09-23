import java.io.IOException;
import java.util.List;
import java.util.Map;

/*
 * Layer: application / runner / PA1
 * Role: Start to finish pipeline for basic song stats:
 *       read ratings -> aggregate by song -> write song stats CSV.
 * Depends on: RunConfig, CsvDataReader, SongStatsAggregator, CsvSongDataWriter
 * Used by: Mode.PA1
 */
public final class SongStatsRunnerPA1 implements ModeRunner {

    // reads ratings -> aggregate by song -> write song stats to output CSV.
    @Override
    public void run(ModeConfig config) throws IOException {
        List<RatingRow> rows = new RatingsReader().read(config.input());
        Map<String, SongStats> bySong = new SongStatsAggregator().aggregateBySong(rows);

        new SongDataWriter().write(config.output(), bySong);
    }
}
