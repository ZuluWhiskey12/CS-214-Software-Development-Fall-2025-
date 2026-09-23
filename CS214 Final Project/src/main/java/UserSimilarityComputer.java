import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Layer: domain logic / similarity / PA4
 * Role: Compute Euclidean distances between users over normalized ratings and wrap them in a DistanceMatrix.
 * Depends on: Profiles, UserProfile, UserId, UserIdPair, UserIdPairsGenerator, SongId, DistanceMatrix
 * Used by: UserPredictionsRunnerPA4
 */
public final class UserSimilarityComputer {

    //Main method: Builds a DistanceMatrix of user to user distances from the normalized profiles
    public DistanceMatrix distances(Profiles normalized) {
        Set<Username> users = new TreeSet<>(normalized.users());
        List<UsernamePair> pairs = generateAllPairs(users);
        Map<UsernamePair, Double> byPair = new TreeMap<>();

        for (UsernamePair pair : pairs) {
            UserProfile profile1 = normalized.get(pair.user1());
            UserProfile profile2 = normalized.get(pair.user2());

            double distance = computeDistance(profile1, profile2);
            byPair.put(pair, distance);
        }

        return new DistanceMatrix(byPair);
    }

    //Generates all unique user pairs from set of users
    private static List<UsernamePair> generateAllPairs(Set<Username> users) {
        if (users.size() < 2) {
            return List.of();
        }

        List<Username> sortedUsers = new ArrayList<>(users);
        List<UsernamePair> output = new ArrayList<>();

        for (int i = 0; i < sortedUsers.size(); i++) {
            for (int j = i + 1; j < sortedUsers.size(); j++) {
                output.add(new UsernamePair(sortedUsers.get(i), sortedUsers.get(j)));
            }
        }
        return output;
    }

    //Calculates Euclidean distance between two user over songs, skipping NaN
    private static double computeDistance(UserProfile pair1, UserProfile pair2) {
        double sumSquared = 0.0;
        int songOverlap = 0;

        Map<Song, Double> row1 = pair1.asMap();
        Map<Song, Double> row2 = pair2.asMap();

        for (Song song : row1.keySet()) {
            double rating1 = row1.get(song);
            double rating2 = row2.get(song);

            if (Double.isNaN(rating1) || Double.isNaN(rating2)) {
                continue;
            }

            double diff = rating1 - rating2;
            sumSquared += diff * diff;
            songOverlap++;
        }

        if (songOverlap == 0) {
            return Double.NaN;
        } else {
            return Math.sqrt(sumSquared);
        }
    }
}
