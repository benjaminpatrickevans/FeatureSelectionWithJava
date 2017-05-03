import java.util.Set;

/**
 * Created by ben on 18/04/17.
 */
public class Instance  {

    private double[] features;

    // The class label for the instance, optional
    private String label;

    public Instance(double[] features){
        this.features = features;
    }

    public Instance(double[] features, String label){
        this.features = features;
        this.label = label;
    }

    public double[] getFeatures(){ return features; }

    public int getNumFeatures(){ return features.length; }

    public String getLabel(){ return label; }

    public double getFeature(int index){ return features[index]; }

    public Instance createInstance(Set<Integer> featureIndices){
        double[] features = new double[featureIndices.size()];

        int i=0;
        for(int index: featureIndices){
            features[i++] = features[index];
        }

        return new Instance(features , label);
    }

    public double distanceTo(Instance other,  Set<Integer> indices){
        if(getNumFeatures() != other.getNumFeatures()) throw new IllegalArgumentException("Number of features do not match");

        double diff = 0;
        for(Integer index: indices){
            double feature = features[index];
            double otherFeature = other.getFeature(index);
            diff += Math.abs(feature - otherFeature);
        }

        return diff;
    }

}
