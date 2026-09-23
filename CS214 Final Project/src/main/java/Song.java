/*
 * Layer: domain model / identifier
 * Role: Song identifier wrapping a String; Comparable by value.
 * Used by: SongIdPair, SongIdPairsGenerator, UserProfile, Profiles, similarity and prediction logic
 */

 //Wrapper of a string to represent song names and sorting by name
public record Song(String value) implements Comparable<Song> {

    //Conmpare songs by string value
    @Override
    public int compareTo(Song other) {
        return value.compareTo(other.value);
    }

    //Print the songs name
    @Override
    public String toString() {
        return value;
    }
}
