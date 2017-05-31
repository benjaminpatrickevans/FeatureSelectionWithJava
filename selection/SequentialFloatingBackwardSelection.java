package selection;

import java.util.HashSet;
import java.util.Set;

/**
 * Performs Sequential Floating Backward Selection (SFBS)
 * <p>
 * - Starts with full set of features
 * - Removes the "worst" feature
 * - Performs SFS as long as the objective function increases
 * - Goes back to step 2 until stopping criteria is met
 */
public class SequentialFloatingBackwardSelection extends FeatureSelection {

    public SequentialFloatingBackwardSelection(String file, int classIndex) throws Exception {
        super(file, classIndex);
    }

    public SequentialFloatingBackwardSelection(String training, String testing, int classIndex) throws Exception {
        super(training, testing, classIndex);
    }

    public Set<Integer> select(int maxNumFeatures) throws Exception {
        return select((noImprovement, size) -> size > maxNumFeatures || noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS, maxNumFeatures);
    }

    public Set<Integer> select() throws Exception {
        return select((noImprovement, size) -> noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS);
    }

    public Set<Integer> select(Criteria criteria) throws Exception {
        // Max features is all the features
        return select(criteria, getNumFeatures());
    }

    private Set<Integer> select(Criteria criteria, int maxNumFeatures) throws Exception {

        // To begin with all features are selected, so all the indices from 0..totalFeatures are remaining
        Set<Integer> selectedFeatures = getAllFeatureIndices();

        // Subset of only remaining features indices
        Set<Integer> remainingFeatures = new HashSet<>();

        // Keep track of the best solution, so we never get worse
        double highestAccuracy = 0;
        Set<Integer> bestSoFar = new HashSet<>();
        double accuracy = objectiveFunction(selectedFeatures);
        double lastAccuracy = accuracy;

        // Keep track of the visited states to avoid getting stuck in an infinite loop
        Set<Set<Integer>> visitedSubsets = new HashSet<Set<Integer>>();
        visitedSubsets.add(new HashSet<>(selectedFeatures));

        // Number of iterations with no improvement
        int iterationsWithoutImprovement = 0;

        printAccuracy(selectedFeatures.size(), accuracy);

        while (criteria.evaluate(iterationsWithoutImprovement, selectedFeatures.size())) {

            /* EXCLUDE THE WORST FEATURE */
            int worstFeature = worst(selectedFeatures);

            // No more valid features
            if (worstFeature == -1) break;

            // Remove the feature and add the feature back to our remaining features
            selectedFeatures.remove(worstFeature);
            remainingFeatures.add(worstFeature);

            // Note that we have been to this state
            visitedSubsets.add(new HashSet<>(selectedFeatures));

            // This will be our point of comparison when adding features
            double accuracyBeforeAddition = objectiveFunction(selectedFeatures);

            printAccuracy(selectedFeatures.size(), accuracyBeforeAddition);

            /* INCLUDE THE BEST FEATURES */
            // Now add the best features, while we are improving
            while (true) {
                int bestFeature = best(selectedFeatures, remainingFeatures);

                // No more valid features
                if (bestFeature == -1) break;

                selectedFeatures.add(bestFeature);
                remainingFeatures.remove(bestFeature);

                double accuracyAfterAddition = objectiveFunction(selectedFeatures);

                printAccuracy(selectedFeatures.size(), accuracyAfterAddition);

                // If the accuracy did not improve or we have been to this state, undo this step and continue removing features
                if (lessThan(accuracyAfterAddition, accuracyBeforeAddition) || visitedSubsets.contains(selectedFeatures)) {
                    selectedFeatures.remove(bestFeature);
                    remainingFeatures.add(bestFeature);
                    break;
                }

                // Note that we have been to this state
                visitedSubsets.add(new HashSet<>(selectedFeatures));

                // This will be our new point of comparison for the next addition to the selected features
                accuracyBeforeAddition = accuracyAfterAddition;
            }

            accuracy = objectiveFunction(selectedFeatures);

            // If the accuracy is higher than our previous best, or the same with less features and its a valid size (<= maxFeatures)
            if ((greaterThan(accuracy, highestAccuracy) || (equalTo(accuracy, highestAccuracy) && selectedFeatures.size() < bestSoFar.size()))
                    && selectedFeatures.size() <= maxNumFeatures) {
                highestAccuracy = accuracy;
                // Save our best set
                bestSoFar = new HashSet<>(selectedFeatures);
            }

            if (lessThanOrEqualTo(accuracy, lastAccuracy)) {
                iterationsWithoutImprovement++;
            } else {
                iterationsWithoutImprovement = 0;
            }

            lastAccuracy = accuracy;

        }

        return bestSoFar;
    }


}