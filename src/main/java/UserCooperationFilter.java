import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Layer: domain logic / preprocessing
 * Role: Remove users with only one distinct rating value and drop songs rated only by those users.
 * Depends on: RatingRow
 * Used by: UserAnalysisRunnerPA2, SongSimilarityRunnerPA3, UserPredictionsRunnerPA4
 */
public final class UserCooperationFilter {

    //Main method: drops uncooperative uses and songs only rated by them
    public List<RatingRow> filter(List<RatingRow> ratingRows) {
        Map<String, UserStats> userStats = new HashMap<>();
        Map<String, SongStats> songStats = new HashMap<>();
        buildStats(ratingRows, userStats, songStats);

        Set<String> droppedUsers = findDroppedUsers(userStats);
        Set<String> droppedSongs = findDroppedSongs(songStats, droppedUsers);

        return keepRows(ratingRows, droppedUsers, droppedSongs);
    }

    //Collect per user and per song stats from raw rows
    private static void buildStats(List<RatingRow> ratingRows, Map<String, UserStats> userStats,
            Map<String, SongStats> songStats) {
        for (RatingRow row : ratingRows) {
            String user = row.user();
            String song = row.song();
            int rating = row.rating();

            UserStats stats = userStats.get(user);
            if (stats == null) {
                stats = new UserStats();
                userStats.put(user, stats);
            }
            stats.addRating(rating, song);

            SongStats sStats = songStats.get(song);
            if (sStats == null) {
                sStats = new SongStats();
                songStats.put(song, sStats);
            }
            sStats.addRater(user);
        }
    }

    //Determine which users are uncooperative, have <= 1 unique rating
    private static Set<String> findDroppedUsers(Map<String, UserStats> userStats) {
        Set<String> droppedUsers = new HashSet<>();

        for (Map.Entry<String, UserStats> entry : userStats.entrySet()) {
            if (entry.getValue().isUncooperative()) {
                droppedUsers.add(entry.getKey());
            }
        }
        return droppedUsers;
    }

    //Find songs only rated by dropped users
    private static Set<String> findDroppedSongs(Map<String, SongStats> songStats, Set<String> droppedUsers) {
        Set<String> droppedSongs = new HashSet<>();

        for (Map.Entry<String, SongStats> entry : songStats.entrySet()) {
            if (entry.getValue().onlyDroppedRaters(droppedUsers)) {
                droppedSongs.add(entry.getKey());
            }
        }
        return droppedSongs;
    }

    //Keep only rows with cooperative users and not dropped songs
    private static List<RatingRow> keepRows(List<RatingRow> ratingRows, Set<String> droppedUsers,
            Set<String> droppedSongs) {
        List<RatingRow> keptRows = new ArrayList<>();

        for (RatingRow row : ratingRows) {
            boolean userKept = !droppedUsers.contains(row.user());
            boolean songKept = !droppedSongs.contains(row.song());

            if (userKept && songKept) {
                keptRows.add(row);
            }
        }
        return keptRows;
    }

    //Tracks ratings values and songs for a single user
    private static final class UserStats {
        private final Set<Integer> ratingValues = new HashSet<>();
        private final Set<String> ratedSongs = new HashSet<>();

        //Record a rating and the song it applies to 
        void addRating(int rating, String song) {
            ratingValues.add(rating);
            ratedSongs.add(song);
        }

        //User is uncooperative is only 1 or less rating value
        boolean isUncooperative() {
            return ratingValues.size() <= 1;
        }
    }

    //Tracks which users have rated a given song
    private static final class SongStats {
        private final Set<String> raters = new HashSet<>();

        //Records song was rated by user
        void addRater(String user) {
            raters.add(user);
        }

        //Song is dropped if all ratings are from droppedUsers
        boolean onlyDroppedRaters(Set<String> droppedUsers) {
            return !raters.isEmpty() && droppedUsers.containsAll(raters);
        }
    }
}
