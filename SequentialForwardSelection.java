import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ben on 8/04/17.
 */
public class SequentialForwardSelection extends FeatureSelection {

    public Set<Integer> select(final Set<Instance> instances, int numFeaturesToSelect) {
        // In this case we have no data to use, so return the empty set
        if(instances == null || instances.isEmpty()) return new HashSet<Integer>();

        // Extract an instance to check the amount of features, assumes all instances have same # of features
        Instance sampleInstance = instances.iterator().next();
        int totalFeatures = sampleInstance.getNumFeatures();

        // To begin with no features are selected, so all the indices from 0..totalFeatures are remaining
        Set<Integer> remainingFeatures = IntStream.rangeClosed(0, totalFeatures - 1)
                .boxed().collect(Collectors.toSet());

        // Nothing we can do if the number of features to select is greater than or equal to the total size
        if (numFeaturesToSelect >= totalFeatures){
            return remainingFeatures;
        }

        // Subset of only selected features indices
        Set<Integer> selectedFeatures = new HashSet<>();

        Classifier classifier = new Classifier(instances);

        while (selectedFeatures.size() < numFeaturesToSelect){
            int feature = best(classifier, selectedFeatures, remainingFeatures);

            // No more valid features
            if (feature == -1) break;

            selectedFeatures.add(feature);
            // Remove the feature so we do not keep selecting the same one
            remainingFeatures.remove(feature);
        }

        compareAccuracy(classifier, selectedFeatures, totalFeatures);
        return selectedFeatures;
    }

    public Set<Integer> select(final Set<Instance> instances, double goalAccuracy) {
        // In this case we have no data to use, so return the empty set
        if (instances == null || instances.isEmpty()) return new HashSet<Integer>();

        // Extract an instance to check the amount of features, assumes all instances have same # of features
        Instance sampleInstance = instances.iterator().next();
        int totalFeatures = sampleInstance.getNumFeatures();

        // To begin with no features are selected, so all the indices from 0..totalFeatures are remaining
        Set<Integer> remainingFeatures = IntStream.rangeClosed(0, totalFeatures - 1)
                .boxed().collect(Collectors.toSet());

        // Subset of only selected features indices
        Set<Integer> selectedFeatures = new HashSet<>();

        // Track classifiction accuracy
        double accuracy = 0;

        Classifier classifier = new Classifier(instances);

        while (accuracy < goalAccuracy){
            int feature = best(classifier, selectedFeatures, remainingFeatures);
            // No more valid features
            if (feature == -1) break;

            selectedFeatures.add(feature);
            // Remove the feature so we do not keep selecting the same one
            remainingFeatures.remove(feature);

            accuracy = objectiveFunction(classifier, selectedFeatures);
        }

        compareAccuracy(classifier, selectedFeatures, totalFeatures);
        return selectedFeatures;
    }




}