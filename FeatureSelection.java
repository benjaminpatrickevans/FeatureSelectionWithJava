import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ben on 8/04/17.
 */
public abstract class FeatureSelection {

    private Classifier classifier;
    protected Set<Instance> instances;

    // The number of times to try extra subsets if no improvement is made
    protected final int MAX_ITERATIONS_WITHOUT_PROGRESS = 5;

    public FeatureSelection(Set<Instance> instances){
        this.instances = instances;
        this.classifier = new Classifier(instances);
    }

    /**
        Returns a subset of only the most important features,
     the parameter specifies the maximum number of features to select (m).
     If a number of features is found (n), where n < m, with a higher accuracy,
     this set will be returned instead.
     */
   public abstract Set<Integer> select(int maxNumFeatures);

   /**
   Returns a subset containing only the numFeatures most important
    features. If numFeatures is >= original.size(), the original
    set is returned.
    */
   public abstract Set<Integer> select();

    /**
     * Returns the feature in remaining features
     * which maximises the objective function.
     *
     * @param selectedFeatures
     * @param remainingFeatures
     * @return
     */
    protected int best(Set<Integer> selectedFeatures, Set<Integer> remainingFeatures){
        double highest = -Integer.MAX_VALUE;
        int selected = -1;

        for(int feature: remainingFeatures){
            Set<Integer> newFeatures = new HashSet<>(selectedFeatures);
            newFeatures.add(feature);

            double result = objectiveFunction(newFeatures);
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
    protected int worst(Set<Integer> features){
        double highestAccuracy = -Integer.MAX_VALUE;
        int selected = -1;

        for(int feature: features){
            Set<Integer> newFeatures = new HashSet<>(features);
            newFeatures.remove(feature);

            double result = objectiveFunction(newFeatures);
            if(result > highestAccuracy){
                highestAccuracy = result;
                selected = feature;
            }
        }

        return selected;
    }

    protected double objectiveFunction(Set<Integer> selectedFeatures) {
        return classifier.classify(selectedFeatures);
    }

    protected Set<Integer> getAllFeatureIndices(){
        // Extract an instance to check the amount of features, assumes all instances have same # of features
        Instance sampleInstance = instances.iterator().next();
        int totalFeatures = sampleInstance.getNumFeatures();

        // To begin with all features are selected
        return IntStream.rangeClosed(0, totalFeatures - 1)
                .boxed().collect(Collectors.toSet());
    }

    public void compareTestingAccuracy(Set<Integer> selectedIndices) {
        System.out.println("Classification accuracy on testing set using all features: " + classifier.classify());
        System.out.println("Classification accuracy on testing set using features " + selectedIndices + ": " + classifier.classify(selectedIndices));
    }


}
