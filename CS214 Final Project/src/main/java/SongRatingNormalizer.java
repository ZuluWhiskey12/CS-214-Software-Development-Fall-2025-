import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

    //Per song Score normalization output as a song by user matrix

public final class SongRatingNormalizer {

    //Main methodL Normalize all the ratings per song and return songs, users, and a normlazed matrix
    public SongNormalizationResult normalize(Profiles profiles) {
        List<Username> users = new ArrayList<>(new TreeSet<>(profiles.users()));
        List<Song> songs = collectSongs(profiles);

        //Compute the mean and std dev per song
        Map<Song, RunningStats> statsBySong = buildStats(profiles, users, songs);
        double[][] normalizedRatings = new double[songs.size()][users.size()];

        for (int songIndex = 0; songIndex < songs.size(); songIndex++) {
            Song song = songs.get(songIndex);
            RunningStats stats = statsBySong.get(song);
            double mean = stats.mean();
            double stdDev = stats.stdDev();

            for (int userIndex = 0; userIndex < users.size(); userIndex++) {
                Username user = users.get(userIndex);
                UserProfile profile = profiles.get(user);

                double value = profile.rating(song);

                double normalized = (value - mean) / stdDev;
                normalizedRatings[songIndex][userIndex] = normalized;
            }
        }
        return new SongNormalizationResult(songs, users, normalizedRatings);
    }

    //Collects all songs that apperat in any user profile
    private List<Song> collectSongs(Profiles profiles) {
        TreeSet<Song> songs = new TreeSet<>();

        for (Username user : profiles.users()) {
            UserProfile profile = profiles.get(user);
            songs.addAll(profile.asMap().keySet());
        }
        return new ArrayList<>(songs);
    }

    //Builds the running stats (mean & std dev) for each song across all the users
    private Map<Song, RunningStats> buildStats(Profiles profiles, List<Username> users,
            List<Song> songs) {
        Map<Song, RunningStats> stats = new HashMap<>();
        
        for (Song song : songs) {
            stats.put(song, new RunningStats());
        }

        for (Username user : users) {
            Map<Song, Double> row = profiles.get(user).asMap();

            for (Song song : songs) {
                Double rating = row.get(song);

                RunningStats running = stats.get(song);
                running.add(rating);
            }
        }
        return stats;
    }

    //An ordered list of songs, ordered list of users and the normalized ratings for using in clustering.
    public record SongNormalizationResult(List<Song> songs, List<Username> users, double[][] normalizedRatings) {
    }

    //Computes the running stats again like songStats 
    private static final class RunningStats {
        private double mean = 0.0;
        private double m2 = 0.0;
        private int count = 0;

        void add(double value) {
            count++;

            double delta = value - mean;
            mean += delta / count;

            double delta2 = value - mean;
            m2 += delta * delta2;
        }

        double mean() {
            if (count == 0) {
                return 0.0;
            }
            return mean;
        }

        double stdDev() {
            if (count == 0) {
                return 0.0;
            }
            double difference = m2 / count;
            return Math.sqrt(difference);
        }
    }
}
