package selection;

import clasification.Classifier;
import clasification.KNearestNeighbour;
import clasification.WekaClassifier;
import helper.Normalize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Top level class for feature selection, holds the classifier
 * to use and has some helper functions such as best and worst instances.
 */
public abstract class FeatureSelection {

    // The number of iterations to try if no improvement is made
    protected final int MAX_ITERATIONS_WITHOUT_PROGRESS = 300;

    // Training instances to be used in evaluation
    protected List<Instance> trainingInstances = new ArrayList<>();
    // The classifier to use
    private WekaClassifier classifier;
    // Testing instances only ever used to check performance of learnt features
    private List<Instance> testingInstances = new ArrayList<Instance>();

    /**
     * If only a single set is provided, the attributes of each instance
     * will be scaled and then instances split 70:30 into training:testing.
     *
     * @param instances
     */
    /*public FeatureSelection(List<Instance> instances) {
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

    public FeatureSelection(List<Instance> trainingInstances, List<Instance> testingInstances) {
        this.trainingInstances = trainingInstances;
        this.testingInstances = testingInstances;

        Normalize.normalize(trainingInstances, testingInstances);

        // Only use the training instances for the classifier, avoid bias!
        this.classifier = new KNearestNeighbour(trainingInstances);
    }*/

    public FeatureSelection (String fileName) throws Exception {
        this.classifier = new WekaClassifier(fileName);
    }

    public FeatureSelection (String trainingFile, String testingFile) throws Exception {
        this.classifier = new WekaClassifier(trainingFile, testingFile);
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
    public abstract Set<Integer> select(int maxNumFeatures) throws Exception;

    /**
     * Returns the most accurate subset of features found. Continues until no improvement
     * is made (i.e. numIterations <= MAX_ITERATIONS_WITHOUT_PROGRESS)
     *
     * @return
     */
    public abstract Set<Integer> select() throws Exception;

    /**
     * Returns the feature in the remaining set of features
     * which when added to the selectedFeatures maximises the objective function.
     *
     * @param selectedFeatures
     * @param remainingFeatures
     * @return
     */
    protected int best(Set<Integer> selectedFeatures, Set<Integer> remainingFeatures) throws Exception {
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
    protected int worst(Set<Integer> features) throws Exception {
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

    protected double objectiveFunction(Set<Integer> selectedFeatures) throws Exception {
        return classifier.classify(selectedFeatures);
    }
    protected int getNumFeatures() {
        return classifier.getNumFeatures();
    }

    protected Set<Integer> getAllFeatureIndices() {
        return classifier.getAllFeatureIndices();
    }

    /***
     * Uses the testing instances to check the performance
     * of the selected indices, prints a summary of the results.
     * @param selectedIndices
     */
    public void compareTestingAccuracy(Set<Integer> selectedIndices) throws Exception {
        //Classifier testingClassifier = new KNearestNeighbour(testingInstances);
        System.out.printf("Accuracy using all features: %.3f%%\n", classifier.testAccuracy());
        System.out.printf("Accuracy using features (%s): %.3f%%\n", selectedIndices, classifier.testAccuracy(selectedIndices));
    }

}
