import java.io.IOException;
import java.util.List;

public class PlayListRunnerPA6 implements ModeRunner {

    @Override
    public void run(ModeConfig config) throws IOException {

        // Read ratings and drop uncooperative users / songs
        List<RatingRow> rows = new RatingsReader().read(config.input());
        List<RatingRow> filteredRows = new UserCooperationFilter().filter(rows);

        Profiles profiles = new UserProfileBuilder().buildProfiles(filteredRows);
        if (profiles.users().size() < 2) {
            throw Errors.tooFewCooperativeUsers();
        }

        // Normalize per user and compute user-to-user distances
        ScoreNormalizer.UserNormalizationResult result = new ScoreNormalizer().normalize(profiles);
        Profiles normalized = result.normalized();
        DistanceMatrix distances = new UserSimilarityComputer().distances(normalized);

        // Predict missing ratings
        List<PredictionRow> predictions = new UserPredictionComputer().predict(profiles, result, distances);

        // Fill missing ratings with predictions + weighted averages
        ProfileFiller filler = new ProfileFiller(profiles);
        filler.applyPredictions(predictions);
        Profiles fullProfiles = filler.build();

        // Drop songs with only one distinct rating
        OneRatingSongFilter.FilterResults filtered = new OneRatingSongFilter().filter(fullProfiles);
        Profiles fullFilteredProfiles = filtered.profiles();

        // Make sure selected songs still have enough variation
        for (String name : config.songSelections()) {
            Song selected = new Song(name);
            if (filtered.droppedSongs().contains(selected)) {
                throw Errors.tooFewDistinctRatings();
            }
        }

        // Normalize per song into matrix for clustering and playlist scoring
        SongRatingNormalizer normalizer = new SongRatingNormalizer();
        SongRatingNormalizer.SongNormalizationResult songData = normalizer.normalize(fullFilteredProfiles);

        // Build playlist using K-means clusters and the liked songs
        KMeansClustering clustering = new KMeansClustering();
        List<Song> playlist = clustering.buildPlaylist(
                songData,
                config.songSelections(),
                config.k());

        new PlayListWriter().write(config.output(), playlist);
    }
}
