/*
 * Layer: domain model / PA5
 * Role: single starting song to a suggested song.
 * Used by: SongRecommendationWriter, SongRecommendationRunnerPA5
 */
public record SongRecommendation(String startingSong, String recommendedSong) {
}
