import java.util.Comparator;

/*
 * Layer: helper / comparison
 * Role: Comparator that orders neighbors for a base user by distance, breaking ties by username.
 * Depends on: UserId, DistanceMatrix
 * Used by: UserPredictionComputer (buildSortedNeighbors)
 */

//Comparator for sorting neighbors from a base user to increasing distance of neighbors
public final class NeighborComparison implements Comparator<Username> {
    private final Username base;
    private final DistanceMatrix distances;

    //Set base user for distance comparisons
    NeighborComparison(Username base, DistanceMatrix distances) {
        this.base = base;
        this.distances = distances;
    }

    //Orders the neigbhors by their distance from the user, then by username if their is a tie
    @Override
    public int compare(Username userA, Username userB) {
        double distanceA = distances.get(base, userA);
        double distanceB = distances.get(base, userB);

        int compare = Double.compare(distanceA, distanceB);
        if (compare != 0) {
            return compare;
        }
        return userA.value().compareTo(userB.value());
    }

}
