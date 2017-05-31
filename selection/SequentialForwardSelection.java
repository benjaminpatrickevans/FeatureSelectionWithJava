package selection;

import java.util.HashSet;
import java.util.Set;

/**
 * Performs Sequential Floating Forward Selection (SFFS)
 * <p>
 * - Starts with empty set of features
 * - Adds the "best" feature until stopping criteria is met
 */
public class SequentialForwardSelection extends FeatureSelection {

    public SequentialForwardSelection(String file, int classIndex) throws Exception {
        super(file, classIndex);
    }

    public SequentialForwardSelection(String training, String testing, int classIndex) throws Exception {
        super(training, testing, classIndex);
    }

    public Set<Integer> select(int maxNumFeatures) throws Exception {
        return select((accuracy, size) -> size < maxNumFeatures);
    }

    public Set<Integer> select() throws Exception {
        return select((noImprovement, size) -> noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS);
    }

    public Set<Integer> select(Criteria criteria) throws Exception {
        // To begin with no features are selected, so all the indices from 0..totalFeatures are remaining
        Set<Integer> remainingFeatures = getAllFeatureIndices();

        // Subset of only selected features indices
        Set<Integer> selectedFeatures = new HashSet<>();

        // Keep track of the best solution, so we never get worse
        double highestAccuracy = 0;
        Set<Integer> bestSoFar = new HashSet<>();
        double accuracy = objectiveFunction(selectedFeatures);
        double lastAccuracy = accuracy;

        printAccuracy(selectedFeatures.size(), accuracy);

        // Number of iterations with no improvement
        double noImprovement = 0;

        while (criteria.evaluate(noImprovement, selectedFeatures.size())) {
            int feature = best(selectedFeatures, remainingFeatures);
            // No more valid features
            if (feature == -1) break;

            selectedFeatures.add(feature);
            // Remove the feature so we do not keep selecting the same one
            remainingFeatures.remove(feature);

            accuracy = objectiveFunction(selectedFeatures);

            if (greaterThan(accuracy, highestAccuracy)) {
                highestAccuracy = accuracy;
                // Make a copy, so we don't accidentally modify the best subset
                bestSoFar = new HashSet<>(selectedFeatures);
            }

            printAccuracy(selectedFeatures.size(), accuracy);

            if (lessThanOrEqualTo(accuracy, lastAccuracy)) {
                noImprovement++;
            } else {
                noImprovement = 0;
            }
            lastAccuracy = accuracy;
        }

        return bestSoFar;
    }


}