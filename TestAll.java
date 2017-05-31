import selection.*;

import java.util.Set;

import static org.junit.Assert.assertTrue;


/**
 * Created by ben on 18/04/17.
 */
public class TestAll {

    // Since weka treats attributes and classes uniformly, must explicitly state class indiex
    private final int CLASS_INDEX = 166;

    // File of instances to use
    private final String FILE_NAME = "musk.arff";

    // Maximum number of features to select
    private final int MAX_FEATURES = 50;

    /***
     * ===============
     * SFS TESTS
     * ===============
     */

    @org.junit.Test
    public void testSequentialForwardSelection() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential forward selection");
        FeatureSelection selector = generateSelector(Selection.SFS);
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialForwardSelectionNumfeatures() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential forward selection for max " + MAX_FEATURES + " features");
        FeatureSelection selector = generateSelector(Selection.SFS);
        Set<Integer> selectedIndices = selector.select(MAX_FEATURES);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
        assertTrue(selectedIndices.size() <= MAX_FEATURES);
    }

    /***
     * ===============
     * SBS TESTS
     * ===============
     */

    @org.junit.Test
    public void testSequentialBackwardSelection() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential backward selection");
        FeatureSelection selector = generateSelector(Selection.SBS);
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialBackwardSelectionNumfeatures() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential backward selection for max " + MAX_FEATURES + " Features");
        FeatureSelection selector = generateSelector(Selection.SBS);
        Set<Integer> selectedIndices = selector.select(MAX_FEATURES);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
        assertTrue(selectedIndices.size() <= MAX_FEATURES);
    }

    /***
     * ===============
     * FLOATING TESTS
     * ===============
     */

    @org.junit.Test
    public void testSequentialFloatingForwardSelection() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential floating forward selection");
        FeatureSelection selector = generateSelector(Selection.SFFS);
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialFloatingForwardSelectionNumFeatures() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential floating forward selection for " + MAX_FEATURES + " features");
        FeatureSelection selector = generateSelector(Selection.SFFS);
        Set<Integer> selectedIndices = selector.select(MAX_FEATURES);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
        assertTrue(selectedIndices.size() <= MAX_FEATURES);

    }

    @org.junit.Test
    public void testSequentialFloatingBackwardSelection() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential backward floating selection");
        FeatureSelection selector = generateSelector(Selection.SFBS);
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialFloatingBackwardSelectionNumFeatures() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential backward floating selection for " + MAX_FEATURES + " features");
        FeatureSelection selector = generateSelector(Selection.SFBS);
        Set<Integer> selectedIndices = selector.select(MAX_FEATURES);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
        assertTrue(selectedIndices.size() <= MAX_FEATURES);
    }

    /***
     * ===============
     * HELPER METHODS
     * ===============
     */

    private FeatureSelection generateSelector(Selection method) throws Exception {
        switch (method){
            case SBS:
                return new SequentialBackwardSelection(FILE_NAME, CLASS_INDEX);
            case SFS:
                return new SequentialForwardSelection(FILE_NAME, CLASS_INDEX);
            case SFBS:
                return new SequentialFloatingBackwardSelection(FILE_NAME, CLASS_INDEX);
            case SFFS:
                return new SequentialFloatingForwardSelection(FILE_NAME, CLASS_INDEX);
            default:
                return null;
        }
    }

    private enum Selection {
        SFS,
        SBS,
        SFFS,
        SFBS
    }


}
