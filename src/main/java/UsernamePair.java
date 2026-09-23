    /*
     * Layer: domain model / identifier pair
     * Role: Ordered pair of UserId values; Comparable by (user1, user2).
     * Depends on: Username
     * Used by: DistanceMatrix, UserIdPairsGenerator, UserSimilarityComputer
     */
public record UsernamePair(Username user1, Username user2) implements Comparable<UsernamePair> {

     //Sorts pairs by users
    @Override
    public int compareTo(UsernamePair other) {
        int compare = this.user1().compareTo(other.user1());

        if (compare != 0) {
            return compare;
        }
        return this.user2().compareTo(other.user2());
    }
}
