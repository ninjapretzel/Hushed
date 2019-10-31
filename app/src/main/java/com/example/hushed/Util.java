package com.example.hushed;

import java.util.HashMap;
import java.util.List;
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
     * @return {@code Map<K,V>} of {@code K}/{@code V} pairs selected from {@code data} */
    public static <K,V,D> Map<K,V> toMap(Iterable<D> data, Func1<D, K> keySelect, Func1<D, V> valueSelect) {
        if (data == null || keySelect == null || valueSelect == null) { return null; }
        HashMap<K,V> map = new HashMap<>();
        for (D item : data) {
            K key = keySelect.invoke(item);
            V val = valueSelect.invoke(item);
            map.put(key, val);
        }
        return map;
    }

    /** Public interface for quicksort.
     * @param list List to sort
     * @param comparator Comparison function. Returns negative if first is less, positive if it is larger, and zero if it is equal to second parameter.
     * @param <T> Generic Parameter */
    public static <T> void sort(List<T> list, Func2<T, T, Integer> comparator) {
        if (list == null || comparator == null) {
            return;
        }
        qsort(list, comparator, 0, list.size()-1);
    }

    /** Simple generic swap method.
     * @param list list to swap in
     * @param a first index
     * @param b second index
     * @param <T> Generic Parameter */
    private static <T> void swap(List<T> list, int a, int b) {
        T atA = list.get(a);
        T atB = list.get(b);
        list.set(a, atB);
        list.set(b, atA);
    }

    /** Simple generic quicksort implementation
     * @param list list to sort
     * @param comparator comparison function
     * @param left left edge of sort region
     * @param right right edge of sort region
     * @param <T> Generic Parameter */
    private static <T> void qsort(List<T> list,
            Func2<T, T, Integer> comparator,
            int left,
            int right) {
        int span = right - left;
        if (span < 1) { return; }
        if (span < 2) {
            if (comparator.invoke(list.get(left), list.get(right)) > 0) {
                swap(list, left, right);
            }
            return;
        }

        int pivot = (left + right) / 2;
        int p = partition(list, comparator, left, right, pivot);
        qsort(list, comparator, left, p-1);
        qsort(list, comparator, p+1, right);
    }

    /** Simple generic partition function used in quicksort
     * @param list list to partition
     * @param comparator comparison function
     * @param left left side of region to partition
     * @param right right side of region to partition
     * @param pivot pivot location
     * @param <T> Generic Parameter
     * @return index where pivot value was placed after partitioning */
    private static <T> int partition(List<T> list,
            Func2<T, T, Integer> comparator,
            int left,
            int right,
            int pivot) {

        int lp = left;
        int rp = right;
        T pv = list.get(pivot);

        while (true) {
            while (comparator.invoke(list.get(lp), pv) <= 0 && lp != rp) { lp++; }
            if (lp == rp) { break; }
            while (comparator.invoke(list.get(rp), pv) >= 0 && lp != rp) { rp--; }
            if (lp == rp) { break; }

            swap(list, lp, rp);
        }

        int swapIndex = (pivot < lp)
                ? ((comparator.invoke(list.get(lp), pv) > 0) ? lp - 1 : lp)
                : ((comparator.invoke(list.get(rp), pv) < 0) ? rp - 1 : rp);
        swap(list, pivot, swapIndex);

        return swapIndex;
    }


}
