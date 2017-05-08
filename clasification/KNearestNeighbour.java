package clasification;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import selection.Instance;


/**
 * Classifier to use as the evaluation criteria
 * for feature selection. K-Nearest neighbour is
 * implemented here due to its simplicity.
 */
public class KNearestNeighbour extends Classifier {

    // How many neighbors to find
    private final int K = 3;

    public KNearestNeighbour(Set<Instance> instances) {
        super(instances);

    }

    /**
     * Classifies and returns the percentage
     * of correct classification. Uses every feature
     * available in the trainingInstances.
     */
    public double classify(){
        Instance sampleInstance = training.iterator().next();
        int totalFeatures = sampleInstance.getNumFeatures();

        // We are using all features
        Set<Integer> allIndices =  IntStream.rangeClosed(0, totalFeatures - 1)
                .boxed().collect(Collectors.toSet());

        return classify(allIndices);
    }
    /**
     * Classifies and returns the percentage
     * of correct classifications. Only uses the specified indices
     * for comparing trainingInstances.
     */
    public double classify(Set<Integer> indices) {
        int correct = 0;

        for (Instance instance : testing) {
            //Keep neighbours in queue, to get the K closest neighbours quickly
            PriorityQueue<Result> neighbours = new PriorityQueue<Result>(new Comparator<Result>() {
                @Override
                public int compare(Result a, Result b) {
                    return a.DISTANCE < b.DISTANCE ? -1 : a.DISTANCE > b.DISTANCE ? 1 : 0;
                }
            });

            // Find the distance to every neighbor in training set
            for (Instance other : training) {
                Result result = new Result(other.getLabel(), instance.distanceTo(other, indices));
                neighbours.add(result);
            }

            // Take the K closest neighbours
            Result[] closestNeighbours = new Result[K];
            for(int i=0; i < K; i++){
                closestNeighbours[i] = neighbours.poll();
            }

            // Find the most common class among these K neighbours
            String type = mostCommonType(closestNeighbours);

            // If we correctly classified the instance
            if(type.equals(instance.getLabel())){
                correct++;
            }
        }

        // Return the percentage of correct classifications
        return correct/(double)testing.size();
    }


    /**
     * Returns the mode of @param list
     * @param list
     * @return
     */
    private String mostCommonType(Result[] list) {
        HashMap<String, Integer> occurences = new HashMap<String, Integer>();

        int maxFrequency = 0;
        String mostFrequent = null;

        for (Result r : list) {
            String type = r.CLASS;

            // If we have not seen this type yet, add it
            if (!occurences.containsKey(type)) {
                occurences.put(type, 0);
            }

            // Increment the frequency by 1
            int frequency = occurences.get(type) + 1;
            occurences.put(type, frequency);

            // Check if we have a new most frequent type
            if (frequency > maxFrequency) {
                mostFrequent = type;
                maxFrequency = frequency;
            }

        }


        return mostFrequent;

    }

    /**
     * Wrapper class to hold the outcomes
     * of K-NN.
     */
    private class Result {

        public final String CLASS;
        public final double DISTANCE;

        public Result(String type, double distance) {
            this.CLASS = type;
            this.DISTANCE = distance;
        }

        @Override
        public String toString() {
            return DISTANCE + " " + CLASS;
        }
    }

}
