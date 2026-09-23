/*
 * Layer: domain model / input row
 * Role: Immutable record representing a single CSV row (song, user, rating).
 * Used by: CsvDataReader, SongStatsAggregator, UserCooperationFilter, UserProfileBuilder
 */
public record RatingRow(String song, String user, int rating) {
}
