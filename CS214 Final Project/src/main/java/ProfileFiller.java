import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

//Fills in missing ratings using predictons and weighted averages
public final class ProfileFiller {
    //Copy of Profiles (Username -> Song -> Ratings)
    private final Map<Username, Map<Song, Double>> ratingsByUser = new TreeMap<>();
    //Per song running stats for mean and count
    private final Map<Song, RatingCollection> songCollections = new TreeMap<>();
    //Per User running stats for mean and count
    private final Map<Username, RatingCollection> userCollections = new TreeMap<>();

    //Main method: Copies input Profiles and builds the per song and per user stats from existing ratings
    public ProfileFiller(Profiles missingRatings) {

        for (Username user : new TreeSet<>(missingRatings.users())) {
            UserProfile profile = missingRatings.get(user);
            Map<Song, Double> row = new TreeMap<>(profile.asMap());
            ratingsByUser.put(user, row);

            RatingCollection userCollection = userCollections.get(user);
            if (userCollection == null) {
                userCollection = new RatingCollection();
                userCollections.put(user, userCollection);
            }

            for (Map.Entry<Song, Double> entry : row.entrySet()) {
                Song song = entry.getKey();
                Double rating = entry.getValue();

                RatingCollection songCollection = songCollections.get(song);
                if (songCollection == null) {
                    songCollection = new RatingCollection();
                    songCollections.put(song, songCollection);
                }

                //Only track real ratings in running stats
                if (rating != null && !rating.isNaN()) {
                    songCollection.add(rating);
                    userCollection.add(rating);
                }
            }
        }
    }

    //Apply predictions from PA4 into a ratings grid
    public void applyPredictions(List<PredictionRow> predictions) {

        for (PredictionRow prediction : predictions) {

            Double value = prediction.prediction();
            if (value == null || value.isNaN()) {
                continue;
            }

            Username user = new Username(prediction.user());
            Song song = new Song(prediction.song());

            Map<Song, Double> row = ratingsByUser.get(user);
            if (row == null) {
                continue;
            }
            row.put(song, value);
        }
    }

    //Fills in the gaps in ratings and Wraps back into Profiles
    public Profiles build() {
        fillWithWeightedAvg();

        Map<Username, UserProfile> byUser = new TreeMap<>();
        for (Map.Entry<Username, Map<Song, Double>> entry : ratingsByUser.entrySet()) {
            byUser.put(entry.getKey(), new UserProfile(entry.getValue()));
        }
        return new Profiles(byUser);
    }

    //Fills in the NaN ratings with a weighted average of song mean and user mean
    private void fillWithWeightedAvg() {
        for (Map.Entry<Username, Map<Song, Double>> userEntry : ratingsByUser.entrySet()) {
            Username user = userEntry.getKey();
            Map<Song, Double> row = userEntry.getValue();

            for (Map.Entry<Song, Double> songEntry : row.entrySet()) {
                Double rating = songEntry.getValue();

                //Skip ratings that have been filled
                if (rating != null && !rating.isNaN()) {
                    continue;
                }

                double filled = computeWeightedAvg(user, songEntry.getKey());
                songEntry.setValue(filled);
            }
        }
    }

    //Compute weighted average between 1 and 5 from the running stats for song and user
    private double computeWeightedAvg(Username user, Song song) {
        RatingCollection songCollection = songCollections.get(song);
        RatingCollection userCollection = userCollections.get(user);

        double songMean = 0.0;
        int songCoutn = 0;

        if (songCollection != null) {
            songMean = songCollection.mean();
            songCoutn = songCollection.count();
        }

        double userMean = 0.0;
        int userCount = 0;

        if (userCollection != null) {
            userMean = userCollection.mean();
            userCount = userCollection.count();
        }

        int total = songCoutn + userCount;
        double weighted = (songMean * songCoutn + userMean * userCount) / total;

        int rounded = (int) Math.round(weighted);
        if (rounded < 1) {
            rounded = 1;
        }

        if (rounded > 5) {
            rounded = 5;
        }
        return (double) rounded;
    }

    //Running stats accumulator, similary to songStats without std Dev
    private static final class RatingCollection {
        private double mean = 0.0;
        private int count = 0;

        void add(double value) {
            count++;

            double delta = value - mean;
            mean += delta / count;
        }

        double mean() {
            if (count == 0) {
                return 0.0;
            }
            return mean;
        }

        int count() {
            return count;
        }
    }
}
