/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Alex
 * @param <E>
 */
public class MySet<E> implements Iterable<E> {

    private final HashMap<E, Node<E>> hash;
    private Node<E> root;
    private final int next_index = 0;
    private boolean since_modified;

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Node<E> current = root;
            @Override

            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                Node<E> temp = current;
                current = current.next;
                return temp.e;
            }
        };
    }

    private class Node<E> {

        E e;
        Node<E> next;
        Node<E> prev;
        int index;

        public Node(E e, Node<E> next, Node<E> prev) {
            this.e = e;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public int hashCode() {
            return e.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) {
                return false;
            }
            try {
                return ((Node) o).e.equals(e);
            } catch (Exception ex) {
                return false;
            }
        }
    }
    
    public boolean contains(E e) {
        return hash.get(e) != null;
    }

    public MySet() {
        hash = new HashMap<>();
        root = null;
        since_modified = false;
    }

    public void add(E e) {
        if (contains(e)) return;
        Node<E> newNode = new Node<>(e, root, null);
        if (root == null) {
            root = newNode;
            hash.put(e,newNode);
            since_modified = true;
            return;
        }
        root.prev = newNode;
        hash.put(e, newNode);
        root = newNode;
        since_modified = true;
    }

    public void remove(E e) {
        Node<E> n = hash.get(e);
        if (n == root) {
            root = n.next;
            root.prev = null;
            hash.remove(e);
            since_modified = true;
            return;
        }
        n.prev.next = n.next;
        if (n.next != null) {
           n.next.prev = n.prev; 
        }
        
        hash.remove(e);
        since_modified = true;
    }

    public int getIndex(E e) {
        return hash.get(e).index;
    }

    public int size() {
        return hash.size();
    }

    public void setIndex(E e, int in) {
        hash.get(e).index = in;
    }
    
    public String toString() {
        String str = "[";
        Node<E> c = root;
        while (c != null) {
            str += c.e.toString();
            if (c.next != null) str += ", ";
            c = c.next;
        }
        str += "]";
        return str;     
    }

}
