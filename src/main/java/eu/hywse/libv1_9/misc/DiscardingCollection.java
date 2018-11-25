package eu.hywse.libv1_9.misc;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Collection decorator that discards elements if its upper size bound is reached.
 * <p>
 * Initialized with a constant bound the {@code DiscardingCollection} removes values
 * when it reaches it. A Deque is used to just remove the first/oldest elements.
 * The decorator can be wrapped around an existing Deque and may be fully used
 * as a collection. But casting it to a Deque will result in a {@code ClassCastException}.
 * <p>
 * The delegated object (which is a {@code LinkedList} by default) can not  be accessed
 * from outside of the package and it should not be used since that could cause the
 * class to be useless.
 * Note that {@code DiscardingCollections} are no codeDeques!
 * They just use those to be able to remove the oldest element.
 *
 * @param <E> Type of the collections elements
 * @see ForwardingCollection
 * @author Merlin
 */
public class DiscardingCollection<E> extends ForwardingCollection<E> implements Collection<E> {

    /**
     * The maximum amount of elements that can be removed
     * from a collection at once.
     * This is needed when a lot of elements are added
     * in one operation. It should prevent not ending loops.
     */
    private static final int MAX_MASS_REMOVAL = 200;

    /**
     * The collections upper size bound.
     * If its size is getting bigger than this value
     * when modifying the collection the oldest values are discarded.
     */
    private final int upperSizeBound;

    /**
     * The Deque that is being delegated to provide all functionality needed.
     */
    private final Deque<E> delegated;

    /**
     * Constructs a DiscardingCollection from a delegated Deque.
     *
     * @param delegated      The deque that is being delegated.
     * @param upperSizeBound The collections upperSizeBound.
     */
    private DiscardingCollection(Deque<E> delegated, int upperSizeBound) {
        this.upperSizeBound = upperSizeBound;

        // Uses streams to break the delegates size down
        // to the passed upperSizeBound.
        if (delegated.size() > upperSizeBound) {
            this.delegated = delegated.stream()
                    .limit(upperSizeBound)
                    .collect(Collectors.toCollection(LinkedList::new));
            return;
        }

        this.delegated = new LinkedList<>();
    }

    /**
     * Constructs a DiscardingCollection with an upperSizeBound.
     *
     * @param upperSizeBound The collections upperSizeBound.
     */
    private DiscardingCollection(int upperSizeBound) {
        this.upperSizeBound = upperSizeBound;
        this.delegated = new LinkedList<>();
    }

    @Override
    public boolean add(E element) {
        // If the element could not have been added to the
        // delegated collection the operation is stopped.
        if (!delegated.add(element)) {
            return false;
        }

        // Checks whether the size if out of the bounds;
        // If so the oldest element is removed.
        if (delegated.size() > upperSizeBound) {
            delegated.removeFirst();
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean originalOperationResult = delegated.addAll(collection);

        // Removes all elements from the collection that exceed the upper bound.
        for (int i = 0; delegated.size() > upperSizeBound && i < MAX_MASS_REMOVAL; i++) {
            delegated.removeFirst();
        }

        return originalOperationResult;
    }

    @Override
    protected Deque<E> delegate() {
        return delegated;
    }

    /**
     * Factory Method that constructs a DiscardingCollection.
     * <p>
     * The result of this method may not be cached or reused.
     *
     * @param upperSizeBound The collections upper size bound.
     * @param <E>            Type of the collections elements.
     * @return Instance of a Discarding Collection with the given {@code upperSizeBound}.
     */
    public static <E> DiscardingCollection<E> create(int upperSizeBound) {
        Preconditions.checkArgument(upperSizeBound != 0);

        return new DiscardingCollection<E>(upperSizeBound);
    }

    /**
     * Factory Method that constructs a DiscardingCollection.
     * <p>
     * The result of this method may not be cached or reused.
     *
     * @param upperSizeBound The collections upper size bound.
     * @param <E>            Type of the collections elements.
     * @return Synchronized Instance of a Discarding Collection with the given {@code upperSizeBound}.
     */
    public static <E> Collection<E> createSynchronized(int upperSizeBound) {
        return Collections.synchronizedCollection(create(upperSizeBound));
    }


}