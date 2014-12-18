/**
 * A trie that remembers the current search path so that it can quickly search
 * if the same path is just extended.
 */
public class Trie {

    private static class Node {
        public char value;
        public Node left, middle, right;
        public boolean isWord;
        
        @Override
        public String toString() {
            return new StringBuilder().append(value).toString();
        }
    }

    private Node root;

    private Stack<Node> searchPath;

    public void add(CharSequence s) {
        root = add(root, s, 0);
    }

    private Node add(Node node, CharSequence s, int pos) {
        if (pos >= s.length())
            return node;

        char ch = s.charAt(pos);
        if (node == null) {
            node = new Node();
            node.value = ch;
            if (pos == s.length() - 1) {
                node.isWord = true;
                return node;
            }
        }

        if (ch == node.value) {
            node.middle = add(node.middle, s, pos + 1);
            if (pos == s.length() - 1)
                node.isWord = true;
        } else if (ch < node.value)
            node.left = add(node.left, s, pos);
        else if (ch > node.value)
            node.right = add(node.right, s, pos);

        return node;
    }

    /**
     * Reset the current search path
     */
    public void reset() {
        searchPath = new Stack<>();
    }

    /**
     * Returns true if the current search prefix is a word.
     */
    public boolean isAtWord() {
        Node searchNode = searchPath.peek();
        return searchNode != null && searchNode.isWord;
    }

    public boolean containsWord(CharSequence s) {
        Node node = findNode(root, s, 0);
        return node != null && node.isWord;
    }

    private Node findNode(Node node, CharSequence s, int pos) {
        if (pos >= s.length())
            return null;

        if (node == null)
            return null;

        char ch = s.charAt(pos);
        if (ch == node.value) {
            if (pos == s.length() - 1)
                return node;
            else
                return findNode(node.middle, s, pos + 1);
        } else if (ch < node.value)
            return findNode(node.left, s, pos);
        else
            // if (ch > node.value)
            return findNode(node.right, s, pos);
    }

    public boolean searchChar(char ch) {
        if (root == null)
            return false;

        Node start;
        if (searchPath.isEmpty())
            start = root;
        else
            start = searchPath.peek().middle;

        return searchChar(start, ch) != null;
    }

    private Node searchChar(Node node, char ch) {
        if (node == null)
            return null;
        else if (ch == node.value) {
            searchPath.push(node);
            return node;
        } else if (ch < node.value)
            return searchChar(node.left, ch);
        else
            // if (ch > node.value)
            return searchChar(node.right, ch);
    }

    public void backtrack() {
        if (searchPath != null && !searchPath.isEmpty())
            searchPath.pop();
    }
}
