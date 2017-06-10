import selection.*;

import java.util.Set;

import static org.junit.Assert.assertTrue;


/**
 * Runs the four selection methods against a given
 * dataset. This is a useful class for running all the
 * code at once and checking the output.
 *
 * The only tests this actually does is checks
 * the size of the subsets returned from the numFeatures
 * methods is less than or equal to the specified size.
 */

public class TestAll {

    // File of instances to use
    private final String FILE_NAME = "res/musk.arff";

    // Only specify this if you have a testing file, otherwise leave null and above file will be split
    private final String TESTING_FILE = null;

    // Maximum number of features to select
    private final int MAX_FEATURES = 50;

    // Maximum iterations to keep trying with no progression in subset accuracy
    private final int MAX_ITERATIONS_WITHOUT_PROGRESS = 10;

    // Whether or not to run the num feature tests
    private boolean numFeatureTests = false;

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
        if(!numFeatureTests) return;
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
        if(!numFeatureTests) return;
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
        if(!numFeatureTests) return;
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
        if(!numFeatureTests) return;
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
        FeatureSelection selector = null;
        switch (method){
            case SBS:
                selector = TESTING_FILE == null ? new SequentialBackwardSelection(FILE_NAME, MAX_ITERATIONS_WITHOUT_PROGRESS) : new SequentialBackwardSelection(FILE_NAME, TESTING_FILE, MAX_ITERATIONS_WITHOUT_PROGRESS);
                break;
            case SFS:
                selector = TESTING_FILE == null ? new SequentialForwardSelection(FILE_NAME, MAX_ITERATIONS_WITHOUT_PROGRESS) : new SequentialForwardSelection(FILE_NAME, TESTING_FILE, MAX_ITERATIONS_WITHOUT_PROGRESS);
                break;
            case SFBS:
                selector = TESTING_FILE == null ? new SequentialFloatingBackwardSelection(FILE_NAME, MAX_ITERATIONS_WITHOUT_PROGRESS) : new SequentialFloatingBackwardSelection(FILE_NAME, TESTING_FILE, MAX_ITERATIONS_WITHOUT_PROGRESS);
                break;
            case SFFS:
                selector = TESTING_FILE == null ? new SequentialFloatingForwardSelection(FILE_NAME, MAX_ITERATIONS_WITHOUT_PROGRESS) : new SequentialFloatingForwardSelection(FILE_NAME, TESTING_FILE, MAX_ITERATIONS_WITHOUT_PROGRESS);
                break;
        }

        // Special case for musk
        if(FILE_NAME.equals("musk.arff")){
            // There is a "giveaway" feature (molecule_name) which stores some class information
            selector.removeAttribute(0);
        }

        return selector;
    }

    private enum Selection {
        SFS,
        SBS,
        SFFS,
        SFBS
    }


}
