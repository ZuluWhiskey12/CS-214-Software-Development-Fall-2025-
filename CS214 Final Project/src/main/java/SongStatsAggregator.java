import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * Layer: domain logic / aggregation / PA1
 * Role: Aggregate RatingRow list into per song SongStats in a sorted map.
 * Depends on: RatingRow, SongStats
 * Used by: SongStatsRunnerPA1
 */
public final class SongStatsAggregator {

    //Build a sorted map keyed with a song name and its cumlative SongStats
    public Map<String, SongStats> aggregateBySong(List<RatingRow> rows) {
        Map<String, SongStats> songMap = new TreeMap<>();

        for (RatingRow row : rows) {
            String song = row.song();
            SongStats stats = songMap.get(song);

            if (stats == null) {
                stats = new SongStats();
                songMap.put(row.song(), stats);
            }
            stats.add(row.rating());
        }
        return songMap;
    }
}
