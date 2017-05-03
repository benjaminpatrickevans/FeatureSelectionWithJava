import java.util.HashSet;
import java.util.Set;

/**
 * Created by ben on 8/04/17.
 */
public class SequentialForwardSelection extends FeatureSelection {

    public SequentialForwardSelection(Set<Instance> instances){
        super(instances);
    }

    public Set<Integer> select(int numFeaturesToSelect) {
       return select((features, size) -> size < numFeaturesToSelect);
    }

    public Set<Integer> select(double goalAccuracy) {
        return select((features, size) -> objectiveFunction( features) < goalAccuracy);
    }

    public Set<Integer> select(Criteria criteria) {
        // In this case we have no data to use, so return the empty set
        if (instances == null || instances.isEmpty()) return new HashSet<Integer>();

        // To begin with no features are selected, so all the indices from 0..totalFeatures are remaining
        Set<Integer> remainingFeatures = getFeatures();

        // Subset of only selected features indices
        Set<Integer> selectedFeatures = new HashSet<>();

        Classifier classifier = new Classifier(instances);

        while (criteria.evaluate(selectedFeatures, selectedFeatures.size())){
            int feature = best(selectedFeatures, remainingFeatures);
            // No more valid features
            if (feature == -1) break;

            selectedFeatures.add(feature);
            // Remove the feature so we do not keep selecting the same one
            remainingFeatures.remove(feature);
        }

        return selectedFeatures;
    }


}