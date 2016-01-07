package dirTree.util;

import java.io.Serializable;

public class Pair<T, P> implements Serializable {
    public T first;
    public P second;

    public Pair(T t, P p){
        first = t;
        second = p;
    }
}
