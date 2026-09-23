import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//Kmean over songs: Clusters songs around a user selected song
public final class KMeansClustering {
    private static final int iterations = 10;

    // PA5: run K-means and return recommendations for the selected songs
    public List<SongRecommendation> recommend(SongRatingNormalizer.SongNormalizationResult normalization,
            List<String> selections) {

        List<Song> songs = normalization.songs();
        double[][] normalizedRatings = normalization.normalizedRatings();

        if (songs.isEmpty()) {
            throw Errors.noRecommendations();
        }

        Map<String, Integer> indexByName = buildIndexByName(songs);
        List<Cluster> clusters = buildSeedClusters(normalization, indexByName, selections);

        runKMeans(clusters, normalizedRatings);

        Set<String> selectionNames = new HashSet<>(selections);
        List<SongRecommendation> recommendations = collectRecommendations(clusters, songs, selectionNames);

        if (recommendations.isEmpty()) {
            throw Errors.noRecommendations();
        }

        // Sorts recommendations by starting song then recommended song
        Collections.sort(recommendations, (left, right) -> {
            int compare = left.startingSong().compareTo(right.startingSong());
            if (compare != 0) {
                return compare;
            }
            return left.recommendedSong().compareTo(right.recommendedSong());
        });

        return recommendations;
    }

    // PA6: build a playlist using the first K liked songs
    public List<Song> buildPlaylist(SongRatingNormalizer.SongNormalizationResult normalization,
            List<String> likedSongs, int k) {

        List<Song> songs = normalization.songs();
        double[][] normalizedRatings = normalization.normalizedRatings();

        if (songs.size() < k + 1) {
            throw Errors.noRecommendations();
        }

        Map<String, Integer> indexByName = buildIndexByName(songs);
        List<String> seeds = likedSongs.subList(0, k);
        List<Cluster> clusters = buildSeedClusters(normalization, indexByName, seeds);

        runKMeans(clusters, normalizedRatings);

        Map<Integer, Integer> clusterIndexBySong = mapSongsToClusters(clusters);
        Set<String> likedSet = new HashSet<>(likedSongs);

        PlaylistContext context = new PlaylistContext(songs, normalizedRatings, indexByName, clusterIndexBySong,
                likedSet, clusters);

        Map<Integer, Double> bestDistanceByIndex = scoreCandidates(likedSongs, context);

        if (bestDistanceByIndex.isEmpty()) {
            throw Errors.noRecommendations();
        }

        return selectTopSongs(songs, bestDistanceByIndex);
    }

    private static Map<String, Integer> buildIndexByName(List<Song> songs) {
        Map<String, Integer> indexByName = new HashMap<>();
        for (int i = 0; i < songs.size(); i++) {
            indexByName.put(songs.get(i).value(), i);
        }
        return indexByName;
    }

    // Builds initial clusters given seed song names
    private static List<Cluster> buildSeedClusters(SongRatingNormalizer.SongNormalizationResult normalization,
            Map<String, Integer> indexByName, List<String> seedNames) {

        double[][] normalizedRatings = normalization.normalizedRatings();

        int userCount = normalizedRatings[0].length;
        List<Cluster> clusters = new ArrayList<>();

        for (String seed : seedNames) {
            Integer index = indexByName.get(seed);
            if (index == null) {
                throw Errors.selectionNotFound(seed);
            }

            double[] centroid = Arrays.copyOf(normalizedRatings[index], userCount);
            clusters.add(new Cluster(centroid, seed));
        }

        return clusters;
    }

    // K-means loop: assign songs to clusters then recompute centroids
    private static void runKMeans(List<Cluster> clusters, double[][] normalizedRatings) {
        int songCount = normalizedRatings.length;
        int userCount = normalizedRatings[0].length;

        for (int iteration = 0; iteration < iterations; iteration++) {
            // clear previous assignments
            for (Cluster cluster : clusters) {
                cluster.clearAssignments();
            }

            // assign each song to the closest cluster centroid
            for (int songIndex = 0; songIndex < songCount; songIndex++) {
                double[] songVector = normalizedRatings[songIndex];
                int targetCluster = findClosestCluster(clusters, songVector);
                clusters.get(targetCluster).assign(songIndex);
            }

            // recompute centroids as the mean of all assigned songs
            for (Cluster cluster : clusters) {
                cluster.recomputeCentroid(normalizedRatings, userCount);
            }
        }
    }

