import java.util.Map;

/*
 * Layer: domain / data structure
 * Role: lookup table for user to user distances keyed by UsernamePair, with symmetric get(UserId, UserId).
 * Depends on: UsernamePair, Username
 * Used by: UserPredictionComputer, NeighborComparison, UserPredictionsRunnerPA4
 */

//Stores distance between pairs of users and provides a symmetric lookup
public final class DistanceMatrix {
    private final Map<UsernamePair, Double> byPair;

    //Wrap an existing UserNamePair to a distance map
    public DistanceMatrix(Map<UsernamePair, Double> byPair) {
        this.byPair = byPair;
    }

    //Look up the distance between two users, to order does not matter. Returns NaN if no distance is stored
    public double get(Username user1, Username user2) {
        Username first;
        Username second;

        if (user1.compareTo(user2) <= 0) {
            first = user1;
            second = user2;
        } else {
            first = user2;
            second = user1;
        }

        return byPair.getOrDefault(new UsernamePair(first, second), Double.NaN);
    }

}
