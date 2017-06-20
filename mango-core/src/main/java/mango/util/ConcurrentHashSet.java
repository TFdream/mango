package mango.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Serializable {

    private static final long serialVersionUID = -8730431991700296747L;

    private static final Object PRESENT = new Object();

    private final ConcurrentHashMap<E, Object> map;

    public ConcurrentHashSet() {
        map = new ConcurrentHashMap<E, Object>();
    }

    public ConcurrentHashSet(int initialCapacity) {
        map = new ConcurrentHashMap<E, Object>(initialCapacity);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.keySet().size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    public void clear() {
        map.clear();
    }
}