package selection;

import java.util.HashSet;
import java.util.Set;

/**
 * Performs Sequential Floating Forward Selection (SFFS)
 *
 * - Starts with empty set of features
 * - Adds the "best" feature
 * - Performs SBS as long as the objective function increases
 * - Goes back to step 2 until stopping criteria is met
 */
public class SequentialFloatingForwardSelection extends FeatureSelection {

    public SequentialFloatingForwardSelection(String file, int classIndex) throws Exception {
        super(file, classIndex);
    }
    public SequentialFloatingForwardSelection(String training, String testing, int classIndex) throws Exception {
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

        Set<Set<Integer>> visitedSubsets = new HashSet<Set<Integer>>();
        visitedSubsets.add(new HashSet<>(selectedFeatures));

        // Number of iterations with no improvement
        double noImprovement = 0;

        while (criteria.evaluate(noImprovement, selectedFeatures.size())) {

            /* INCLUDE THE BEST FEATURE */
            int bestFeature = best(selectedFeatures, remainingFeatures);

            // No more valid features
            if (bestFeature == -1) break;

            selectedFeatures.add(bestFeature);
            // Remove the feature so we do not keep selecting the same one
            remainingFeatures.remove(bestFeature);

            visitedSubsets.add(new HashSet<>(selectedFeatures));

            double accuracyBeforeRemoval = objectiveFunction(selectedFeatures);
            printAccuracy(selectedFeatures.size(), accuracyBeforeRemoval);

            /* EXCLUDE THE WORST FEATURES */
            // Now remove the worst features, while we are improving
            while (true) {
                int worstFeature = worst(selectedFeatures);

                // No more valid features
                if (worstFeature == -1) break;

                selectedFeatures.remove(worstFeature);
                // Feature becomes available again
                remainingFeatures.add(worstFeature);

                double accuracyAfterRemoval = objectiveFunction(selectedFeatures);

                printAccuracy(selectedFeatures.size(), accuracyAfterRemoval);

                // If the accuracy did not improve or we have already been to this state, undo this step and continue adding features
                if (lessThan(accuracyAfterRemoval, accuracyBeforeRemoval) || visitedSubsets.contains(selectedFeatures)) {
                    selectedFeatures.add(worstFeature);
                    remainingFeatures.remove(worstFeature);
                    break;
                }

                visitedSubsets.add(new HashSet<>(selectedFeatures));
                accuracyBeforeRemoval = accuracyAfterRemoval;
            }

            accuracy = objectiveFunction(selectedFeatures);

            // If the accuracy is higher than our previous best, or the same with less features
            if (greaterThan(accuracy, highestAccuracy) || (equalTo(accuracy, highestAccuracy) && selectedFeatures.size() < bestSoFar.size())) {
                highestAccuracy = accuracy;
                // Make a copy, so we don't accidentally modify this
                bestSoFar = new HashSet<>(selectedFeatures);
            }

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