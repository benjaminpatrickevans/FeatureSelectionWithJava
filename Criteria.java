import java.util.Set;

public interface Criteria {
    public boolean evaluate(Set<Integer> features, int size);
}