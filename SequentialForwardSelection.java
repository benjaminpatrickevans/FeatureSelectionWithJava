import java.util.HashSet;
import java.util.Set;

/**
 * Created by ben on 8/04/17.
 */
public class SequentialForwardSelection<E> implements FeatureSelection<E> {

    public Set<E> select(final Set<E> original, int numFeatures){
        // Nothing we can do if the number of features is greater than or equal to the total size
        if (numFeatures >= original.size()){
            return original;
        }

        // Duplicate the input set, so we do not alter the original collection
        Set<E> remainingFeatures = new HashSet<E>(original);

        // Subset of only selected features
        Set<E> selectedFeatures = new HashSet<>();

        while (selectedFeatures.size() < numFeatures){
            E feature = max(selectedFeatures, remainingFeatures);

            // Remove the feature so we do not keep selecting the same one
            original.remove(feature);
        }

        return selectedFeatures;
    }

    public Set<E> select(final Set<E> original){
        // Duplicate the input set, so we do not alter the original collection
        Set<E> remainingFeatures = new HashSet<E>(original);
        // Finds the given amount of features
        return null;
    }

    /**
     * Returns the feature in remaining features
     * which maximises the objective function.
     *
     * @param selectedFeatures
     * @param remainingFeatures
     * @return
     */
    private E max(Set<E> selectedFeatures, Set<E> remainingFeatures){
        double highest = -1;
        E selected = null;

        for(E feature: remainingFeatures){
            double result = objectiveFunction(selectedFeatures, feature);
            if(result > highest){
                highest = result;
                selected = feature;
            }
        }

        return selected;
    }

    private double objectiveFunction(Set<E> selectedFeatures, E feature){
        return 0;
    }


}
