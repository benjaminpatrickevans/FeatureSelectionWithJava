package selection;

/**
 * This class is used to specify the stopping criteria.
 * Has one method (evaluate) which is passed to the selection methods,
 * this allows us to use lambdas to pass a custom evaluation function
 * for example (noImprovement, size) -> noImprovement < MAX_ITERATIONS_WITHOUT_PROGRESS.
 */
public interface Criteria {

    /***
     * Returns true if the custom specified criteria is true,
     * otherwise false.
     *
     * @param numIterations
     * @param size
     * @return
     */
    public boolean evaluate(double numIterations, int size);
}