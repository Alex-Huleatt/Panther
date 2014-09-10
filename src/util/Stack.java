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
public class Stack<E> {
    private Node<E> root;
    private int size;
    
    private class Node<E> {
        Node<E> next;
        E val;
        public Node(E val, Node<E> next) {
            this.next = next;
            this.val = val;
        }
    }
    
    public Stack() {
        root = null;
        size = 0;
    }
    
    public void push(E e) {
        root = new Node(e, root);
    }
    
    public E pop() {
        if (root == null) return null;
        E temp = root.val;
        root = root.next;
        return temp;
    }
    
    public E peek() {
        return root.val;
    }
    
    public void clear() {root = null;}
    
    public boolean isEmpty() {return root == null;}
    
    public int size() {return size;}
    
    
}
