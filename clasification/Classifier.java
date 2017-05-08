package clasification;

import selection.Instance;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Classifier to use as the evaluation criteria
 * for feature selection.
 */
public abstract class Classifier {

    // Split the data
    Set<Instance> training = new HashSet<Instance>();
    Set<Instance> testing = new HashSet<Instance>();

    public Classifier(Set<Instance> instances) {
        // 70:30 training testing split
        int trainingSize = (int)(instances.size()*0.7);

        int count = 0;
        for(Instance instance: instances){
            if(count++ < trainingSize) training.add(instance);
            else testing.add(instance);
        }
    }

    /**
     * Classifies and returns the percentage
     * of correct classification. Uses every feature
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
