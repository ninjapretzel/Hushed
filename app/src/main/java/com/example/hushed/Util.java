package com.example.hushed;

import java.util.HashMap;
import java.util.Map;

public class Util {
    /** Simple interface to allow lambdas to be used, without requiring higher API version
     * Takes no arguments and returns no result */
    public interface Action { void invoke(); }
    /** Simple interface to allow lambdas to be used, without requiring higher API version
     * Takes 1 argument and returns no result */
    public interface Action1<T1> { void invoke(T1 t1); }
    /** Simple interface to allow lambdas to be used, without requiring higher API version
     * Takes 2 arguments and returns no result */
    public interface Action2<T1, T2> { void invoke(T1 t1, T2 t2); }
    /** Simple interface to allow lambdas to be used, without requiring higher API version
     * Takes no arguments and returns a result */
    public interface Func<R> { R invoke(); }
    /** Simple interface to allow lambdas to be used, without requiring higher API version
     * Takes 1 argument and returns a result */
    public interface Func1<T1, R> { R invoke(T1 t1); }
    /** Simple interface to allow lambdas to be used, without requiring higher API version
     * Takes 2 arguments and returns a result */
    public interface Func2<T1, T2, R> { R invoke(T1 t1, T2 t2); }


    /** Generic function that turns any iterable into a Map of data
     * @param data Data to iterate over
     * @param keySelect Function that selects a key by an element of the data
     * @param valueSelect Function that selects a value by an element of the data
     * @param <K> Generic type of key of resulting map
     * @param <V> Generic type of value of resulting map
     * @param <D> Generic type of input data
     * @return {@code Map<K,V>} of {@code K}/{@code V} pairs selected from {@code data}
     */
    public static <K,V,D> Map<K,V> toMap(Iterable<D> data, Func1<D, K> keySelect, Func1<D, V> valueSelect) {
        HashMap<K,V> map = new HashMap<>();
        for (D item : data) {
            K key = keySelect.invoke(item);
            V val = valueSelect.invoke(item);
        }
        return map;
    }

}
