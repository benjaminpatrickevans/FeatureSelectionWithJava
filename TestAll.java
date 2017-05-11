import selection.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


/**
 * Created by ben on 18/04/17.
 */
public class TestAll {

    private Set<Instance> training = new HashSet<Instance>();
    private Set<Instance> testing = new HashSet<Instance>();

    // Wine or isolet
    private boolean wine = true;

    public TestAll() throws FileNotFoundException {

        if (wine) loadWineSet();
        else loadIsoletSet();

    }

    @org.junit.Test
    public void testSequentialForwardSelection() {
        System.out.println("-------------------");
        System.out.println("Sequential forward selection");
        FeatureSelection selector = new SequentialForwardSelection(training, testing);
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialForwardSelectionNumfeatures() {
        System.out.println("-------------------");
        System.out.println("Sequential forward selection for max 10 features");
        FeatureSelection selector = new SequentialForwardSelection(training, testing);
        Set<Integer> selectedIndices = selector.select(10);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialBackwardSelection() {
        System.out.println("-------------------");
        System.out.println("Sequential backward selection");
        FeatureSelection selector = new SequentialBackwardSelection(training, testing);
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialBackwardSelectionNumfeatures() {
        System.out.println("-------------------");
        System.out.println("Sequential backward selection for max 10 Features");
        FeatureSelection selector = new SequentialBackwardSelection(training, testing);
        Set<Integer> selectedIndices = selector.select(10);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    /***
     * ===============
     * FLOATING METHODS
     * ===============
     */

    @org.junit.Test
    public void testSequentialFloatingForwardSelection() {
        System.out.println("-------------------");
        System.out.println("Sequential floating forward selection");
        FeatureSelection selector = new SequentialFloatingForwardSelection(training, testing);
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialFloatingForwardSelectionNumFeatures() {
        System.out.println("-------------------");
        System.out.println("Sequential floating forward selection for 5 features");
        FeatureSelection selector = new SequentialFloatingForwardSelection(training, testing);
        Set<Integer> selectedIndices = selector.select(5);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialBackwardFloatingSelection() {
        System.out.println("-------------------");
        System.out.println("Sequential backward floating selection");
        FeatureSelection selector = new SequentialFloatingBackwardSelection(training, testing);
        Set<Integer> selectedIndices = selector.select();
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    @org.junit.Test
    public void testSequentialBackwardFloatingSelectionNumFeatures() {
        System.out.println("-------------------");
        System.out.println("Sequential backward floating selection for 10 features");
        FeatureSelection selector = new SequentialFloatingBackwardSelection(training, testing);
        Set<Integer> selectedIndices = selector.select(10);
        selector.compareTestingAccuracy(selectedIndices);
        System.out.println("-------------------");
    }

    private void loadWineSet() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/res/wine.data"));
        Set<Instance> instances = new HashSet<Instance>();

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            Instance wine = createWine(line);
            instances.add(wine);
        }

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

        System.out.println("Training size: " + training.size());
        System.out.println("Testing size: " + testing.size());

    }

    private Set<Instance> loadIsoletSet(boolean training) throws FileNotFoundException {
        String file = training ? "training" : "testing";
        Scanner scanner = new Scanner(new File("src/res/isolet-" + file + ".data"));
        Set<Instance> instances = new HashSet<Instance>();

        int occurences = 0;

        while (scanner.hasNext() && occurences++ < 500) {
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
