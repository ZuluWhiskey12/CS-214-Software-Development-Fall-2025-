import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UsernameTest {
    
    @Test
    void toStringReturns() {
        Username id = new Username("zach");

        assertEquals("zach", id.toString());
    }
}
