/*
 * Layer: domain model / statistics / PA1
 * Role: accumulator using Welford's algorithm (count, mean, population standard deviation).
 * Used by: SongStatsAggregator, CsvSongDataWriter
 */
public final class SongStats {
    private int count = 0; // number of ratings
    private double mean = 0; // running mean
    private double m2; // running sum of squared differences

    //Add one new rating and update the running stats
    public void add(int rating) {
        count++;

        double delta = rating - mean;
        mean += delta / count;

        double delta2 = rating - mean;
        m2 += delta * delta2;
    }

    //Total of number of ratings so far
    public int getCount() {
        return count;
    }

    //Average of seen ratings
    public double getMean() {
        return mean;
    }

    //population standard deviation of ratings
    public double getPopulationStdDev() {
        if (count == 0) {
            return 0.0;
        }
        return Math.sqrt(m2 / count);
    }
}
