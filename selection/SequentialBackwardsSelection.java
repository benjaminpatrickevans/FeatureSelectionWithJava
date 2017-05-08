package selection;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ben on 18/04/17.
 */
public class SequentialBackwardsSelection extends FeatureSelection {

    public SequentialBackwardsSelection(Set<Instance> instances){
        super(instances);
    }

    public SequentialBackwardsSelection(Set<Instance> training, Set<Instance> testing){
        super(training, testing);
    }

    @Override
    public Set<Integer> select(int maxNumFeatures) {
        // While we have too many features or the accuracy is still improving
        return select((noImprovement, size) -> size > maxNumFeatures || noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS, maxNumFeatures);
    }

    @Override
    public Set<Integer> select() {
        // While the accuracy is still improving
        return select((noImprovement, size) -> noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS);
    }


    public Set<Integer> select(Criteria criteria) {
        return select(criteria, trainingInstances.size());
    }


    /**
     * This method takes a criteria which just needs an evaluate method defined to work as the stopping criteria,
     * this allows us to reuse the general code for various stopping criterias.
     * @param criteria
     * @param maxNumFeatures
     * @return
     */
    public Set<Integer> select(Criteria criteria, int maxNumFeatures) {
        // In this case we have no data to use, so return the empty set
        if (trainingInstances == null || trainingInstances.isEmpty()) return new HashSet<Integer>();

        // To begin with all features are selected
        Set<Integer> selectedFeatures = getAllFeatureIndices();

        // Keep track of the best solution, so we never get worse
        double highestAccuracy = 0;
        Set<Integer> bestSoFar = new HashSet<>();
        double accuracy = objectiveFunction(selectedFeatures);
        double lastAccuracy = accuracy;

        // Number of iterations with no improvement
        double noImprovement = 0;

        while (criteria.evaluate(noImprovement, selectedFeatures.size())){
            int feature = worst(selectedFeatures);

            // No more valid features
            if (feature == -1) break;

            // Remove the feature so we do not keep selecting the same one
            selectedFeatures.remove(feature);

            accuracy = objectiveFunction(selectedFeatures);

            // If this is the highest so far, and also valid (i.e < number of features required)
            if ((accuracy > highestAccuracy || (accuracy == highestAccuracy && selectedFeatures.size() < bestSoFar.size()))
                    && selectedFeatures.size() <= maxNumFeatures) {
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