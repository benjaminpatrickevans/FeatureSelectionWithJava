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
    public Set<Integer> select(int numFeaturesToSelect) {
        return select((features, size) -> size > numFeaturesToSelect);
    }

    @Override
    public Set<Integer> select(double goalAccuracy) {
        return select((features, size) -> objectiveFunction(features) < goalAccuracy);
    }

    public Set<Integer> select(Criteria criteria) {
        // In this case we have no data to use, so return the empty set
        if (instances == null || instances.isEmpty()) return new HashSet<Integer>();

        // To begin with all features are selected
        Set<Integer> selectedFeatures = getFeatures();

        while (criteria.evaluate(selectedFeatures, selectedFeatures.size())){
            int feature = worst(selectedFeatures);

            // No more valid features
            if (feature == -1) break;

            // Remove the feature so we do not keep selecting the same one
            selectedFeatures.remove(feature);
        }

        return selectedFeatures;
    }
}