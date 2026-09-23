import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Layer: domain logic / similarity / PA3
 * Role: Compute Euclidean distances between song pairs over normalized Profiles.
 * Depends on: Profiles, UserProfile, SongId, SongIdPair, SongIdPairsGenerator
 * Used by: SongSimilarityRunnerPA3
 */
public final class SongSimilarityComputer {


    //Main method: Builds a map for each songPair to it's distance from all users
    public Map<SongPair, Double> similarities(Profiles normalized) {
        TreeSet<Song> songs = new TreeSet<>();

        for (UserProfile profile : normalized.asMap().values()) {
            songs.addAll(profile.asMap().keySet());
        }

        List<SongPair> pairs = generateAllPairs(songs);

        Map<SongPair, Double> output = new TreeMap<>();
        for (SongPair pair : pairs) {
            double distance = computeDistance(normalized, pair.song1(), pair.song2());
            output.put(pair, distance);
        }
        return output;
    }

    //Generates all the unique songPairs from the set of songs
    private static List<SongPair> generateAllPairs(Set<Song> songs) {
        if (songs.size() < 2) {
            return List.of();
        }

        List<Song> sortedSongs = new ArrayList<>(songs);
        List<SongPair> pairs = new ArrayList<>();

        for (int i = 0; i < sortedSongs.size(); i++) {
            for (int j = i + 1; j < sortedSongs.size(); j++) {
                pairs.add(new SongPair(sortedSongs.get(i), sortedSongs.get(j)));
            }
        }
        return pairs;
    }

    //Finds Euclidean distance between two songs across users, while skipping NaNs
    private static double computeDistance(Profiles normalized, Song song1, Song song2) {
        double sumSquared = 0.0;
        int userOverlap = 0;

        for (UserProfile profile : normalized.asMap().values()) {
            Map<Song, Double> row = profile.asMap();

            double rating1 = row.get(song1);
            double rating2 = row.get(song2);
            if (Double.isNaN(rating1) || Double.isNaN(rating2)) {
                continue;
            }

            double diff = rating1 - rating2;
            sumSquared += diff * diff;
            userOverlap++;
        }

        if (userOverlap == 0) {
            return Double.NaN;
        } else {
            return Math.sqrt(sumSquared);
        }
    }
}
