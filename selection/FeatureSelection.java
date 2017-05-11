package selection;

import clasification.Classifier;
import clasification.KNearestNeighbour;
import helper.Normalize;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Top level class for feature selection, holds the classifier
 * to use and has some helper functions such as best and worst instances.
 */
public abstract class FeatureSelection {

    // The number of iterations to try if no improvement is made
    protected final int MAX_ITERATIONS_WITHOUT_PROGRESS = 5;
    // Training instances to be used in evaluation
    protected Set<Instance> trainingInstances = new HashSet<Instance>();
    // The classifier to use
    private Classifier classifier;
    // Testing instances only ever used to check performance of learnt features
    private Set<Instance> testingInstances = new HashSet<Instance>();

    /**
     * If only a single set is provided, the attributes of each instance
     * will be scaled and then instances split 70:30 into training:testing.
     *
     * @param instances
     */
    public FeatureSelection(Set<Instance> instances) {
        int trainingSize = (int) (instances.size() * 0.7);

        int count = 0;
        for (Instance instance : instances) {
            if (count++ < trainingSize) trainingInstances.add(instance);
            else testingInstances.add(instance);
        }

        Normalize.normalize(trainingInstances, testingInstances);

        // Only use the training instances for the classifier, avoid bias!
        this.classifier = new KNearestNeighbour(trainingInstances);
    }

    public FeatureSelection(Set<Instance> trainingInstances, Set<Instance> testingInstances) {
        this.trainingInstances = trainingInstances;
        this.testingInstances = testingInstances;

        Normalize.normalize(trainingInstances, testingInstances);

        // Only use the training instances for the classifier, avoid bias!
        this.classifier = new KNearestNeighbour(trainingInstances);
    }

    /**
     * Returns a subset of only the most important features,
     * the parameter specifies the maximum number of features to select (m).
     * If a set of features is found with size n, where n < m, with a higher accuracy,
     * this set will be returned instead.
     *
     * @param maxNumFeatures
     * @return
     */
    public abstract Set<Integer> select(int maxNumFeatures);

    /**
     * Returns the most accurate subset of features found. Continues until no improvement
     * is made (i.e. numIterations <= MAX_ITERATIONS_WITHOUT_PROGRESS)
     *
     * @return
     */
    public abstract Set<Integer> select();

    /**
     * Returns the feature in the remaining set of features
     * which when added to the selectedFeatures maximises the objective function.
     *
     * @param selectedFeatures
     * @param remainingFeatures
     * @return
     */
    protected int best(Set<Integer> selectedFeatures, Set<Integer> remainingFeatures) {
        double highest = -Integer.MAX_VALUE;
        int selected = -1;

        for (int feature : remainingFeatures) {
            Set<Integer> newFeatures = new HashSet<>(selectedFeatures);
            newFeatures.add(feature);

            double result = objectiveFunction(newFeatures);
            if (result > highest) {
                highest = result;
                selected = feature;
            }
        }

        return selected;
    }

    /**
     * Finds and returns the index of the worst feature,
     * where worst is defined by the feature whose removal results in the
     * highest classification accuracy (i.e. an irrelevant or redundant feature)
     *
     * @param features
     * @return
     */
    protected int worst(Set<Integer> features) {
        double highestAccuracy = -Integer.MAX_VALUE;
        int selected = -1;

        for (int feature : features) {
            Set<Integer> newFeatures = new HashSet<>(features);
            newFeatures.remove(feature);

            double result = objectiveFunction(newFeatures);
            if (result > highestAccuracy) {
                highestAccuracy = result;
                selected = feature;
            }
        }

        return selected;
    }

    protected double objectiveFunction(Set<Integer> selectedFeatures) {
        return classifier.classify(selectedFeatures);
    }

    protected int getNumFeatures(){
        Instance sampleInstance = trainingInstances.iterator().next();
        return sampleInstance.getNumFeatures();
    }

    protected Set<Integer> getAllFeatureIndices() {
        int totalFeatures = getNumFeatures();

        // Return a set from 0..totalFeatures
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
        System.out.printf("Accuracy using all features: %.3f%%\n", testingClassifier.classify() * 100);
        System.out.printf("Accuracy using features (%s): %.3f%%\n", selectedIndices, testingClassifier.classify(selectedIndices) * 100);
    }

}
