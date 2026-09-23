import java.io.IOException;
import java.util.List;

public final class SongRecommendationsRunnerPA5 implements ModeRunner {

    /*
    * Runs PA5: Does PA4 then fills in missing ratings, Drops songs with too little variation, 
    * normalizes ratings per song into a matrix for clustering, creates cluster of songs around 
    * the user selected songs and collects the recommendations. Then writes orignal songs and 
    * recommendations to output CSV
    */
    @Override
    public void run(ModeConfig config) throws IOException {

        List<RatingRow> rows = new RatingsReader().read(config.input());
        List<RatingRow> filteredRows = new UserCooperationFilter().filter(rows);

        Profiles profiles = new UserProfileBuilder().buildProfiles(filteredRows);
        if (profiles.users().size() < 2) {
            throw Errors.tooFewCooperativeUsers();
        }

        ScoreNormalizer.UserNormalizationResult result = new ScoreNormalizer().normalize(profiles);
        Profiles normalized = result.normalized();
        DistanceMatrix distances = new UserSimilarityComputer().distances(normalized);
        List<PredictionRow> predictions = new UserPredictionComputer().predict(profiles, result, distances);

        ProfileFiller filler = new ProfileFiller(profiles);
        filler.applyPredictions(predictions);
        Profiles fullProfiles = filler.build();

        OneRatingSongFilter.FilterResults filtered = new OneRatingSongFilter().filter(fullProfiles);
        Profiles fullFilteredProfiles = filtered.profiles();
        
        for (String name : config.songSelections()) {
            Song selected = new Song(name);

            if (filtered.droppedSongs().contains(selected)) {
                throw Errors.tooFewDistinctRatings();
            }
        }

        SongRatingNormalizer normalizer = new SongRatingNormalizer();
        SongRatingNormalizer.SongNormalizationResult songData = normalizer.normalize(fullFilteredProfiles);

        int songCount = songData.songs().size();
        int k = config.songSelections().size();

        if (songCount < k + 1) {
            throw Errors.noRecommendations();
        }

        KMeansClustering recommender = new KMeansClustering();
        List<SongRecommendation> recommendations = recommender.recommend(songData, config.songSelections());

        new SongRecommendationsWriter().write(config.output(), recommendations);
    }
}
