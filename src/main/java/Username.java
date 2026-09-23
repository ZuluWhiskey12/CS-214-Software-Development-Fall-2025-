/*
 * Layer: domain model / identifier
 * Role: User identifier wrapping a String; Comparable by value.
 * Used by: UserIdPair, Profiles, UserProfileBuilder, DistanceMatrix, similarity and prediction logic
 */

 //Wrapper of String for Usernames with sorting by name
public record Username(String value) implements Comparable<Username> {

    //Compare usernames by string value
    @Override
    public int compareTo(Username other) {
        return value.compareTo(other.value);
    }

    //Print out username
    @Override
    public String toString() {
        return value;
    }
}
