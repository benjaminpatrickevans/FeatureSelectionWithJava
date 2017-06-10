package selection;

import java.util.HashSet;
import java.util.Set;

/**
 * Top level class for feature selection, holds the classifier
 * to use and has some helper functions such as selecting the
 * best and worst instances. This is an abstract class, so
 * should be extended to add a feature selection method -
 * it can not be instantiated.
 */
public abstract class FeatureSelection {

    // The number of iterations to try if no improvement is made
    protected final int MAX_ITERATIONS_WITHOUT_PROGRESS;

    private final boolean DEBUG = true;

    // The wrapped classifier to use
    private Classifier classifier;

    /**
     * Creates a new FeatureSelection instance and classifier,
     * splits the specified file into training:testing:validation
     * sets for  use with the classifier.
     *
     * @param fileName The instances to read
     * @param maxIterationsWithoutProgress Max iterations to try if no improvement is made
     * @throws Exception
     */
    public FeatureSelection(String fileName, int maxIterationsWithoutProgress) throws Exception {
        this.classifier = new Classifier(fileName);
        this.MAX_ITERATIONS_WITHOUT_PROGRESS = maxIterationsWithoutProgress;
    }

    /**
     * Creates a new FeatureSelection instance and classifier,
     * splits the specified training file into training:validation sets
     * and uses the testing file for testing the classifier.
     *
     * @param trainingFile The instances to read
     * @param testingFile The instances to read
     * @param classIndex The feature to mark as the class
     * @param maxIterationsWithoutProgress Max iterations to try if no improvement is made
     * @throws Exception
     */
    public FeatureSelection(String trainingFile, String testingFile, int maxIterationsWithoutProgress) throws Exception {
        this.classifier = new Classifier(trainingFile, testingFile);
        this.MAX_ITERATIONS_WITHOUT_PROGRESS = maxIterationsWithoutProgress;
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
     * Returns the index of the "best" feature in the remaining set of features,
     * ie the feature which when added to the selectedFeatures maximises
     * the objective function.
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
     * Finds and returns the index of the "worst" feature,
     * where worst is defined by the feature whose removal results in the
     * highest classification accuracy (i.e. an irrelevant or redundant feature)
     *
     * @param selectedFeatures
     * @return
     */
    protected int worst(Set<Integer> selectedFeatures) throws Exception {
        double highestAccuracy = -Integer.MAX_VALUE;
        int selected = -1;

        for (int feature : selectedFeatures) {
            Set<Integer> newFeatures = new HashSet<>(selectedFeatures);
            newFeatures.remove(feature);

            double result = objectiveFunction(newFeatures);
            if (result > highestAccuracy) {
                highestAccuracy = result;
                selected = feature;
            }
        }

        return selected;
    }


    /**
     * Returns the classification accuracy on the validation
     * set using the specified features
     *
     * @param selectedFeatures
     * @return
     * @throws Exception
     */
    protected double objectiveFunction(Set<Integer> selectedFeatures) throws Exception {
        return classifier.classify(selectedFeatures);
    }

    /**
     * Removes the specified attribute, this is useful if the dataset
     * has extra "information" variables that give away the class.
     * Need to also specify the class index since it may change.
     *
     * @param index
     * @throws Exception
     */
    public void removeAttribute(int index) throws Exception {
        this.classifier.removeAttribute(index);
    }


    /***
     * Uses the testing instances to check the performance
     * of the selected indices, prints a summary of the results.
     * @param selectedIndices
     */
    public void compareTestingAccuracy(Set<Integer> selectedIndices) throws Exception {
        System.out.printf("Accuracy using all features: %.3f%%\n", classifier.testAccuracy());
        System.out.printf("Accuracy using features (%s): %.3f%%\n", selectedIndices, classifier.testAccuracy(selectedIndices));
    }


    /**
     * Prints out the size and accuracy at each iteration of selection.
     * Only prints if DEBUG is set to true, does nothing otherwise.
     *
     * @param size
     * @param accuracy
     */
    protected void printAccuracy(int size, double accuracy) {
        if (DEBUG) System.out.println(size + ": " + accuracy);
    }

    /***
     * ===============
     * GETTERS / SETTERS
     * ===============
     */

    protected int getNumFeatures() {
        return classifier.getNumFeatures();
    }

    protected Set<Integer> getAllFeatureIndices() {
        return classifier.getAllFeatureIndices();
    }


    /***
     * ===============
     * HELPER METHODS FOR DEALING WITH FLOATING POINT PRECISION EASILY
     * ===============
     */

    protected boolean greaterThan(double d1, double d2) {
        return Double.compare(d1, d2) > 0;
    }

    protected boolean lessThan(double d1, double d2) {
        return Double.compare(d1, d2) < 0;
    }

    protected boolean lessThanOrEqualTo(double d1, double d2) {
        return Double.compare(d1, d2) <= 0;
    }

    protected boolean equalTo(double d1, double d2) {
        return Double.compare(d1, d2) == 0;
    }


    /**
     * This interface is used to specify the stopping criteria.
     * Has one method (evaluate) which is passed to the selection methods,
     * this allows us to use lambdas to pass a custom evaluation function
     * for example (numIterations, size) -> numIterations < MAX_ITERATIONS_WITHOUT_PROGRESS.
     */

    protected interface Criteria {

        /***
         * Returns true if the custom specified criteria is true,
         * otherwise false.
         *
         * @param numIterations
         * @param size
         * @return
         */
        boolean evaluate(double numIterations, int size);
    }

}
