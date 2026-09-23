import java.util.Map;
import java.util.Set;

/*
 * Layer: domain model / data structure
 * Role: Immutable wrapper around Map<UserId, UserProfile> with convenient accessors.
 * Depends on: Username, UserProfile
 * Used by: normalization, similarity, prediction, CsvUserRatingsWriter
 */

 //Wrapper for all nested map of Users -> Songs -> Ratings
public record Profiles(Map<Username, UserProfile> byUser) {

    //Copy of map to keep Profiles Immutable
    public Profiles {
        byUser = Map.copyOf(byUser);
    }

    //All usernames in the map
    public Set<Username> users() {
        return byUser.keySet();
    }

    //Look up a single user's profile
    public UserProfile get(Username user) {
        return byUser.get(user);
    }

    //The map under the wrapper
    public Map<Username, UserProfile> asMap() {
        return byUser;
    }

}
