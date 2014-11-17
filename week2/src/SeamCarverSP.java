/**
 * A shortest path algorithm based on topological sorting. The picture is
 * represented as a DAG, so we can use the topological sort algorithm to find
 * the seam.
 */
public class SeamCarverSP {

    private int height;
    private int width;
    private int[][] energy;

    private int[] edgeTo, distTo;
    private Stack<Integer> topologyStack;
    private boolean[] marked;

    private int[] seam;

    public SeamCarverSP(int height, int width, int[][] energy) {
        this.height = height;
        this.width = width;
        this.energy = energy;

        findSeam();
    }

    private void findSeam() {
        /*
         * Topological shortest distance algorithm.
         * 
         * There is one extra vertex acting as a sink in our graph. Each vertex
         * is represented by an integer.
         */
        int V = height * width + 1;
        edgeTo = new int[V];
        distTo = new int[V];

        for (int i = 0; i < width; i++)
            distTo[0] = 0;
        for (int i = width; i < V; i++)
            distTo[i] = Integer.MAX_VALUE;

        for (int v : topologicalSort(V))
            for (int adj : adjacentVertices(v)) {
                int weight = 0;
                if (adj < V - 1)
                    weight = energy[adj / width][adj % width];
                relax(v, adj, weight);
            }

        seam = new int[height];
        for (int i = height - 1, v = V - 1; i >= 0; i--, v = edgeTo[v])
            seam[i] = edgeTo[v] % width;
    }

    private void relax(int from, int to, int weight) {
        if (distTo[to] > distTo[from] + weight) {
            distTo[to] = distTo[from] + weight;
            edgeTo[to] = from;
        }
    }

    private Iterable<Integer> topologicalSort(int V) {
        marked = new boolean[V];
        topologyStack = new Stack<>();
        for (int i = 0; i < V; i++)
            if (!marked[i])
                dfs(i);
        return topologyStack;
    }

    private void dfs(int i) {
        marked[i] = true;
        for (int j : adjacentVertices(i))
            if (!marked[j])
                dfs(j);
        topologyStack.push(i);
    }

    private Iterable<Integer> adjacentVertices(int v) {
        Bag<Integer> bag = new Bag<>();
        int row = v / width;
        int column = v % width;
        if (row == height - 1)
            bag.add(height * width);
        else if (row < height) {
            int idx = (row + 1) * width + column - 1;
            if (column > 0)
                bag.add(idx);
            bag.add(idx + 1);
            if (column < width - 1)
                bag.add(idx + 2);
        }
        return bag;
    }

    public int[] getSeam() {
        return seam;
    }
}
