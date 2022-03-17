package bfst20.logic.ternary;

import bfst20.logic.entities.Address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TST implements Serializable {
    private class Node implements Serializable {
        private Node left, mid, right;
        private List<Address> value;
        private char key;


        public Node(char key) {
            this.key = key;
        }

        public Node getLeft() {
            return left;
        }

        public Node getMid() {
            return mid;
        }

        public Node getRight() {
            return right;
        }

        public char getKey() {
            return key;
        }

        public List<Address> getValue() {
            return value;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public void setMid(Node mid) {
            this.mid = mid;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public void setValue(Address address) {
            if (value == null) {
                value = new ArrayList<>();
            }

            value.add(address);
        }
    }

    private Node root;
    private int size;

    public void put(String key, Address value) {
        key = key.toLowerCase();
        root = put(root, key, value, 0);
    }

    private Node put(Node parent, String key, Address value, int index) {
        char character = key.charAt(index);

        if (parent == null) {
            size++;
            parent = new Node(character);
        }

        if (character == 'l') {
            String i = "";
        }

        if (character < parent.getKey()) {
            parent.setLeft(put(parent.getLeft(), key, value, index));
        } else if (character > parent.getKey()) {
            parent.setRight(put(parent.getRight(), key, value, index));
        } else if (index < key.length() - 1) {
            parent.setMid(put(parent.getMid(), key, value, index + 1));
        } else {
            parent.setValue(value);
        }

        return parent;
    }

    public List<Address> get(String key) {
        key = key.toLowerCase();
        Node result = get(root, key, 0);

        return result == null ? null : result.getValue();
    }


    private Node get(Node parent, String key, int index) {
        char character = key.charAt(index);
        if (character == ' ') return parent;
        if (character > parent.getKey()) {
            return get(parent.getRight(), key, index);
        } else if (character < parent.getKey()) {
            return get(parent.getLeft(), key, index);
        } else if (index < key.length() - 1) {
            return get(parent.getMid(), key, index + 1);
        } else {
            return parent;
        }
    }

    public int getSize() {
        return size;
    }

    public Queue<Address> keysWithPrefix(String prefix) {
        prefix = prefix.toLowerCase();
        Queue<Address> queue = new LinkedList<>();
        Node startNode;
        try {
            startNode = get(root, prefix, 0);
        } catch (NullPointerException exception) {
            return queue;
        }

        if (startNode == null) return queue;
        if (startNode.getValue() != null) queue.addAll(startNode.getValue());

        collect(startNode.getMid(), new StringBuilder(prefix), queue);

        return queue;
    }

    private void collect(Node parent, StringBuilder prefix, Queue<Address> queue) {
        if (parent == null) return;
        collect(parent.getLeft(), prefix, queue);
        if (parent.getValue() != null) queue.addAll(parent.getValue());
        collect(parent.getMid(), prefix.append(parent.getKey()), queue);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(parent.getRight(), prefix, queue);
    }

}
