/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.jurisearch.tree;


import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class HashCollection<K, V> {

    
    /**
     * With classes of this interface it is possible to control
     * which values that have the same key are allowed in the HashCollection
     * in parallel.
     */
    public interface ListController<E> {
        /**
         * This method controls how a HashCollection handles key-value
         * pairs with the same key. If you add a new key-value pair to the
         * HashCollection it is checked whether there already exist
         * elements with  that pair. The existing elements are gathered
         * in a HashSet and given to this method together with the
         * new value. If this method returns false, then the new value
         * is added to the set with the respective key at once. If
         * this method returns true then the behaviour depends on the
         * the method {@link ListController.handelConflict()}.
         */
        public boolean generatesConflict(HashSet<E> s, E element);
        /**
         * In the case of a conflict, this method is called and its
         * result is used as new collection of elements which are
         * used as values of the key in question. 
         */
        public HashSet<E> handleConflict(HashSet<E> oldSet, E element);
    }
    
    
    private Hashtable<K, HashSet<V>> data;
    private ListController<V> listController; 

    
    /**
     * Creates a new HashCollection, where new key-value pairs never cause
     * conflicts with existing ones.
     */
    public HashCollection() {
        clear();
        listController = new ListController<V>() {
            public boolean generatesConflict(HashSet<V> s, V element) {
                return false;
            }
            public HashSet<V> handleConflict(HashSet<V> oldSet, V element) {
                return null;
            }
        };
    }
    
    public HashCollection(ListController<V> c) {
        clear();
        this.listController = c;
    }
    
    
    public void put(K key, V value) {
        HashSet<V> s = data.get(key);
        if (s == null) s = new HashSet<V>();
        if (listController.generatesConflict(s, value)) {
            s = listController.handleConflict(s, value);
        }
        s.add(value);
        data.put(key, s);
    }
    
    
    /**
     * Returns the number of elements the given key maps to.
     * 
     * @param key The key for which to ask the number of entries.
     * @return The number of elements the given key maps to.
     */
    public int getNumberOfEntries(K key) {
        HashSet<V> s = data.get(key);
        if (s == null) return 0;
        return s.size();
    }
    
    public int keySize() {
        return data.size();
    }
    
    public int valueSize() {
        int counter = 0;
        for (K key : data.keySet()) {
            counter = counter + data.get(key).size();
        }
        return counter;
    }
    
    public void clear() {
        data = new Hashtable<K, HashSet<V>>();
    }
    
    
    public boolean contains(Object value) {
        for (HashSet<V> l : data.values()) {
            if (l.contains(value)) return true;
        }
        return false;
    }

    public boolean containsValue(Object value) {
        return contains(value);
    }
    
    public boolean containsKey(Object k) {
        return data.contains(k);
    }
    
    public Enumeration<V> elements() {
        return new HashCollectionEnumerator<V>();
    }
    
    public Enumeration<K> keys() {
        return data.keys();
    }
    
    public Set<K> keySet() {
        return data.keySet();
    }
    
    /**
     * Adds all objects of the given map to this collection.
     */
    public void putAll(Map<? extends K,? extends V> t) {
        for (K key : t.keySet()) {
            V value = t.get(key);
            put(key, value);
        }
    }
    
    
    /**
     * Removes all objects which are associated with the given key.
     */
    public void remove(K key) {
        data.remove(key);
    }
    
    /**
     * Removes all occurrences of the given value from all lists of the HashCollection.
     */
    public void removeValue(V value) {
        for (K key : data.keySet()) {
            removeValue(key, value);
        }
    }
    
    
    /**
     * Removes the given value from the list to which the given
     * key points. 
     */
    public void removeValue(K key, V value) {
        HashSet<V> s = data.get(key);
        if (s != null) {
            while (s.remove(value));
            if (data.get(key).size() == 0) data.remove(key);
        }
    }
    
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof HashCollection) {
            HashCollection other = (HashCollection)o;
            if (other.data.keySet().size() != data.keySet().size()) return false;
            for (K key : data.keySet()) {
                HashSet s = other.get(key);
                if (s == null) return false;
                if (!s.equals(data.get(key))) return false; 
            }
            return true;
        } else return false;
    }
    
    public boolean isEmpty() {
        if (data.isEmpty()) return true;
        for (K key : data.keySet()) {
            HashSet<V> l = data.get(key);
            if (l.size() > 0) return false;
        }
        return true;
    }
    
    
    /**
     * Returns a HashSet containing all elements that the given
     * key maps to. The method returns always a (possibly empty)
     * HashSet, never null.
     * 
     * @param key The key that maps to the elements which are to be returned.
     * @return A HashSet containing all elements that the given
     *         key maps to.
     */
    public HashSet<V> get(K key) {
        HashSet<V> s = data.get(key);
        if (s == null) return new HashSet<V>();
        else return s;
    }
    
    
    private class HashCollectionEnumerator<E> implements Enumeration<E> {
        private Iterator<K> keyIterator;
        private Iterator<V> listIterator;
        
        public HashCollectionEnumerator() {                
            keyIterator = data.keySet().iterator();
            if (keyIterator.hasNext())
                listIterator = data.get(keyIterator.next()).iterator();
        }

        public boolean hasMoreElements() {
            if (listIterator.hasNext()) return true;
            if (keyIterator.hasNext()) return true;
            return false;
        }

        public E nextElement() {
            if (listIterator.hasNext()) return (E)listIterator.next();
            if (keyIterator.hasNext()) {                
                listIterator = data.get(keyIterator.next()).iterator();
                return nextElement();
            }
            return null;
        }
        
        
        
    }
    
}
