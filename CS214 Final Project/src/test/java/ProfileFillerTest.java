import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class ProfileFillerTest {
    
    @Test
    void fillsMissingRatings() {
        Song song1 = new Song("song1");
        Song song2 = new Song("song2");

        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(song1, 4.0);
        alexRatings.put(song2, Double.NaN);
        UserProfile alexProfile = new UserProfile(alexRatings);

        Map<Song, Double> zachRatings = new TreeMap<>();
        zachRatings.put(song1, 2.0);
        zachRatings.put(song2, 2.0);
        UserProfile zachProfile = new UserProfile(zachRatings);

        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(alex, alexProfile);
        byUser.put(zach, zachProfile);
        Profiles profiles = new Profiles(byUser);

        List<PredictionRow> predictions = List.of();

        ProfileFiller filler = new ProfileFiller(profiles);
        filler.applyPredictions(predictions);
        Profiles filled = filler.build();

        UserProfile alexFilled = filled.get(alex);

        assertEquals(3.0, alexFilled.rating(song2));
    }

    @Test
    void predictionOverWeightedAvg() {
        Song song2 = new Song("song2");
        Username alex = new Username("alex");

        Map<Song, Double> alexRatings = new TreeMap<>();
        alexRatings.put(song2, Double.NaN);
        UserProfile alexProfile = new UserProfile(alexRatings);
        
        Map<Username, UserProfile> byUser = new TreeMap<>();
        byUser.put(alex, alexProfile);
        Profiles profiles = new Profiles(byUser);

        List<PredictionRow> predictions = new ArrayList<>();
        predictions.add(new PredictionRow("song2", "alex", 3.0));

        ProfileFiller filler = new ProfileFiller(profiles);
        filler.applyPredictions(predictions);
        Profiles filled = filler.build();

        UserProfile alexFilled = filled.get(alex);

        assertEquals(3.0, alexFilled.rating(song2));
    }
}
