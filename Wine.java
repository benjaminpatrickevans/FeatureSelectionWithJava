import java.util.Arrays;

/**
 * Created by ben on 18/04/17.
 * Uses the Wine Data set from https://archive.ics.uci.edu/ml/datasets/Wine
 * for testing purposes
 */
public class Wine extends Instance {

    private final int NUM_FEATURES = 13;

    public Wine(String line){
        //1,14.23,1.71,2.43,15.6,127,2.8,3.06,.28,2.29,5.64,1.04,3.92,1065
        String[] split = line.split("\\s*,\\s*");

        // First item is class label
        label = split[0];

        // Remaining are features
        features = Arrays.copyOfRange(split, 1, NUM_FEATURES + 1);

        if(features.length != NUM_FEATURES) throw new IllegalArgumentException("Input must be a comma seperated list of output, feature 1,.. , feature 13");
    }

    @Override
    public int getNumFeatures(){ return NUM_FEATURES; }

}
