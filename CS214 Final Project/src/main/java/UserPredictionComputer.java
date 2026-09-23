import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/*
 * Layer: domain logic / predictions / PA4
 * Role: Predict missing ratings by borrowing normalized scores from nearest neighbors and denormalizing.
 * Depends on: Profiles, UserProfile, Song, User, DistanceMatrix, NeighborComparison, PredictionRow
 * Used by: UserPredictionsRunnerPA4
 */
public final class UserPredictionComputer {

    //Main method: Generates PredictRows list for all users with missing ratings.
    public List<PredictionRow> predict(Profiles rawProfiles, ScoreNormalizer.UserNormalizationResult normalize,
            DistanceMatrix distances) {

        Profiles normalized = normalize.normalized();
        Map<Username, Double> means = normalize.means();
        Map<Username, Double> stdDevs = normalize.stdDevs();

        //Works with usres in sorted order for simplier output
        List<Username> users = new ArrayList<>(new TreeSet<>(normalized.users()));
        List<PredictionRow> rows = new ArrayList<>();
        PredictionScaler scaler = new PredictionScaler();

        for (Username user : users) {
            UserProfile rawRow = rawProfiles.get(user);
            if (rawRow == null) {
                continue;
            }

            //Find songs where the user is missing a rating 
            List<Song> missingSongs = collectMissingRatings(rawRow);
            if (missingSongs.isEmpty()) {
                continue;
            }

            //Get the user's mean & Std dev and a list of neighbors
            UserStats stats = buildUserStats(user, means, stdDevs);
            List<Username> neighbors = buildSortedNeighbors(user, users, distances);

            //Predicrt each missing rating the closet neighbor with a normalized score
            for (Song song : missingSongs) {
                NeighborPrediction neighborPrediction = firstNeighborPrediction(neighbors, normalized, song);
                double prediction = scaler.denormalize(neighborPrediction, stats);
                rows.add(new PredictionRow(song.value(), user.value(), prediction));
            }
        }
        //Sort predictions by son, then user
        Collections.sort(rows);
        return rows;
    }

    //Builds neighbor list for one user sorted by increasing distance
    private static List<Username> buildSortedNeighbors(Username base, List<Username> allUsers,
            DistanceMatrix distances) {

        List<Username> neighbors = new ArrayList<>();

        for (Username user : allUsers) {
            if (user.equals(base)) {
                continue;
            }

            double distance = distances.get(base, user);
            if (Double.isNaN(distance)) {
                continue;
            }
            neighbors.add(user);
        }
        neighbors.sort(new NeighborComparison(base, distances));
        return neighbors;
    }

    //Collect all the songs where the user has a rating 
    private static List<Song> collectMissingRatings(UserProfile rawRow) {
        Map<Song, Double> rawMap = rawRow.asMap();
        List<Song> missing = new ArrayList<>();

        List<Song> orderedSongs = new ArrayList<>(new TreeSet<>(rawMap.keySet()));
        for (Song song : orderedSongs) {
            Double value = rawMap.get(song);
            if (value != null && value.isNaN()) {
                missing.add(song);
            }
        }
        return missing;
    }

    //Find the closet or first neighbor what has a normalized rating for the song
    private static NeighborPrediction firstNeighborPrediction(List<Username> neighbors, Profiles normalized,
            Song song) {

        for (Username neighbor : neighbors) {
            UserProfile row = normalized.get(neighbor);
            if (row == null) {
                continue;
            }

            Double zScore = row.asMap().get(song);
            if (zScore != null && !Double.isNaN(zScore)) {
                return new NeighborPrediction(neighbor, zScore);
            }
        }
        //No neighbor found
        return null;
    }

    //Look up the user's mean & std dev from normalization results
    private static UserStats buildUserStats(Username userId, Map<Username, Double> means,
            Map<Username, Double> stdDevs) {

        double mean = means.getOrDefault(userId, 0.0);
        double std = stdDevs.getOrDefault(userId, 0.0);
        return new UserStats(mean, std);
    }

    //Hold the neighbor that was used and there normalized score
    private static record NeighborPrediction(Username neighbor, double zScore) {
    }

    //Hold the user's mean and Std dev
    private static record UserStats(double mean, double stdDev) {
    }

    //Turns the neighbors normalized score back into a 1 through  rating for the user
    private static final class PredictionScaler {

        //Denormalizes neighbors normalized score using the user's mean & std dev and puts it between 1 to 5
        double denormalize(NeighborPrediction neighborPrediction, UserStats stats) {
            if (neighborPrediction == null || Double.isNaN(neighborPrediction.zScore())) {
                return Double.NaN;
            }
            double zScore = neighborPrediction.zScore();

            double denormalized = stats.mean();
            if (stats.stdDev() != 0.0) {
                denormalized = zScore * stats.stdDev() + stats.mean();
            }
            return clamp(Math.round(denormalized));
        }

        //Ensures rating stays between 1 to 5 range
        private double clamp(double roundedValue) {
            int rounded = (int) roundedValue;
            if (rounded < 1) {
                rounded = 1;
            }
            
            if (rounded > 5) {
                rounded = 5;
            }
            return (double) rounded;
        }
    }
}
