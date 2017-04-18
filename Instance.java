/**
 * Created by ben on 18/04/17.
 */
public abstract class Instance {

    /*
        An object array to store features,
        messy but its the only way java allows multiple
        types in an array. Which means features can be a combination
        of for example numeric and string values.
     */
    protected Object[] features;

    // The class label for the instance, optional
    protected Object label;

    public Object[] getFeatures(){ return features; }

    public int getNumFeatures(){ return features.length; }

}
