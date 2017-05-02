import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ben on 18/04/17.
 */
public class SequentialBackwardsSelection extends FeatureSelection {

    @Override
    public Set<Integer> select(Set<Instance> instances, int numFeaturesToSelect) {
        // In this case we have no data to use, so return the empty set
        if(instances == null || instances.isEmpty()) return new HashSet<Integer>();

        // Extract an instance to check the amount of features, assumes all instances have same # of features
        Instance sampleInstance = instances.iterator().next();
        int totalFeatures = sampleInstance.getNumFeatures();

        // To begin with all features are selected
        Set<Integer> selectedFeatures = IntStream.rangeClosed(0, totalFeatures - 1)
                .boxed().collect(Collectors.toSet());

        // Nothing we can do if the number of features to select is greater than or equal to the total size
        if (numFeaturesToSelect >= totalFeatures){
            return selectedFeatures;
        }

        Classifier classifier = new Classifier(instances);

        while (selectedFeatures.size() >= numFeaturesToSelect){
            int feature = worst(classifier, selectedFeatures);

            // No more valid features
            if (feature == -1) break;

            // Remove the feature so we do not keep selecting the same one
            selectedFeatures.remove(feature);
        }

        return selectedFeatures;
    }

    @Override
    public Set<Integer> select(Set<Instance> instances, double goalAccuracy) {
        return null;
    }
}