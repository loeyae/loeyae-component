package com.loeyae.component.audit.entities;

import java.io.Serializable;

/**
 * Pair
 *
 * @author ZhangYi
 */
public class Pair<T1, T2> implements Serializable {

    private static final long serialVersionUID = -7469531617856576040L;

    private final T1 first;

    private final T2 second;

    public Pair(T1 t1, T2 t2) {
        this.first = t1;
        this.second = t2;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }

        final Pair pair = (Pair) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) {
            return false;
        }
        if (second != null ? !second.equals(pair.second) : pair.second != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (first != null ? first.hashCode() : 0);
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    public static <T1, T2> Pair<T1, T2> make(T1 obj1, T2 obj2) {
        return new Pair<>(obj1, obj2);
    }

}
