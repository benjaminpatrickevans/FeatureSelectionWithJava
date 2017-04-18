import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by ben on 18/04/17.
 */
public class Test {

    private Set<Instance> instances;

    public Test() throws FileNotFoundException {
        this.instances = populateInstances();
        testSequentialForwardSelection();
    }

    public void testSequentialForwardSelection(){
        SequentialForwardSelection selector = new SequentialForwardSelection();
        Set<Integer> selectedFeatures = selector.select(instances, 1);
        System.out.println("Selected features: " + selectedFeatures);
    }

    private Set<Instance>  populateInstances() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/res/wine.data"));
        Set<Instance> instances = new HashSet<Instance>();

        while (scanner.hasNext()){
            String line = scanner.nextLine();
            System.out.println(line);
            Wine wine = new Wine(line);
            instances.add(wine);
        }

        return instances;
    }

    public static void main(String[] args) throws FileNotFoundException {
        new Test();
    }
}
