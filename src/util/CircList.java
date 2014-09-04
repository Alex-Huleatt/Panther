/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author Alex
 */
public class CircList<E> {
    
    private final int cap;
    private int size;
    private Node<E> start;
    private Node<E> end;
    public CircList(int cap) {
        this.cap = cap;
        this.size = 0;
        this.start = null;
        this.end = null;
    }
    private class Node<E> {
        E val;
        Node<E> prev;
        Node<E> next;
        public Node(E val) {
            this.val = val;
        }
    }
    
    public void add(E e) {
        Node<E> n = new Node<>(e);
        if (start == null) {
            start = n;
            end = n;
            size = 1;
            return;
        }
        
        //whatever
        
        
    }
    
    
    
}
