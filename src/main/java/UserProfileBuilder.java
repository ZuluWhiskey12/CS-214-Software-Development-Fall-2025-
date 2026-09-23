import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Layer: domain logic / profile construction
 * Role: Build a full user x song rating grid from RatingRow list, filling missing ratings with Double.NaN.
 * Depends on: RatingRow, Song, Username, UserProfile, Profiles
 * Used by: UserAnalysisRunnerPA2, SongSimilarityRunnerPA3, UserPredictionsRunnerPA4
 */
public final class UserProfileBuilder {

    //Main method: converst list of RatingRows into a Profiles grid with NaN for missing ratings
    public Profiles buildProfiles(List<RatingRow> ratingRows) {
        ProfilesAccumulator accumulator = new ProfilesAccumulator();

        for (RatingRow row : ratingRows) {
            accumulator.add(row);
        }
        return accumulator.build();
    }

    //Helper that builds the user by song rating grid
    private static final class ProfilesAccumulator {
        private final Map<Username, Map<Song, Double>> ratingsByUser = new TreeMap<>();
        private final TreeSet<Song> songs = new TreeSet<>();

        //Adds a single rating to the grid and creates user and song entries as needed
        void add(RatingRow row) {
            Song song = new Song(row.song());
            Username user = new Username(row.user());

            ensureSong(song);
            Map<Song, Double> userRow = ensureUser(user);
            userRow.put(song, (double) row.rating());
        }

        //Adds new song to all users with a NaN rating
        private void ensureSong(Song song) {
            if (songs.add(song)) {
                
                for (Map<Song, Double> row : ratingsByUser.values()) {
                    row.put(song, Double.NaN);
                }
            }
        }

        //Adds new user and gives NaN rating for all known songs
        private Map<Song, Double> ensureUser(Username user) {
            Map<Song, Double> userRow = ratingsByUser.get(user);

            if (userRow == null) {
                userRow = new TreeMap<>();
                for (Song song : songs) {
                    userRow.put(song, Double.NaN);
                }
                ratingsByUser.put(user, userRow);
            }
            return userRow;
        }

        //Wraps the completed grid into an immutable Profile, UserProfile object
        Profiles build() {
            Map<Username, UserProfile> byUser = new TreeMap<>();

            for (Map.Entry<Username, Map<Song, Double>> entry : ratingsByUser.entrySet()) {
                byUser.put(entry.getKey(), new UserProfile(entry.getValue()));
            }
            return new Profiles(byUser);
        }
    }
}
