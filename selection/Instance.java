package selection;

import java.util.Set;

/**
 * Represents an Instance/Example.
 * Has an array of features and a class albel.
 */
public class Instance {

    private double[] features;

    // The class label for the instance, optional
    private String label;

    public Instance(double[] features) {
        this.features = features;
    }

    public Instance(double[] features, String label) {
        this.features = features;
        this.label = label;
    }

    public double[] getFeatures() {
        return features;
    }

    public int getNumFeatures() {
        return features.length;
    }

    public String getLabel() {
        return label;
    }

    public double getFeature(int index) {
        return features[index];
    }

    /**
     * Scales the feature using the formula (currentValue - min) / (max - min)
     *
     * @param feature
     * @param min
     * @param max
     */
    public void scaleFeature(int feature, double min, double max) {
        double val = features[feature];
        features[feature] = (val - min) / (max - min);
    }

    /**
     * Distance measure to use when comparing instances with K-NN
     *
     * @param other
     * @param indices
     * @return
     */
    public double distanceTo(Instance other, Set<Integer> indices) {
        if (getNumFeatures() != other.getNumFeatures())
            throw new IllegalArgumentException("Number of features do not match");

        double diff = 0;
        for (Integer index : indices) {
            double feature = features[index];
            double otherFeature = other.getFeature(index);
            diff += Math.abs(feature - otherFeature);
        }

        return diff;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (double feature : features) {
            sb.append(feature);
            sb.append(" ,");
        }


        return label + ":  " + sb.toString();
    }
}
