package ru.ifmo.rain.Zhevtyak.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements NavigableSet<T> {

    private List<T> list = null;
    private Comparator<? super T> comp = null;

    public ArraySet() {
        this(Collections.emptyList(), null, true);
    }

    private ArraySet(Comparator<? super T> comp, List<T> collection) {
        list = collection;
        this.comp = comp;
    }

    private ArraySet(Collection<? extends T> collection, Comparator<? super T> comp, boolean isSorted) {
        if (!isSorted) {
            Set<T> set = new TreeSet<>(comp);
            set.addAll(collection);
            list = new ArrayList<>(set);
        } else {
            list = new ArrayList<>(collection);
        }
        this.comp = comp;
    }

    public ArraySet(Collection<? extends T> collection) {
        this(collection, null, false);
    }

    public ArraySet(Collection<? extends T> collection, Comparator<? super T> comp) {
        this(collection, comp, false);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public Comparator<? super T> comparator() {
        return comp;
    }

    private T superGet(int ind) {
        if (list.isEmpty()) {
            throw new NoSuchElementException();
        }
        return list.get(ind);
    }

    private T getElement(T t, boolean eq, char sign) {
        int ind = Collections.binarySearch(list, t, comp);
        if (ind > 0) {
            if (eq) {
                return list.get(ind);
            } else if ((sign == '+') && (ind == list.size() - 1)) {
                return list.get(list.size() - 1);
            } else if (sign == '+')
                return list.get(ind + 1);
            return list.get(ind - 1);
        }
        if (ind == 0) {
            if (eq)
                return list.get(ind);
            else if ((sign == '-') || list.size() == 1)
                return null;
            else return list.get(ind + 1);

        }
        if (ind < 0) {
            if (list.isEmpty() || ((-ind - 1 == 0) && (sign == '-')) || ((-ind == list.size()) && (sign == '+')))
                return null;
            if (sign == '+')
                return list.get(-ind - 1);
            else return list.get(-ind - 2);
        }
        return null;
    }

    private T floorAndLower(T e, boolean inc) {
        return null;
    }

    @Override
    public T first() {
        return superGet(0);
    }

    @Override
    public T last() {
        return superGet(size() - 1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return Collections.binarySearch(list, o, (Comparator<Object>) comp) >= 0;
    }

    private int index(T element) {
        int ind = Collections.binarySearch(list, element, comp);
        if (ind >= 0) {
            return ind;
        } else {
            return -ind - 1;
        }
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, false, toElement, false);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return tailSet(fromElement, false);
    }

    @Override
    public T lower(T t) {
        return getElement(t, false, '-');
    }

    @Override
    public T floor(T t) {
        return getElement(t, true, '-');
    }

    @Override
    public T ceiling(T t) {
        return getElement(t, true, '+');
    }

    @Override
    public T higher(T t) {
        return getElement(t, false, '+');
    }

    private void exc() {
        throw new UnsupportedOperationException("Operation of polling isn't valid");
    }

    @Override
    public T pollFirst() {
        exc();
        return null;
    }

    @Override
    public T pollLast() {
        exc();
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(list).iterator();
    }

    @Override
    public NavigableSet<T> descendingSet() {
        return null;
    }

    @Override
    public Iterator<T> descendingIterator() {
        return null;
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        return headSet(toElement, toInclusive).tailSet(fromElement, fromInclusive);
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        int ch = 1;
        if (!inclusive) ch = 0;
        return new ArraySet<T>(comp, list.subList(0, index(toElement) + ch));
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        int ch = 1;
        if (!inclusive) ch = 0;
        return new ArraySet<>(comp, list.subList(index(fromElement) - ch, size()));
    }
}

