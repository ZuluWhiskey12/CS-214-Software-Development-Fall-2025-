import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PredictionRowTest {
    @Test
    void sortBySongName() {
        PredictionRow row1 = new PredictionRow("songB", "alex", 3.0);
        PredictionRow row2 = new PredictionRow("songA", "zach", 4.0);

        List<PredictionRow> rows = new ArrayList<>(List.of(row1, row2));
        Collections.sort(rows);

        assertEquals("songA", rows.get(0).song());
        assertEquals("songB", rows.get(1).song());
    }

    @Test  
    void sortByUserWithSameSongName() {
        PredictionRow row1 = new PredictionRow("sameSong", "alex", 3.0);
        PredictionRow row2 = new PredictionRow("sameSong", "zach", 4.0);

        List<PredictionRow> rows = new ArrayList<>(List.of(row1, row2));
        Collections.sort(rows);

        assertEquals("alex", rows.get(0).user());
        assertEquals("zach", rows.get(1).user());
    }
}
