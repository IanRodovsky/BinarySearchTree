
/**
 * A binary search tree based implementation of a symbol table.
 *
 * @author Ian Rodovsky, Sedgewick and Wayne, Acuna
 * @version 1
 */
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class RodovskyBSTST<Key extends Comparable<Key>, Value> implements OrderedSymbolTable<Key, Value> {

    private Node root;

    private class Node {

        private final Key key;
        private Value val;
        private Node left, right;
        private int N;

        public Node(Key key, Value val, int N) {
            this.key = key;
            this.val = val;
            this.N = N;
        }
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) {
            return 0;
        } else {
            return x.N;
        }
    }

    @Override
    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        // Return value associated with key in the subtree rooted at x;
        // return null if key not present in subtree rooted at x.
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return get(x.left, key);
        } else if (cmp > 0) {
            return get(x.right, key);
        } else {
            return x.val;
        }
    }

    public Value getFast(Key key) {
        Node currentNode = root;
        while (currentNode != null) {
            int cmp = key.compareTo(currentNode.key);
            if (cmp == 0) {
                return currentNode.val;
            } else if (cmp > 0) {
                currentNode = currentNode.right;
            } else {
                currentNode = currentNode.left;
            }
        }
        return null;
    }

    @Override
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }

    private Node put(Node x, Key key, Value val) {
        // Change key’s value to val if key in subtree rooted at x.
        // Otherwise, add new node to subtree associating key with val.
        if (x == null) {
            return new Node(key, val, 1);
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = put(x.left, key, val);
        } else if (cmp > 0) {
            x.right = put(x.right, key, val);
        } else {
            x.val = val;
        }
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    public void putFast(Key key, Value val) {
        if (root == null) {
            root = new Node(key, val, 1);
            return;
        }
        Node currentNode = root;
        int cmp;
        while (currentNode != null) {
            cmp = key.compareTo(currentNode.key);
            if (cmp == 0) {
                if (val == null) {
                    delete(key);
                    break;
                }
                currentNode.val = val;
                break;
            }
            currentNode.N++;
            if (cmp > 0 && currentNode.right == null) {
                currentNode.right = new Node(key, val, 1);
                break;
            } else if (cmp < 0 && currentNode.left == null) {
                currentNode.left = new Node(key, val, 1);
                break;
            } else if (cmp > 0) {
                currentNode = currentNode.right;
            } else {
                currentNode = currentNode.left;
            }
        }
    }

    @Override
    public Key min() {
        return min(root).key;
    }

    private Node min(Node x) {
        if (x.left == null) {
            return x;
        }
        return min(x.left);
    }

    @Override
    public Key max() {
        return max(root).key;
    }

    private Node max(Node x) {
        if (x.right == null) {
            return x;
        }
        return max(x.right);
    }

    @Override
    public Key floor(Key key) {
        Node x = floor(root, key);
        if (x == null) {
            return null;
        }
        return x.key;
    }

    private Node floor(Node x, Key key) {
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp == 0) {
            return x;
        }
        if (cmp < 0) {
            return floor(x.left, key);
        }
        Node t = floor(x.right, key);
        if (t != null) {
            return t;
        } else {
            return x;
        }
    }

    @Override
    public Key select(int k) {
        return select(root, k).key;
    }

    private Node select(Node x, int k) {
        if (x == null) {
            return null;
        }
        int t = size(x.left);
        if (t > k) {
            return select(x.left, k);
        } else if (t < k) {
            return select(x.right, k - t - 1);
        } else {
            return x;
        }
    }

    @Override
    public int rank(Key key) {
        return rank(key, root);
    }

    private int rank(Key key, Node x) {
        // Return number of keys less than x.key in the subtree rooted at x.
        if (x == null) {
            return 0;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return rank(key, x.left);
        } else if (cmp > 0) {
            return 1 + size(x.left) + rank(key, x.right);
        } else {
            return size(x.left);
        }
    }

    @Override
    public void deleteMin() {
        root = deleteMin(root);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) {
            return x.right;
        }
        x.left = deleteMin(x.left);
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public void delete(Key key) {
        root = delete(root, key);
    }

    private Node delete(Node x, Key key) {
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = delete(x.left, key);
        } else if (cmp > 0) {
            x.right = delete(x.right, key);
        } else {
            if (x.right == null) {
                return x.left;
            }
            if (x.left == null) {
                return x.right;
            }
            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    @Override
    public Iterable<Key> keys(Key lo, Key hi) {
        Queue<Key> queue = new LinkedList<>();
        keys(root, queue, lo, hi);
        return queue;
    }

    private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
        if (x == null) {
            return;
        }
        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);
        if (cmplo < 0) {
            keys(x.left, queue, lo, hi);
        }
        if (cmplo <= 0 && cmphi >= 0) {
            queue.add(x.key);
        }
        if (cmphi > 0) {
            keys(x.right, queue, lo, hi);
        }
    }

    @Override
    public boolean contains(Key key) {
        return contains(root, key);
    }

    private boolean contains(Node x, Key key) {
        if (x == null) {
            return false;
        }
        int cmp = x.key.compareTo(key);
        if (cmp == 0) {
            return true;
        } else if (cmp > 0) {
            return contains(x.right, key);
        } else {
            return contains(x.left, key);
        }
    }

    @Override
    public boolean isEmpty() {
        return size(root) == 0;
    }

    @Override
    public Key ceiling(Key key) {
        Node x = ceiling(root, key);
        if (x == null) {
            return null;
        }
        return x.key;
    }

    private Node ceiling(Node x, Key key) {
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp == 0) {
            return x;
        }
        if (cmp > 0) {
            return ceiling(x.right, key);
        }
        Node t = ceiling(x.left, key);
        if (t != null) {
            return t;
        } else {
            return x;
        }
    }

    @Override
    public void deleteMax() {
        root = deleteMax(root);
    }

    private Node deleteMax(Node x) {
        if (x.right == null) {
            return x.left;
        }
        x.right = deleteMax(x.right);
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    public int size(Key lo, Key hi) {
        if (hi.compareTo(lo) < 0) {
            return 0;
        } else if (contains(hi)) {
            return rank(hi) - rank(lo) + 1;
        } else {
            return rank(hi) - rank(lo);
        }
    }

    public void balance() {
        LinkedList<Node> queue = new LinkedList<Node>();
        sort(queue, root);
        root = balance(queue, 0, size() - 1);
    }

    private void sort(LinkedList<Node> queue, Node currentNode) {
        if (currentNode == null) {
            return;
        }
        sort(queue, currentNode.left);
        queue.add(currentNode);
        sort(queue, currentNode.right);
    }

    private Node balance(LinkedList<Node> queue, int lo, int hi) {
        if (lo > hi) {
            return null;
        }
        int mid = (hi + lo) / 2;
        if (((hi + lo) % 2) == 1) {
            mid++;
        }
        Node midNode = queue.get(mid);
        midNode.left = balance(queue, lo, mid - 1);
        midNode.right = balance(queue, mid + 1, hi);
        return midNode;
    }

    public void printLevel(Key key) {
        Node currentNode = root;
        while (currentNode != null) {
            int cmp = key.compareTo(currentNode.key);
            if (cmp == 0) {
                break;
            } else if (cmp > 0) {
                currentNode = currentNode.right;
            } else {
                currentNode = currentNode.left;
            }
        }
        if (currentNode == null) {
            return;
        }
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(currentNode);
        while (!queue.isEmpty()) {
            currentNode = queue.poll();
            System.out.println(currentNode.val);
            if (currentNode.left != null) {
                queue.add(currentNode.left);
            }
            if (currentNode.right != null) {
                queue.add(currentNode.right);
            }
        }
    }

    /**
     * entry point for testing.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RodovskyBSTST<Integer, String> bst = new RodovskyBSTST();

        bst.put(10, "TEN");
        bst.put(3, "THREE");
        bst.put(1, "ONE");
        bst.put(5, "FIVE");
        bst.put(2, "TWO");
        bst.put(7, "SEVEN");

        System.out.println("Before balance:");
        bst.printLevel(10); //root

        System.out.println("After balance:");
        bst.balance();
        bst.printLevel(5); //root

        System.out.print("\nSize: " + bst.size() + "\n\n");

        bst = new RodovskyBSTST();

        bst.putFast(10, "TEN");
        bst.putFast(3, "THREE");
        bst.putFast(1, "ONE");
        bst.putFast(5, "FIVE");
        bst.putFast(2, "TWO");
        bst.putFast(7, "SEVEN");

        System.out.println("Before balance:");
        bst.printLevel(10); //root

        System.out.println("After balance:");
        bst.balance();
        bst.printLevel(5); //root

        System.out.print("\nSize: " + bst.size() + "\n\n");

        System.out.println(bst.get(10));
        System.out.println(bst.getFast(10));
    }
}
