package eu.hywse.libv1_4.misc;

import lombok.Getter;

import java.util.stream.Stream;

public class WseQueue<T> {

    @Getter
    private DiscardingCollection<T> collection;

    public WseQueue() {
        this(1000);
    }

    public WseQueue(int maxSize) {
        this.collection = DiscardingCollection.create(maxSize);
    }

    public void queue(T t) {
        this.collection.add(t);
    }

    public boolean hasNext() {
        return collection.size() > 0;
    }

    public T next() {
        Stream<T> stream = collection.stream();
        return stream.findFirst().isPresent() ? stream.findFirst().get() : null;
    }

    public int getSize() {
        return this.collection.size();
    }

}
