import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


//Drops songs that have only one distinct rating acoss all users
public final class OneRatingSongFilter {

    //Main method: Returns the profiles filtered + a set of dropped songs
    public FilterResults filter(Profiles profiles) {
        Set<Song> allSongs = collectSongs(profiles);
        Set<Song> droppedSongs = findSongsWithOneRating(profiles, allSongs);

        if (droppedSongs.isEmpty()) {
            return new FilterResults(profiles, Set.of());
        }

        Profiles filteredProfiles = removeSongs(profiles, droppedSongs);
        return new FilterResults(filteredProfiles, Set.copyOf(droppedSongs));
    }

    //Collects all the songs in all the profiles
    private Set<Song> collectSongs(Profiles profiles) {
        Set<Song> songs = new TreeSet<>();

        for (UserProfile profile : profiles.asMap().values()) {
            songs.addAll(profile.asMap().keySet());
        }
        return songs;
    }

    //Finds songs with only one unique ratings
    private Set<Song> findSongsWithOneRating(Profiles profiles, Set<Song> allSongs) {
        Set<Song> droppedSongs = new TreeSet<>();

        for (Song song : allSongs) {
            Set<Double> distanctRatings = new TreeSet<>();

            for (UserProfile profile : profiles.asMap().values()) {
                Double rating = profile.asMap().get(song);

                if (rating != null) {
                    distanctRatings.add(rating);
                }

                //If a song has more then one rating its fine
                if (distanctRatings.size() > 1) {
                    break;
                }
            }
            //Drops songs with 1 or less unique ratings
            if (distanctRatings.size() <= 1) {
                droppedSongs.add(song);
            }
        }
        return droppedSongs;
    }

    //Removes the dropped songs from all user profiles
    private Profiles removeSongs(Profiles profiles, Set<Song> droppedSongs) {
        Map<Username, UserProfile> filtered = new TreeMap<>();
        
        for (Map.Entry<Username, UserProfile> entry : profiles.asMap().entrySet()) {
            Map<Song, Double> row = new TreeMap<>(entry.getValue().asMap());

            for (Song song : droppedSongs) {
                row.remove(song);
            }
            filtered.put(entry.getKey(), new UserProfile(row));
        }
        return new Profiles(filtered);
    }

    //Bundles the filtered profiles + teh set of dropped songs
    public record FilterResults(Profiles profiles, Set<Song> droppedSongs) {
    }
}
