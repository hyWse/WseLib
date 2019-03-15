package eu.hywse.lib.misc;

import com.google.common.collect.Iterables;
import lombok.Getter;

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
        if(!hasNext()) {
            return null;
        }

        T res = Iterables.get(collection, 0);
        collection.remove(res);

        return res;
    }

    public int getSize() {
        return this.collection.size();
    }

}
