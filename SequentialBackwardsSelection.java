import java.util.HashSet;
import java.util.Set;

/**
 * Created by ben on 18/04/17.
 */
public class SequentialBackwardsSelection extends FeatureSelection {

    public SequentialBackwardsSelection(Set<Instance> instances){
        super(instances);
    }

    @Override
    public Set<Integer> select(int maxNumFeatures) {
        return select((accuracy, size) -> size > maxNumFeatures);
    }

    @Override
    public Set<Integer> select(double minimumAccuracy) {
        return select((accuracy, size) -> accuracy < minimumAccuracy);
    }

    public Set<Integer> select(Criteria criteria) {
        // In this case we have no data to use, so return the empty set
        if (instances == null || instances.isEmpty()) return new HashSet<Integer>();

        // To begin with all features are selected
        Set<Integer> selectedFeatures = getFeatures();

        // Keep track of the best solution, so we never get worse
        double highestAccuracy = 0;
        Set<Integer> bestSoFar = new HashSet<>();
        double accuracy = objectiveFunction(selectedFeatures);

        while (criteria.evaluate(accuracy, selectedFeatures.size())){
            int feature = worst(selectedFeatures);

            // No more valid features
            if (feature == -1) break;

            // Remove the feature so we do not keep selecting the same one
            selectedFeatures.remove(feature);

            accuracy = objectiveFunction(selectedFeatures);

            if (accuracy > highestAccuracy) {
                highestAccuracy = accuracy;
                // Make a copy, so we don't accidentally modify this
                bestSoFar = new HashSet<>(selectedFeatures);
            }
        }

        return bestSoFar;
    }
}