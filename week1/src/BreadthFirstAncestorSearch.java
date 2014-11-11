import java.util.HashSet;
import java.util.Set;

public class BreadthFirstAncestorSearch {

    private Digraph graph;

    private int ancestor;
    private int bestAncestorLength;

    /**
     * markedFromV[i] is true if i is reachable from v. markedFromW[i] is true
     * if i is reachable from w.
     */
    private boolean[] markedFromV, markedFromW;

    /**
     * distToV[i] is the shortest distance to i from v distToW[i] is the
     * shortest distance to i from w
     */
    private int[] distToV, distToW;

    /**
     * Vertices that are visited so far
     */
    private Set<Integer> visited;

    public BreadthFirstAncestorSearch(Digraph graph) {
        this.graph = graph;
        this.ancestor = -1;
        this.bestAncestorLength = -1;

        int vertices = graph.V();
        markedFromV = new boolean[vertices];
        markedFromW = new boolean[vertices];
        distToV = new int[vertices];
        distToW = new int[vertices];
        visited = new HashSet<>();
        for (int i = 0; i < vertices; i++) {
            distToV[i] = Integer.MAX_VALUE;
            distToW[i] = Integer.MAX_VALUE;
        }
    }

    public void searchAncestor(Iterable<Integer> vs, Iterable<Integer> ws) {
        // Clear previous state
        clearState();

        Queue<Integer> vqueue = new Queue<>();
        for (int i : vs) {
            vqueue.enqueue(i);
            markedFromV[i] = true;
            visited.add(i);
            distToV[i] = 0;
        }
        Queue<Integer> wqueue = new Queue<>();
        for (int i : ws) {
            if (markedFromV[i]) {
                ancestor = i;
                bestAncestorLength = 0;
                return;
            }
            wqueue.enqueue(i);
            markedFromW[i] = true;
            visited.add(i);
            distToW[i] = 0;
        }

        while (!vqueue.isEmpty() || !wqueue.isEmpty()) {
            bfs(vqueue, distToV, distToW, markedFromV, markedFromW);
            bfs(wqueue, distToW, distToV, markedFromW, markedFromV);
        }
    }

    private void bfs(Queue<Integer> queue, int[] dist, int[] otherDist,
            boolean[] marked, boolean[] otherMarked) {
        if (!queue.isEmpty()) {
            int i = queue.dequeue();
            for (int j : graph.adj(i)) {
                if (!marked[j]) {
                    dist[j] = dist[i] + 1;
                    // Best ancestor cannot be in the path further
                    if (dist[j] > bestAncestorLength)
                        continue;

                    // Is j a better ancestor?
                    if (otherMarked[j]) {
                        int length = dist[j] + otherDist[j];
                        if (length < bestAncestorLength) {
                            ancestor = j;
                            bestAncestorLength = length;
                        }
                    }

                    // Enqueue j for BFS
                    marked[j] = true;
                    visited.add(j);
                    queue.enqueue(j);
                }
            }
        }
    }

    private void clearState() {
        for (int i : visited) {
            markedFromV[i] = false;
            markedFromW[i] = false;
            distToV[i] = Integer.MAX_VALUE;
            distToW[i] = Integer.MAX_VALUE;
        }
        visited.clear();
        this.ancestor = -1;
        this.bestAncestorLength = Integer.MAX_VALUE;
    }

    public int ancestorPathLength() {
        if (bestAncestorLength == Integer.MAX_VALUE)
            return -1;
        else
            return bestAncestorLength;
    }

    public int ancestor() {
        return ancestor;
    }

}
