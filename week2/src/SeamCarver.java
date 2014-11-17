import java.awt.Color;

public class SeamCarver {

    private int height, width;
    private Color[][] picture;
    private int[][] energy;
    private boolean isTransposed;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture pic) {
        this.width = pic.width();
        this.height = pic.height();
        this.picture = new Color[height][width];

        for (int x = 0; x < height; x++)
            for (int y = 0; y < width; y++)
                this.picture[x][y] = pic.get(y, x);

        this.isTransposed = false;

        // Compute the energy for each pixel
        this.energy = new int[height][width];
        for (int x = 0; x < height; x++)
            for (int y = 0; y < width; y++)
                computeEnergy(x, y);
    }

    private void computeEnergy(int x, int y) {
        if (x == 0 || x == height - 1 || y == 0 || y == width - 1)
            energy[x][y] = 195075;
        else
            energy[x][y] = deltaEnergy(picture[x - 1][y], picture[x + 1][y])
                    + deltaEnergy(picture[x][y - 1], picture[x][y + 1]);
    }

    private int deltaEnergy(Color c1, Color c2) {
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return r * r + g * g + b * b;
    }

    // current picture
    public Picture picture() {
        if (isTransposed)
            transpose();

        Picture pic = new Picture(width, height);
        for (int x = 0; x < height; x++)
            for (int y = 0; y < width; y++)
                pic.set(y, x, this.picture[x][y]);
        return pic;
    }

    private void transpose() {
        int p, q;
        if (isTransposed) {
            p = height;
            q = width;
        } else {
            p = width;
            q = height;
        }
        Color[][] transPic = new Color[p][q];
        int[][] transEnergy = new int[p][q];
        for (int x = 0; x < p; x++)
            for (int y = 0; y < q; y++) {
                transPic[x][y] = picture[y][x];
                transEnergy[x][y] = energy[y][x];
            }
        picture = transPic;
        energy = transEnergy;
        isTransposed = !isTransposed;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (isTransposed)
            return energy[x][y];
        else
            return energy[y][x];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!isTransposed)
            transpose();
        return findVerticalSeamInternal();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (isTransposed)
            transpose();
        return findVerticalSeamInternal();
    }

    private int[] findVerticalSeamInternal() {
        int h = picture.length;
        int w = picture[0].length;
        int[] seam = new int[h];

        /*
         * Topological shortest distance algorithm.
         * 
         * There is one extra vertex acting as a sink in our graph. Each vertex
         * is represented by an integer.
         */
        int V = height * width + 1;
        int[] edgeTo = new int[V];
        int[] distTo = new int[V];

        for (int i = 0; i < w; i++)
            distTo[0] = 0;
        for (int i = w; i < V; i++)
            distTo[i] = Integer.MAX_VALUE;

        for (int v : topologicalSort(V, h, w))
            for (int adj : adjacentVertices(v, h, w)) {
                int weight = 0;
                if (adj < V - 1)
                    weight = energy[adj / w][adj % w];
                relax(v, adj, weight, distTo, edgeTo);
            }

        for (int i = h - 1, v = V - 1; i >= 0; i--, v = edgeTo[v])
            seam[i] = edgeTo[v] % w;

        return seam;
    }

    private void relax(int from, int to, int weight, int[] distTo, int[] edgeTo) {
        if (distTo[to] > distTo[from] + weight) {
            distTo[to] = distTo[from] + weight;
            edgeTo[to] = from;
        }
    }

    private Iterable<Integer> topologicalSort(int V, int h, int w) {
        boolean[] marked = new boolean[V];
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < V; i++)
            if (!marked[i])
                dfs(marked, stack, h, w, i);
        return stack;
    }

    private void dfs(boolean[] marked, Stack<Integer> stack, int h, int w, int i) {
        marked[i] = true;
        for (int j : adjacentVertices(i, h, w))
            if (!marked[j])
                dfs(marked, stack, h, w, j);
        stack.push(i);
    }

    private Iterable<Integer> adjacentVertices(int v, int h, int w) {
        Bag<Integer> bag = new Bag<>();
        int row = v / w;
        int column = v % w;
        if (row == h - 1)
            bag.add(h * w);
        else if (row < h) {
            int idx = (row + 1) * w + column - 1;
            if (column > 0)
                bag.add(idx);
            bag.add(idx + 1);
            if (column < w - 1)
                bag.add(idx + 2);
        }
        return bag;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!isTransposed)
            transpose();
        removeVerticalSeamInternal(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (isTransposed)
            transpose();
        removeVerticalSeamInternal(seam);
    }

    private void removeVerticalSeamInternal(int[] seam) {
        int newWidth = picture[0].length - 1;
        for (int i = 0; i < seam.length; i++) {
            Color[] oldRow = picture[i];
            Color[] newRow = new Color[newWidth];
            System.arraycopy(oldRow, 0, newRow, 0, seam[i]);
            System.arraycopy(oldRow, seam[i] + 1, newRow, seam[i], newWidth
                    - seam[i] - 1);
        }

        // Recompute energy for affected pixels
        for (int i = 0; i < seam.length; i++) {
            // Above
            if (i > 0)
                computeEnergy(i - 1, seam[i]);
            // Below
            if (i < seam.length - 1)
                computeEnergy(i + 1, seam[i]);
            // Left
            if (seam[i] > 0)
                computeEnergy(i, seam[i] - 1);
            // Right
            if (seam[i] < newWidth - 1)
                computeEnergy(i, seam[i] + 1);
        }
    }
}
