import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class KMeansClusteringTest {

    @Test
    void recommendsNonSeedSong() {
        Song song1 = new Song("song1");
        Song song2 = new Song("song2");
        List<Song> songs = List.of(song1, song2);

        Username alex = new Username("alex");
        List<Username> users = List.of(alex);

        double[][] ratings = new double[2][1];
        ratings[0][0] = 0.0;
        ratings[1][0] = 1.0;

        SongRatingNormalizer.SongNormalizationResult normalization = new SongRatingNormalizer.SongNormalizationResult(
                songs, users, ratings);

        KMeansClustering kmeans = new KMeansClustering();
        List<SongRecommendation> recommendations = kmeans.recommend(normalization, List.of("song1"));
        SongRecommendation rec = recommendations.get(0);

        assertEquals("song1", rec.startingSong());
        assertEquals("song2", rec.recommendedSong());
    }

    @Test
    void recommendThrowsWhenNoSongs() {
        // Empty normalization
        List<Song> songs = List.of();
        List<Username> users = List.of();
        double[][] ratings = new double[0][0];

        SongRatingNormalizer.SongNormalizationResult normalization = new SongRatingNormalizer.SongNormalizationResult(
                songs, users, ratings);

        KMeansClustering kmeans = new KMeansClustering();

        assertThrows(IllegalArgumentException.class,
                () -> kmeans.recommend(normalization, List.of("anything")));
    }

    @Test
    void buildPlaylistSimpleCase() {
        // Three songs, one user, simple distance structure
        Song song1 = new Song("song1");
        Song song2 = new Song("song2");
        Song song3 = new Song("song3");

        List<Song> songs = List.of(song1, song2, song3);

        Username alex = new Username("alex");
        List<Username> users = List.of(alex);

        double[][] ratings = new double[3][1];
        ratings[0][0] = 0.0; // song1
        ratings[1][0] = 0.1; // song2 (closest to song1)
        ratings[2][0] = 2.0; // song3 (farther)

        SongRatingNormalizer.SongNormalizationResult normalization = new SongRatingNormalizer.SongNormalizationResult(
                songs, users, ratings);

        KMeansClustering kmeans = new KMeansClustering();

        // likedSongs: only song1, k = 1
        List<Song> playlist = kmeans.buildPlaylist(normalization, List.of("song1"), 1);

        // We expect both non-liked songs, closest one first
        assertEquals(2, playlist.size());
        assertEquals("song2", playlist.get(0).value());
        assertEquals("song3", playlist.get(1).value());
    }

    @Test
    void buildPlaylistThrowsWhenTooFewSongs() {
        // Only one song -> cannot build playlist with k=1 (needs at least k+1 songs)
        Song song1 = new Song("song1");
        List<Song> songs = List.of(song1);

        Username alex = new Username("alex");
        List<Username> users = List.of(alex);

        double[][] ratings = new double[1][1];
        ratings[0][0] = 0.0;

        SongRatingNormalizer.SongNormalizationResult normalization = new SongRatingNormalizer.SongNormalizationResult(
                songs, users, ratings);

        KMeansClustering kmeans = new KMeansClustering();

        assertThrows(IllegalArgumentException.class,
                () -> kmeans.buildPlaylist(normalization, List.of("song1"), 1));
    }
}
