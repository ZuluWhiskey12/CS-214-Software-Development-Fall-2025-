import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SongTest {

    @Test
    void toStringReturns() {
        Song id = new Song("All Star");

        assertEquals("All Star", id.toString());
    }
}
