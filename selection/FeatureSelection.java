package selection;

import clasification.Classifier;
import clasification.KNearestNeighbour;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ben on 8/04/17.
 */
public abstract class FeatureSelection {

    // The classifier to use, in this case K-NN
    private Classifier classifier;

    // Training instances to be used in evaluation
    protected Set<Instance> trainingInstances = new HashSet<Instance>();

    // Testing instances only ever used to check performance of learnt features
    private Set<Instance> testingInstances = new HashSet<Instance>();

    // The number of iterations to try if no improvement is made
    protected final int MAX_ITERATIONS_WITHOUT_PROGRESS = 5;

    public FeatureSelection(Set<Instance> instances){
        // Split the data 70:30 into training and testing sets
        int trainingSize = (int)(instances.size()*0.7);

        int count = 0;
        for (Instance instance: instances){
            if(count++ < trainingSize) trainingInstances.add(instance);
            else testingInstances.add(instance);
        }

        // Only use the training instances for the classifier, avoid bias!
        this.classifier = new KNearestNeighbour(trainingInstances);
    }

    public FeatureSelection(Set<Instance> trainingInstances, Set<Instance> testingInstances){
        this.trainingInstances = trainingInstances;
        this.testingInstances = testingInstances;

        // Only use the training instances for the classifier, avoid bias!
        this.classifier = new KNearestNeighbour(trainingInstances);
    }

    /***
     Returns a subset of only the most important features,
     the parameter specifies the maximum number of features to select (m).
     If a number of features is found (n), where n < m, with a higher accuracy,
     this set will be returned instead.
     * @param maxNumFeatures
     * @return
     */
   public abstract Set<Integer> select(int maxNumFeatures);

    /**
     * Returns the most accurate subset found. Continues until no improvement
     * is made (i.e. numIterations <= MAX_ITERATIONS_WITHOUT_PROGRESS)
     * @return
     */
   public abstract Set<Integer> select();

    /**
     * Returns the feature in the remaining set of features
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
     * where worst is defined by the features whose removal results in the
     * highest classification accuracy (i.e. an irrelevant or redundant feature)
     *
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
        // Extract an instance to check the amount of features, assumes all trainingInstances have same # of features
        Instance sampleInstance = trainingInstances.iterator().next();
        int totalFeatures = sampleInstance.getNumFeatures();

        // To begin with all features are selected
        return IntStream.rangeClosed(0, totalFeatures - 1)
                .boxed().collect(Collectors.toSet());
    }

    /***
     * Uses the testing instances to check the performance
     * of the selected indices, prints a summary of the results.
     * @param selectedIndices
     */
    public void compareTestingAccuracy(Set<Integer> selectedIndices) {
        Classifier testingClassifier = new KNearestNeighbour(testingInstances);
        System.out.println("Classification accuracy on testing set using all features: " + testingClassifier.classify());
        System.out.println("Classification accuracy on testing set using features " + selectedIndices + ": " + testingClassifier.classify(selectedIndices));
    }


}