    // Finds which cluster centroid is closest to the song
    private static int findClosestCluster(List<Cluster> clusters, double[] songVector) {
        boolean foundAny = false;
        double bestDistance = 0.0;
        int bestIndex = 0;

        for (int i = 0; i < clusters.size(); i++) {
            double distance = clusters.get(i).distanceTo(songVector);

            if (!foundAny || distance < bestDistance) {
                foundAny = true;
                bestDistance = distance;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    // For each cluster, recommend all songs assigned to the cluster except the orignal
    private static List<SongRecommendation> collectRecommendations(List<Cluster> clusters, List<Song> songs,
            Set<String> selectionNames) {

        List<SongRecommendation> recommendations = new ArrayList<>();

        for (Cluster cluster : clusters) {
            if (cluster.assignedSongs.isEmpty()) {
                continue;
            }

            String seedName = cluster.seedName;

            for (int songIndex : cluster.assignedSongs) {
                Song song = songs.get(songIndex);
                String name = song.value();

                if (selectionNames.contains(name)) {
                    continue;
                }
                recommendations.add(new SongRecommendation(seedName, name));
            }
        }
        return recommendations;
    }

    private static Map<Integer, Integer> mapSongsToClusters(List<Cluster> clusters) {
        Map<Integer, Integer> clusterIndexBySong = new HashMap<>();
        for (int c = 0; c < clusters.size(); c++) {
            Cluster cluster = clusters.get(c);
            for (int songIndex : cluster.assignedSongs) {
                clusterIndexBySong.put(songIndex, c);
            }
        }
        return clusterIndexBySong;
    }

    private static Map<Integer, Double> scoreCandidates(List<String> likedSongs, PlaylistContext context) {
        Map<Integer, Double> bestDistanceByIndex = new HashMap<>();
        for (String liked : likedSongs) {
            handleLikedSong(liked, context, bestDistanceByIndex);
        }
        return bestDistanceByIndex;
    }

    private static void handleLikedSong(String liked, PlaylistContext context,
            Map<Integer, Double> bestDistanceByIndex) {

        Integer sIndex = context.indexByName().get(liked);
        if (sIndex == null) {
            throw Errors.selectionNotFound(liked);
        }

        Integer clusterIndex = context.clusterIndexBySong().get(sIndex);
        if (clusterIndex == null) {
            return;
        }

        Cluster cluster = context.clusters().get(clusterIndex);
        double[] sVector = context.normalizedRatings()[sIndex];

        for (int tIndex : cluster.assignedSongs) {
            if (tIndex == sIndex) {
                continue;
            }

            String candidateName = context.songs().get(tIndex).value();
            if (context.likedSet().contains(candidateName)) {
                continue;
            }

            double dist = euclideanDistance(context.normalizedRatings()[tIndex], sVector);
            if (Double.isNaN(dist)) {
                continue;
            }

            // smaller distance is better: keep the minimum for each candidate
            bestDistanceByIndex.merge(tIndex, dist, Math::min);
        }
    }

    private static List<Song> selectTopSongs(List<Song> songs, Map<Integer, Double> bestDistanceByIndex) {
        List<Map.Entry<Integer, Double>> candidates = new ArrayList<>(bestDistanceByIndex.entrySet());

        candidates.sort((a, b) -> {
            // smaller distance first
            int cmp = Double.compare(a.getValue(), b.getValue());
            if (cmp != 0) {
                return cmp;
            }
            String nameA = songs.get(a.getKey()).value();
            String nameB = songs.get(b.getKey()).value();
            return nameA.compareTo(nameB);
        });

        List<Song> playlist = new ArrayList<>();
        for (int i = 0; i < candidates.size() && i < 20; i++) {
            int idx = candidates.get(i).getKey();
            playlist.add(songs.get(idx));
        }

        return playlist;
    }

    private static double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    private static record PlaylistContext(List<Song> songs, double[][] normalizedRatings,
            Map<String, Integer> indexByName, Map<Integer, Integer> clusterIndexBySong, Set<String> likedSet,
            List<Cluster> clusters) {
    }

    // Cluster: centroid plus assigned song indices
    private static final class Cluster {
        private double[] centroid;
        private final List<Integer> assignedSongs = new ArrayList<>();
        private final String seedName;

        Cluster(double[] centroid, String seedName) {
            this.centroid = centroid;
            this.seedName = seedName;
        }

        void clearAssignments() {
            assignedSongs.clear();
        }

        void assign(int songIndex) {
            assignedSongs.add(songIndex);
        }

        void recomputeCentroid(double[][] normalizedRatings, int userCount) {
            if (assignedSongs.isEmpty()) {
                return;
            }

            double[] sums = new double[userCount];

            for (int songIndex : assignedSongs) {
                double[] vector = normalizedRatings[songIndex];
                for (int i = 0; i < userCount; i++) {
                    sums[i] += vector[i];
                }
            }

            for (int i = 0; i < userCount; i++) {
                sums[i] = sums[i] / assignedSongs.size();
            }
            centroid = sums;
        }

        double distanceTo(double[] vector) {
            double sum = 0.0;

            for (int i = 0; i < centroid.length; i++) {
                double diff = centroid[i] - vector[i];
                sum += diff * diff;
            }
            return Math.sqrt(sum);
        }
    }
}