package clasification;

import selection.Instance;

import java.util.HashSet;
import java.util.Set;

/**
 * Classifier to use as the evaluation criteria
 * for feature selection. Abstract class so classifiers
 * can easily be added
 */
public abstract class Classifier {

    // Split the data into training and testing to prevent overfitting
    Set<Instance> training = new HashSet<Instance>();
    Set<Instance> testing = new HashSet<Instance>();

    /**
     * If only a single set of instances is specified,
     * split this set 70:30 into training:testing.
     *
     * @param instances
     */
    public Classifier(Set<Instance> instances) {
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
