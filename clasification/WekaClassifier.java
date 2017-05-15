package clasification;

import weka.classifiers.*;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ben on 15/05/17.
 */
public class WekaClassifier {

    Instances training;
    Instances testing;

    public final int CLASS_INDEX = 0;

    // classifier
    weka.classifiers.Classifier classifier = new NaiveBayes();

    public WekaClassifier (String fileName) throws Exception {
        Instances instances =  readArffFile(fileName);

        int trainSize = (int) Math.round(instances.numInstances() * 0.7);
        int testSize = instances.numInstances() - trainSize;

        this.training = new Instances(instances, 0, trainSize);
        this.testing = new Instances(instances, trainSize, testSize);

        System.out.println("Training instances: " + training.size());
        System.out.println("Testing instances: " + testing.size());

        classify();
    }

    public WekaClassifier (String trainingFileName, String testingFileName) throws Exception {
        this.training = readArffFile(trainingFileName);
        this.testing = readArffFile(testingFileName);

        classify();
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
        classifier.buildClassifier(training);
        Evaluation eval = new Evaluation(training);

        eval.evaluateModel(classifier, training);

        System.out.println(eval.pctCorrect());
        return eval.pctCorrect();
    }

    private int[] remove(Set<Integer> toKeep){

        List<Integer> toRemove = new ArrayList<Integer>();

        for(int i=0; i<training.numAttributes(); i++){
            if (!toKeep.contains(i)){
                toRemove.add(i);
            }
        }

        System.out.println("Removing features: " + toRemove);

        // Convert list to int[]
        return toRemove.stream().mapToInt(i->i).toArray();
    }

    public double classify(Set<Integer> indices) throws Exception {
        Remove rm = new Remove();

        int[] remove = remove(indices);
        rm.setAttributeIndicesArray(remove);

        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(rm);

        fc.setClassifier(classifier);
        // train and make predictions
        fc.buildClassifier(training);

        Evaluation eval = new Evaluation(training);
        eval.evaluateModel(fc, training);

        System.out.println(eval.pctCorrect());

        return eval.pctCorrect();
    }

}
