package eu.hywse.libv1_4.misc;

import java.util.List;
import java.util.Random;

public class WseRandom extends Random {

    public WseRandom() {
        super();
    }

    public WseRandom(long seed) {
        super(seed);
    }

    public int between(int min, int max) {
        return nextInt(max - min + 1) + min;
    }

    public <T> T arrayElement(T... items) {
        return items[nextInt(items.length)];
    }

    public <T> T listElement(List<T> list) {
        return list.get(nextInt(list.size()));
    }

    public <T> int getRandom(T...obj) {
        return nextInt(obj.length);
    }

}
