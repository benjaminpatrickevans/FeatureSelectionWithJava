package clasification;

import weka.classifiers.*;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.pmml.consumer.SupportVectorMachineModel;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ben on 15/05/17.
 */
public class WekaClassifier {

    // Data to learn model from
    Instances training;

    // Used to check performance of learnt model
    Instances validation;

    // Only used for final evaluation
    Instances testing;

    private int CLASS_INDEX;
    private final int NUM_FOLDS = 3;

    public WekaClassifier (String fileName) throws Exception {
        Instances instances =  readArffFile(fileName);

        instances.randomize(new java.util.Random(0));

        int trainSize = (int) Math.round(instances.numInstances() * 0.6);
        int validationSize = (int) Math.round(instances.numInstances() * 0.2);
        int testSize = instances.numInstances() - trainSize - validationSize;

        this.training = new Instances(instances, 0, trainSize);
        this.validation = new Instances(instances, trainSize, validationSize);
        this.testing = new Instances(instances, trainSize + validationSize, testSize);

        setClassIndex();

        classify();
    }

    public WekaClassifier (String trainingFileName, String testingFileName) throws Exception {

        Instances instances =  readArffFile(trainingFileName);

        // Split into training and validation
        this.training = readArffFile(trainingFileName);

        instances.randomize(new java.util.Random(0));

        int trainSize = (int) Math.round(instances.numInstances() * 0.8);
        int validationSize = instances.numInstances() - trainSize;

        this.training = new Instances(instances, 0, trainSize);
        this.validation = new Instances(instances, trainSize, validationSize);


        this.testing = readArffFile(testingFileName);

        setClassIndex();

        classify();
    }

    public void setClassIndex(){
        // this.CLASS_INDEX = training.numAttributes() - 1;
        this.CLASS_INDEX = 0;
        training.setClassIndex(CLASS_INDEX);
        testing.setClassIndex(CLASS_INDEX);

    }

    public static void main(String[] args) throws Exception {
        new WekaClassifier("wine.arff");
    }

    private Instances readArffFile (String fileName) throws IOException{
        BufferedReader reader = new BufferedReader(
                new FileReader("src/res/" + fileName));

        Instances instances = new Instances(reader);

        instances.setClassIndex(CLASS_INDEX);

        return instances;
    }

    public double classify() throws Exception {

        // classifier
        weka.classifiers.Classifier classifier = createClassifier();

        classifier.buildClassifier(training);
        Evaluation eval = new Evaluation(training);

        return evaluate(classifier);
    }

    public double testAccuracy() throws Exception {
        // classifier
        weka.classifiers.Classifier classifier = createClassifier();

        classifier.buildClassifier(training);
        Evaluation eval = new Evaluation(training);

        eval.evaluateModel(classifier, testing);

        System.out.println(eval.toSummaryString());

        return eval.pctCorrect();
    }

    private weka.classifiers.Classifier createClassifier() throws Exception {
        return new NaiveBayes();
    }

    public double testAccuracy(Set<Integer> indices) throws Exception {
        weka.classifiers.Classifier classifier = createClassifier();

        Remove rm = new Remove();

        int[] remove = remove(indices);
        rm.setAttributeIndicesArray(remove);

        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(rm);

        fc.setClassifier(classifier);
        fc.buildClassifier(training);

        Evaluation eval = new Evaluation(training);
        eval.evaluateModel(fc, testing);

        return eval.pctCorrect();
    }

    private double evaluate(weka.classifiers.Classifier classifier) throws Exception {
        Evaluation eval = new Evaluation(training);

        eval.evaluateModel(classifier, validation);
        // eval.crossValidateModel(classifier, training, NUM_FOLDS, new Random(1));

        return eval.pctCorrect();
    }

    private int[] remove(Set<Integer> toKeep){

        List<Integer> toRemove = new ArrayList<Integer>();

        for(int i=0; i<training.numAttributes(); i++){
            if (!toKeep.contains(i) && i != CLASS_INDEX){
                toRemove.add(i);
            }
        }

        // Convert list to int[]
        return toRemove.stream().mapToInt(i->i).toArray();
    }

    public double classify(Set<Integer> indices) throws Exception {
        weka.classifiers.Classifier classifier = createClassifier();

        Remove rm = new Remove();

        int[] remove = remove(indices);
        rm.setAttributeIndicesArray(remove);

        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(rm);

        fc.setClassifier(classifier);
        // train and make predictions
        fc.buildClassifier(training);

        return evaluate(fc);
    }

    public int getNumFeatures(){
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
