/*
 * Layer: domain model / PA4
 * Role: Immutable prediction row (song, user, predicted rating), and prediction row ordering.
 * Used by: UserPredictionComputer, CsvUserPredictionsWriter
 */

 //One Predicted ratings sortable by song then user
public record PredictionRow(String song, String user, double prediction) implements Comparable<PredictionRow> {

    //Orders the rows by song, then by user if needed
    @Override
    public int compareTo(PredictionRow other) {
        int songComparison = this.song.compareTo(other.song());
        if (songComparison != 0) {
            return songComparison;
        }
        return this.user().compareTo(other.user());
    }
}