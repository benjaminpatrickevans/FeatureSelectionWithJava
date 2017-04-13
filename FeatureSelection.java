import java.util.Set;

/**
 * Created by ben on 8/04/17.
 */
public interface FeatureSelection<E> {
    /**
    Returns a subset of only the most important features
    chosen by some measure.
     */
   public Set<E> select(Set<E> features);

   /**
   Returns a subset containing only the numFeatures most important
    features. If numFeatures is >= original.size(), the original
    set is returned.
    */
   public Set<E> select(Set<E> original, int numFeatures);


}
