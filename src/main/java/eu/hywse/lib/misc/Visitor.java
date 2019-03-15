package eu.hywse.lib.misc;


public interface Visitor<T> {
    /**
     * @return {@code true} if the algorithm should visit more results,
     * @param t T
     * {@code false} if it should terminate now.
     */
    public boolean visit(T t);
}