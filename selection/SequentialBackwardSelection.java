package selection;

import java.util.HashSet;
import java.util.Set;

/**
 * Performs Sequential Backward Selection (SBS)
 * <p>
 * - Starts with full set of features
 * - Repeatedly removes the "worst" feature until
 * stopping criteria is met,
 */
public class SequentialBackwardSelection extends FeatureSelection {

    public SequentialBackwardSelection(String file, int classIndex) throws Exception {
        super(file, classIndex);
    }

    public SequentialBackwardSelection(String training, String testing, int classIndex) throws Exception {
        super(training, testing, classIndex);
    }

    @Override
    public Set<Integer> select(int maxNumFeatures) throws Exception {
        // While we have too many features or the accuracy is still improving
        return select((noImprovement, size) -> size > maxNumFeatures || noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS, maxNumFeatures);
    }

    @Override
    public Set<Integer> select() throws Exception {
        // While the accuracy is still improving
        return select((noImprovement, size) -> noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS);
    }

    /**
     * This method takes a criteria which just needs an evaluate method defined to work as the stopping criteria,
     * this allows us to reuse the general code for various stopping criterias.
     *
     * @param criteria
     * @return
     */
    public Set<Integer> select(Criteria criteria) throws Exception {
        // Just pass in the total number of features as the max number
        return select(criteria, getNumFeatures());
    }

    private Set<Integer> select(Criteria criteria, int maxNumFeatures) throws Exception {
        // To begin with all features are selected
        Set<Integer> selectedFeatures = getAllFeatureIndices();

        // Keep track of the best solution, so we never get worse
        double highestAccuracy = 0;
        Set<Integer> bestSoFar = new HashSet<>();
        double accuracy = objectiveFunction(selectedFeatures);
        double lastAccuracy = accuracy;

        printAccuracy(selectedFeatures.size(), accuracy);

        // Number of iterations with no improvement
        double iterationsWithoutImprovement = 0;

        while (criteria.evaluate(iterationsWithoutImprovement, selectedFeatures.size())) {
            int feature = worst(selectedFeatures);

            // No more valid features
            if (feature == -1) break;

            // Remove the feature so we do not keep selecting the same one
            selectedFeatures.remove(feature);

            accuracy = objectiveFunction(selectedFeatures);

            // If this is the highest so far, and also valid (i.e < number of features required)
            if ((greaterThan(accuracy, highestAccuracy) || (equalTo(accuracy, highestAccuracy) && selectedFeatures.size() < bestSoFar.size()))
                    && selectedFeatures.size() <= maxNumFeatures) {
                highestAccuracy = accuracy;
                // Make a copy, so we don't accidentally modify this
                bestSoFar = new HashSet<>(selectedFeatures);
            }

            if (lessThanOrEqualTo(accuracy, lastAccuracy)) {
                iterationsWithoutImprovement++;
            } else {
                iterationsWithoutImprovement = 0;
            }

            lastAccuracy = accuracy;

            printAccuracy(selectedFeatures.size(), accuracy);
        }

        return bestSoFar;
    }


}