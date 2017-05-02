import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ben on 8/04/17.
 */
public abstract class FeatureSelection {


    /**
    Returns a subset of only the most important features
    chosen by some measure.
     */
   public abstract Set<Integer> select(Set<Instance> instances, int numFeaturesToSelect);

   /**
   Returns a subset containing only the numFeatures most important
    features. If numFeatures is >= original.size(), the original
    set is returned.
    */
   public abstract Set<Integer> select(Set<Instance> instances, double goalAccuracy);

    /**
     * Returns the feature in remaining features
     * which maximises the objective function.
     *
     * @param selectedFeatures
     * @param remainingFeatures
     * @return
     */
    protected int best(Classifier classifier, Set<Integer> selectedFeatures, Set<Integer> remainingFeatures){
        double highest = -Integer.MAX_VALUE;
        int selected = -1;

        for(int feature: remainingFeatures){
            Set<Integer> newFeatures = new HashSet<>(selectedFeatures);
            newFeatures.add(feature);

            double result = objectiveFunction(classifier, newFeatures);
            if(result > highest){
                highest = result;
                selected = feature;
            }
        }

        return selected;
    }

    /**
     * Finds and returns the index of the worst feature,
     * where worst is defined by
     * @param instances
     * @param features
     * @return
     */
    protected int worst(Classifier classifier, Set<Integer> features){
        double lowestAccuracy = Integer.MAX_VALUE;
        int selected = -1;

        for(int feature: features){
            Set<Integer> newFeatures = new HashSet<>(features);
            newFeatures.remove(feature);

            double result = objectiveFunction(classifier, newFeatures);
            if(result < lowestAccuracy){
                lowestAccuracy = result;
                selected = feature;
            }
        }

        return selected;
    }

    public void compareAccuracy(Classifier classifier, Set<Integer> selectedIndices, int totalFeatures) {
        Set<Integer> allIndices = IntStream.rangeClosed(0, totalFeatures - 1)
                .boxed().collect(Collectors.toSet());
        System.out.println("Classification accuracy on testing set using all features: " + classifier.classify(allIndices));
        System.out.println("Classification accuracy on testing set using features " + selectedIndices + ": " + classifier.classify(selectedIndices));
    }

    protected double objectiveFunction(Classifier classifier, Set<Integer> selectedFeatures) {
        return classifier.classify(selectedFeatures);
    }


}
