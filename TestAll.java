import org.junit.BeforeClass;
import selection.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.Assert.assertTrue;


/**
 * Created by ben on 18/04/17.
 */
public class TestAll {

    private List<Instance> training = new ArrayList<Instance>();
    private List<Instance> testing = new ArrayList<Instance>();

    // Wine or isolet
    private boolean wine = false;

    public TestAll() throws FileNotFoundException {
        if (wine) loadWineSet();
        else loadIsoletSet();
    }

    @org.junit.Test
    public void testSequentialForwardSelection() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential forward selection");
        FeatureSelection selector = new SequentialForwardSelection("wine.arff");
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialForwardSelectionNumfeatures() throws Exception {
        int maxFeatures = 10;
        System.out.println("-------------------");
        System.out.println("Sequential forward selection for max " + maxFeatures + " features");
        FeatureSelection selector = new SequentialForwardSelection("wine.arff");
        Set<Integer> selectedIndices = selector.select(maxFeatures);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
        assertTrue(selectedIndices.size() <= maxFeatures);
    }

    @org.junit.Test
    public void testSequentialBackwardSelection() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential backward selection");
        FeatureSelection selector = new SequentialBackwardSelection("wine.arff");
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialBackwardSelectionNumfeatures() throws Exception {
        int maxFeatures = 10;
        System.out.println("-------------------");
        System.out.println("Sequential backward selection for max " + maxFeatures + " Features");
        FeatureSelection selector = new SequentialBackwardSelection("wine.arff");
        Set<Integer> selectedIndices = selector.select(maxFeatures);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
        assertTrue(selectedIndices.size() <= maxFeatures);
    }

    /***
     * ===============
     * FLOATING METHODS
     * ===============
     */

    @org.junit.Test
    public void testSequentialFloatingForwardSelection() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential floating forward selection");
        FeatureSelection selector = new SequentialFloatingForwardSelection("wine.arff");
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialFloatingForwardSelectionNumFeatures() throws Exception {
        int maxFeatures = 5;
        System.out.println("-------------------");
        System.out.println("Sequential floating forward selection for " + maxFeatures + " features");
        FeatureSelection selector = new SequentialFloatingForwardSelection("wine.arff");
        Set<Integer> selectedIndices = selector.select(maxFeatures);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
        assertTrue(selectedIndices.size() <= maxFeatures);

    }

    @org.junit.Test
    public void testSequentialBackwardFloatingSelection() throws Exception {
        System.out.println("-------------------");
        System.out.println("Sequential backward floating selection");
        FeatureSelection selector = new SequentialFloatingBackwardSelection("wine.arff");
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialBackwardFloatingSelectionNumFeatures() throws Exception {
        int maxFeatures = 10;
        System.out.println("-------------------");
        System.out.println("Sequential backward floating selection for " + maxFeatures + " features");
        FeatureSelection selector = new SequentialFloatingBackwardSelection("wine.arff");
        Set<Integer> selectedIndices = selector.select(maxFeatures);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
        assertTrue(selectedIndices.size() <= maxFeatures);
    }


    private void loadWineSet() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/res/wine.data"));
        List<Instance> instances = new ArrayList<>();

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            Instance wine = createWine(line);
            instances.add(wine);
        }

        Collections.shuffle(instances, new Random(408962927));

        // Split the data 70:30 into training and testing sets
        int trainingSize = (int) (instances.size() * 0.7);

        int count = 0;
        for (Instance instance : instances) {
            if (count++ < trainingSize) training.add(instance);
            else testing.add(instance);
        }
    }

    private void loadIsoletSet() throws FileNotFoundException {
        this.training = loadIsoletSet(true);
        this.testing = loadIsoletSet(false);
    }

    private List<Instance> loadIsoletSet(boolean training) throws FileNotFoundException {
        String file = training ? "training" : "testing";
        List<Instance> instances = training ? this.training : this.testing;

        Scanner scanner = new Scanner(new File("src/res/isolet-" + file + ".data"));

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            Instance isolet = createIsolet(line);
            instances.add(isolet);
        }
        return instances;
    }

    private Instance createIsolet(String line) {
        // Trim final character (fullstop), and split at commas
        String[] split = line.substring(0, line.length() - 1).split("\\s*,\\s*");

        // Last item is class label
        String label = split[split.length - 1];

        double[] features = new double[split.length - 1];

        // Start at 0, split.length - 1 since final item is label
        for (int i = 0; i < split.length - 1; i++) {
            features[i] = Double.parseDouble(split[i]);
        }

        return new Instance(features, label);
    }

    private Instance createWine(String line) {
        //Example: 1,14.23,1.71,2.43,15.6,127,2.8,3.06,.28,2.29,5.64,1.04,3.92,1065
        String[] split = line.split("\\s*,\\s*");

        // First item is class label
        String label = split[0];

        double[] features = new double[split.length - 1];

        // Start at 1, as first index is label
        for (int i = 1; i < split.length; i++) {
            String feature = split[i];
            features[i - 1] = Double.parseDouble(feature);
        }

        return new Instance(features, label);
    }


}
