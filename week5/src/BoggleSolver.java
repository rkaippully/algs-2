import java.util.Stack;

public class BoggleSolver {
    private Trie dictionary;

    // Initializes the data structure using the given array of strings as the
    // dictionary.
    // (You can assume each word in the dictionary contains only the uppercase
    // letters A through Z.)
    public BoggleSolver(String[] dict) {
        dictionary = new Trie();
        for (String s : dict)
            if (s.length() >= 3)
                dictionary.add(s);
        dictionary.reset();
    }

    // Returns the set of all valid words in the given Boggle board, as an
    // Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        SET<String> words = new SET<>();
        int M = board.rows();
        int N = board.cols();
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++) {
                Stack<Integer> visited = new Stack<>();
                StringBuilder sb = new StringBuilder();
                dictionary.reset();
                dfs(board, visited, i, j, words, sb);
            }
        return words;
    }

    private void dfs(BoggleBoard board, Stack<Integer> visited, int i, int j,
            SET<String> words, StringBuilder sb) {
        char ch = board.getLetter(i, j);

        if (!searchPrefix(sb, ch))
            return;
        // Special case for 'Q'
        if (ch == 'Q' && !searchPrefix(sb, 'U')) {
            sb.setLength(sb.length() - 1);
            return;
        }

        // Mark this dice as visited
        int M = board.rows();
        int N = board.cols();
        visited.push(i * N + j);

        if (dictionary.isAtWord())
            words.add(sb.toString());

        // Find adjacent dice not visited yet
        for (int nextI = i - 1; nextI <= i + 1; nextI++)
            for (int nextJ = j - 1; nextJ <= j + 1; nextJ++) {
                if (nextI >= 0 && nextI < M && nextJ >= 0 && nextJ < N
                        && !visited.contains(nextI * N + nextJ))
                    dfs(board, visited, nextI, nextJ, words, sb);
            }

        visited.pop();
        sb.setLength(sb.length() - 1);
        dictionary.backtrack();
        // Special case for 'Q'
        if (ch == 'Q') {
            sb.setLength(sb.length() - 1);
            dictionary.backtrack();
        }
    }

    private boolean searchPrefix(StringBuilder sb, char ch) {
        sb.append(ch);
        if (!dictionary.searchChar(ch)) {
            sb.setLength(sb.length() - 1);
            return false;
        }

        return true;
    }

    // Returns the score of the given word if it is in the dictionary, zero
    // otherwise.
    // (You can assume the word contains only the uppercase letters A through
    // Z.)
    public int scoreOf(String word) {
        if (!dictionary.containsWord(word))
            return 0;

        switch (word.length()) {
        case 0:
        case 1:
        case 2:
            return 0;
        case 3:
        case 4:
            return 1;
        case 5:
            return 2;
        case 6:
            return 3;
        case 7:
            return 5;
        default:
            return 11;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}