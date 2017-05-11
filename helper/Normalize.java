package helper;

import selection.Instance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ben on 11/05/17.
 */
public class Normalize {

    /**
     * Scale the attributes to be between 0-1
     *
     * @param trainingInstances
     * @param testingInstances
     */
    public static void normalize(Set<Instance> trainingInstances, Set<Instance> testingInstances) {
        Map<Integer, Values> featureValues = new HashMap<Integer, Values>();

        // First run through the training data getting min and max of all features
        for (Instance instance : trainingInstances) {
            double[] features = instance.getFeatures();

            for (int feature = 0; feature < features.length; feature++) {
                double val = instance.getFeature(feature);

                // If we havent yet seen this feature, add this as our min and max
                if (!featureValues.containsKey(feature)) {
                    featureValues.put(feature, new Values(val, val));
                } else {
                    // Otherwise compare it to our current min and max
                    Values current = featureValues.get(feature);
                    if (val > current.getMax()) {
                        current.setMax(val);
                    }
                    if (val < current.getMin()) {
                        current.setMin(val);
                    }
                }
            }
        }

        // Now scale training and testing using the min and max from above
        scale(trainingInstances, featureValues);
        scale(testingInstances, featureValues);
    }

    public static void scale(Set<Instance> instances, Map<Integer, Values> featureValues) {

        // Now scale all our training data
        for (Instance instance : instances) {
            double[] features = instance.getFeatures();

            for (int feature = 0; feature < features.length; feature++) {
                double minValue = featureValues.get(feature).getMin();
                double maxValue = featureValues.get(feature).getMax();

                instance.scaleFeature(feature, minValue, maxValue);
            }
        }

    }

    // Inner class to store the min and max for each feature, used for normalizing
    private static class Values {
        private double min;
        private double max;

        public Values(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        @Override
        public String toString() {
            return "Min: " + min + ", Max: " + max;
        }
    }


}
