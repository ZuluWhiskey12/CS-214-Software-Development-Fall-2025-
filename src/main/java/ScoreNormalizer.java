import java.util.Map;
import java.util.TreeMap;

/*
 * Layer: domain logic / normalization
 * Role: Zscore normalize each user's ratings; produces normalized Profiles plus per user mean and std dev.
 * Depends on: Profiles, UserProfile, Username, Song
 * Used by: SongSimilarityRunnerPA3, UserPredictionsRunnerPA4
 */
public final class ScoreNormalizer {

    //Main method: compute the per user mean and std Dev and return normalized Profiles plus stats
    public UserNormalizationResult normalize(Profiles raw) {
        Map<Username, Double> meanByUser = new TreeMap<>();
        Map<Username, Double> stdDevByUser = new TreeMap<>();
        Map<Username, UserProfile> normalizedByUser = new TreeMap<>();

        for (Username username : raw.users()) {
            UserProfile profile = raw.get(username);
            UserStats stats = computeStats(profile);

            meanByUser.put(username, stats.mean());
            stdDevByUser.put(username, stats.stdDev());
            normalizedByUser.put(username, normalizeProfile(profile, stats.mean(), stats.stdDev()));
        }

        Profiles normalizedProfiles = new Profiles(normalizedByUser);
        return new UserNormalizationResult(normalizedProfiles, meanByUser, stdDevByUser);
    }

    //Creates new UserProfile with Scored rating for one user
    private static UserProfile normalizeProfile(UserProfile profile, double mean, double stdDev) {
        Map<Song, Double> normalizedRow = new TreeMap<>();

        for (Song song : profile.songs()) {
            double rating = profile.rating(song);
            double normalizedRating = normalizeRating(rating, mean, stdDev);

            normalizedRow.put(song, normalizedRating);
        }
        return new UserProfile(normalizedRow);
    }

    //Normalizes one rating, NaNs remain and szero if no std Dev
    private static double normalizeRating(double rating, double mean, double stdDev) {
        if (Double.isNaN(rating)) {
            return Double.NaN;
        }
        if (stdDev == 0.0) {
            return 0.0;
        }
        return (rating - mean) / stdDev;
    }

    //Compute mean and std Dev for one user's profile while ignoring NaNs
    private static UserStats computeStats(UserProfile profile) {
        double mean = 0.0;
        double m2 = 0.0;
        int count = 0;

        for (double rating : profile.asMap().values()) {
            if (Double.isNaN(rating)) {
                continue;
            }
            count++;

            double delta = rating - mean;
            mean += delta / count;

            double delta2 = rating - mean;
            m2 += delta * delta2;
        }

        double difference;
        if (count == 0) {
            difference = 0.0;
        } else {
            difference = m2 / count;
        }

        double stdDev = Math.sqrt(difference);
        return new UserStats(mean, stdDev);
    }

    //Holds one user's meand and std Dev
    private static record UserStats(double mean, double stdDev) {
    }

    //Normalized Profiles + user's mean and Std Dev
    public record UserNormalizationResult(Profiles normalized, Map<Username, Double> means,
            Map<Username, Double> stdDevs) {
    }
}
