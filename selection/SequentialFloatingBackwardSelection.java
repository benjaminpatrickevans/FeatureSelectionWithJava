package selection;

import java.util.HashSet;
import java.util.Set;

/**
 * SFBS
 *
 * Created by ben on 8/04/17.
 */
public class SequentialFloatingBackwardSelection extends FeatureSelection {

    public SequentialFloatingBackwardSelection(Set<Instance> instances){
        super(instances);
    }

    public SequentialFloatingBackwardSelection(Set<Instance> training, Set<Instance> testing){
        super(training, testing);
    }

    public Set<Integer> select(int maxNumFeatures) {
        return select((noImprovement, size) -> size > maxNumFeatures || noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS);
    }

    public Set<Integer> select() {
        return select((noImprovement, size) -> noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS);
    }

    public Set<Integer> select(Criteria criteria) {
        // In this case we have no data to use, so return the empty set
        if (trainingInstances == null || trainingInstances.isEmpty()) return new HashSet<Integer>();

        // To begin with all features are selected, so all the indices from 0..totalFeatures are remaining
        Set<Integer> selectedFeatures = getAllFeatureIndices();

        // Subset of only remaining features indices
        Set<Integer> remainingFeatures = new HashSet<>();

        // Keep track of the best solution, so we never get worse
        double highestAccuracy = 0;
        Set<Integer> bestSoFar = new HashSet<>();
        double accuracy = objectiveFunction(selectedFeatures);
        double lastAccuracy = accuracy;

        // Number of iterations with no improvement
        double noImprovement = 0;

        while (criteria.evaluate(noImprovement, selectedFeatures.size())){

            /* EXCLUDE THE WORST FEATURE */
            int worstFeature = worst(selectedFeatures);

            // No more valid features
            if (worstFeature == -1) break;

            selectedFeatures.remove(worstFeature);
            // Add the feature back to our remaining features
            remainingFeatures.add(worstFeature);

            double accuracyBeforeAddition = objectiveFunction(selectedFeatures);

            /* INCLUDE THE BEST FEATURES */
            // Now add the best features, while we are improving
            while (true){
                int bestFeature = best(selectedFeatures, remainingFeatures);

                // No more valid features
                if (bestFeature == -1) break;

                selectedFeatures.add(bestFeature);
                remainingFeatures.remove(bestFeature);

                double accuracyAfterAddition = objectiveFunction(selectedFeatures);

                // If the accuracy did not improve or we have just removed this feature, undo this step and continue removing features
                if (accuracyAfterAddition < accuracyBeforeAddition || (bestFeature == worstFeature)) {
                    selectedFeatures.remove(bestFeature);
                    remainingFeatures.add(bestFeature);
                    break;
                }

                accuracyBeforeAddition = accuracyAfterAddition;
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