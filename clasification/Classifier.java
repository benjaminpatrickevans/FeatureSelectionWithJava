package clasification;

import selection.Instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classifier to use as the evaluation criteria
 * for feature selection. Abstract class so classifiers
 * can easily be added
 */
public abstract class Classifier {

    // Split the data into training and testing to prevent overfitting
    List<Instance> training = new ArrayList<Instance>();
    List<Instance> testing = new ArrayList<Instance>();

    /**
     * If only a single set of instances is specified,
     * split this set 70:30 into training:testing.
     *
     * @param instances
     */
    public Classifier(List<Instance> instances) {
        int trainingSize = (int) (instances.size() * 0.7);

        int count = 0;
        for (Instance instance : instances) {
            if (count++ < trainingSize) training.add(instance);
            else testing.add(instance);
        }
    }

    /**
     * Classifies and returns the percentage
     * of correct classifications. Uses every feature
     * available in the trainingInstances.
     */
    public abstract double classify();

    /**
     * Classifies and returns the percentage
     * of correct classifications. Only uses the specified indices
     * for comparing trainingInstances.
     */
    public abstract double classify(Set<Integer> indices);

}
