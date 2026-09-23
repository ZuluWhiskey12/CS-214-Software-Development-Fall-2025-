/*
 * Layer: domain model / identifier pair
 * Role: Ordered pair of Song values; Comparable by (song1, song2).
 * Depends on: SongId
 * Used by: SongSimilarityComputer, CsvSongSimilarityWriter
 */
public record SongPair(Song song1, Song song2) implements Comparable<SongPair> {

    //Sorts pairs by song names
    @Override
    public int compareTo(SongPair other) {
        int compare = this.song1().compareTo(other.song1());

        if (compare != 0) {
            return compare;
        }
        return this.song2().compareTo(other.song2());
    }
}
