package selection;

import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Reads isntances from file and splits them into
 * <p>
 * - Training: Used to train the model (classifier)
 * - Validation: Used to check performance throughout, avoid overfitting to training
 * - Testing: Used only at the end to evaluate learnt performance
 * <p>
 * This uses weka: http://www.cs.waikato.ac.nz/ml/weka/
 * both for the classifier and instances.
 */
public class Classifier {

    // Data to learn model from
    Instances training;

    // Used to check performance of learnt model
    Instances validation;

    // Only used for final evaluation
    Instances testing;

    private int CLASS_INDEX;

    public Classifier(String fileName) throws Exception {
        Instances instances = readArffFile(fileName);
        instances.randomize(new java.util.Random(123));

        int trainSize = (int) Math.round(instances.numInstances() * 0.6);
        int validationSize = (int) Math.round(instances.numInstances() * 0.2);
        int testSize = instances.numInstances() - trainSize - validationSize;

        this.training = new Instances(instances, 0, trainSize);
        this.validation = new Instances(instances, trainSize, validationSize);
        this.testing = new Instances(instances, trainSize + validationSize, testSize);
    }

    public Classifier(String trainingFileName, String testingFileName) throws Exception {
        Instances instances = readArffFile(trainingFileName);
        instances.randomize(new java.util.Random(0));

        int trainSize = (int) Math.round(instances.numInstances() * 0.8);
        int validationSize = instances.numInstances() - trainSize;

        this.training = new Instances(instances, 0, trainSize);
        this.validation = new Instances(instances, trainSize, validationSize);
        this.testing = readArffFile(testingFileName);
    }

    public void removeAttribute(int index) throws Exception {
        this.training = removeAttribute(index, training);
        this.validation = removeAttribute(index, validation);
        this.testing = removeAttribute(index, testing);
    }

    private Instances removeAttribute(int index, Instances instances) throws Exception {
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(new int[]{index});
        remove.setInvertSelection(false);

        remove.setInputFormat(instances);
        return Filter.useFilter(instances, remove);
    }

    public void setClassIndex(int index) {
        this.CLASS_INDEX = index;

        training.setClassIndex(CLASS_INDEX);
        testing.setClassIndex(CLASS_INDEX);
        validation.setClassIndex(CLASS_INDEX);
    }

    /**
     * The method of classification to use
     *
     * @return
     * @throws Exception
     */
    private weka.classifiers.Classifier createClassifier() throws Exception {
        // Swap the lines out below out to use a different classifier
        IBk classifier = new IBk();
        classifier.setKNN(5);
        return classifier;
    }


    public double classify(Set<Integer> indices) throws Exception {
        weka.classifiers.Classifier classifier = createClassifier();

        Remove rm = new Remove();
        int[] remove = remove(indices);
        rm.setAttributeIndicesArray(remove);
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(rm);

        fc.setClassifier(classifier);
        fc.buildClassifier(training);

        return evaluateOnValidation(fc);
    }


    /**
     * Returns the accuracy on the testing set
     * using the training set to generate a classifier.
     *
     * @return
     * @throws Exception
     */
    public double testAccuracy() throws Exception {
        weka.classifiers.Classifier classifier = createClassifier();
        classifier.buildClassifier(training);
        return evaluateOnTesting(classifier);
    }

    /**
     * Returns the accuracy on the testing set
     * using the training set to generate a classifier.
     * Only uses the attributes specified in indices.
     *
     * @param indices
     * @return
     * @throws Exception
     */
    public double testAccuracy(Set<Integer> indices) throws Exception {
        weka.classifiers.Classifier classifier = createClassifier();

        // Remove all attributes not in indices
        Remove rm = new Remove();
        int[] remove = remove(indices);
        rm.setAttributeIndicesArray(remove);
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(rm);

        // Make the classifier
        fc.setClassifier(classifier);
        fc.buildClassifier(training);

        // Evaluate the classifier
        return evaluateOnTesting(fc);
    }

    /**
     * Returns the classification accuracy of the validation set
     * using the specified classifier with the training data
     *
     * @param classifier
     * @return
     * @throws Exception
     */
    private double evaluateOnValidation(weka.classifiers.Classifier classifier) throws Exception {
        Evaluation eval = new Evaluation(training);

        eval.evaluateModel(classifier, validation);
        return eval.pctCorrect();
    }

    private double evaluateOnTesting(weka.classifiers.Classifier classifier) throws Exception {
        Evaluation eval = new Evaluation(training);
        eval.evaluateModel(classifier, testing);
        return eval.pctCorrect();
    }

    /**
     * Helper function which returns an array of
     * all attributes to remove (the indices which are not
     * in toKeep)
     *
     * @param toKeep
     * @return
     */
    private int[] remove(Set<Integer> toKeep) {

        List<Integer> toRemove = new ArrayList<Integer>();

        for (int i = 0; i < training.numAttributes(); i++) {
            if (!toKeep.contains(i) && i != CLASS_INDEX) {
                toRemove.add(i);
            }
        }

        // Convert list to int[]
        return toRemove.stream().mapToInt(i -> i).toArray();
    }


    private Instances readArffFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(
                new FileReader("src/res/" + fileName));

        Instances instances = new Instances(reader);
        instances.setClassIndex(CLASS_INDEX);
        return instances;
    }

    public int getNumFeatures() {
        return training.numAttributes();
    }

    public Set<Integer> getAllFeatureIndices() {
        int totalFeatures = training.numAttributes();

        Set<Integer> features = IntStream.rangeClosed(0, totalFeatures - 1)
                .boxed().collect(Collectors.toSet());

        // Class shouldnt be considered a feature
        features.remove(CLASS_INDEX);

        // Return a set from 0..totalFeatures
        return features;
    }

}
