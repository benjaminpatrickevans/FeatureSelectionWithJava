package selection;

import java.util.HashSet;
        import java.util.Set;

/**
 * Created by ben on 8/04/17.
 */
public class SequentialFloatingForwardSelection extends FeatureSelection {

    public SequentialFloatingForwardSelection(Set<Instance> instances){
        super(instances);
    }

    public SequentialFloatingForwardSelection(Set<Instance> training, Set<Instance> testing){
        super(training, testing);
    }

    public Set<Integer> select(int maxNumFeatures) {
        return select((accuracy, size) -> size < maxNumFeatures);
    }

    public Set<Integer> select() {
        return select((noImprovement, size) -> noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS);
    }

    public Set<Integer> select(Criteria criteria) {
        // In this case we have no data to use, so return the empty set
        if (trainingInstances == null || trainingInstances.isEmpty()) return new HashSet<Integer>();

        // To begin with no features are selected, so all the indices from 0..totalFeatures are remaining
        Set<Integer> remainingFeatures = getAllFeatureIndices();

        // Subset of only selected features indices
        Set<Integer> selectedFeatures = new HashSet<>();

        // Keep track of the best solution, so we never get worse
        double highestAccuracy = 0;
        Set<Integer> bestSoFar = new HashSet<>();
        double accuracy = objectiveFunction(selectedFeatures);
        double lastAccuracy = accuracy;

        // Number of iterations with no improvement
        double noImprovement = 0;

        while (criteria.evaluate(noImprovement, selectedFeatures.size())){

            /* INCLUDE THE BEST FEATURE */
            int bestFeature = best(selectedFeatures, remainingFeatures);

            // No more valid features
            if (bestFeature == -1) break;

            selectedFeatures.add(bestFeature);
            // Remove the feature so we do not keep selecting the same one
            remainingFeatures.remove(bestFeature);

            double accuracyBeforeRemoval = objectiveFunction(selectedFeatures);


            /* EXCLUDE THE WORST FEATURES */
            // Now remove the worst features, while we are improving
            while(true){
                int worstFeature = worst(selectedFeatures);

                // No more valid features
                if (worstFeature == -1) break;

                selectedFeatures.remove(worstFeature);
                // Feature becomes available again
                remainingFeatures.add(worstFeature);

                double accuracyAfterRemoval = objectiveFunction(selectedFeatures);

                // If the accuracy did not improve or we have just added this feature, undo this step and continue adding features
                if (accuracyAfterRemoval < accuracyBeforeRemoval || (worstFeature == bestFeature)) {
                    selectedFeatures.add(worstFeature);
                    remainingFeatures.remove(worstFeature);
                    break;
                }

                accuracyBeforeRemoval = accuracyAfterRemoval;
            }

            accuracy = objectiveFunction(selectedFeatures);

            // If the accuracy is higher than our previous best, or the same with less features
            if (accuracy > highestAccuracy || (accuracy == highestAccuracy && selectedFeatures.size() < bestSoFar.size())){
                highestAccuracy = accuracy;
                // Make a copy, so we don't accidentally modify this
                bestSoFar = new HashSet<>(selectedFeatures);
            }

            if (Double.compare(accuracy, lastAccuracy) <= 0){
                noImprovement++;
            } else{
                noImprovement = 0;
            }

            lastAccuracy = accuracy;

        }

        return bestSoFar;
    }


}