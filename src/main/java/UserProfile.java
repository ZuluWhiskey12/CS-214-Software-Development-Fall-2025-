import java.util.Map;
import java.util.Set;

/*
 * Layer: domain model / profile
 * Role: Immutable per user rating map from SongId to Double with convenent accessors.
 * Depends on: Song
 * Used by: Profiles, ScoreNormalizer
 */

//Wrapper for map of one user's song ratings
public record UserProfile(Map<Song, Double> ratings) {

    //Copy of the user's rating map to keep it immutable
    public UserProfile {
        ratings = Map.copyOf(ratings);
    }

    //Get this user's rating for a given song
    public Double rating(Song song) {
        return ratings.get(song);
    }

    //All songs map contains
    public Set<Song> songs() {
        return ratings.keySet();
    }

    //All ratings map contains
    public Map<Song, Double> asMap() {
        return ratings;
    }

}
