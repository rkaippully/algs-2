import java.awt.Color;

/**
 * Seam-carving is a content-aware image resizing technique where the image is
 * reduced in size by one pixel of height (or width) at a time.
 */
public class SeamCarver {

    private int height, width;
    private byte[][][] picture;
    private int[][] energy;
    private boolean isTransposed;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture pic) {
        this.width = pic.width();
        this.height = pic.height();
        this.picture = new byte[height][width][3];

        for (int x = 0; x < height; x++)
            for (int y = 0; y < width; y++) {
                Color c = pic.get(y, x);
                this.picture[x][y][0] = (byte) c.getRed();
                this.picture[x][y][1] = (byte) c.getGreen();
                this.picture[x][y][2] = (byte) c.getBlue();
            }

        this.isTransposed = false;

        // Compute the energy for each pixel
        this.energy = new int[height][width];
        for (int x = 0; x < height; x++)
            for (int y = 0; y < width; y++)
                computeEnergy(x, y);
    }

    private void computeEnergy(int x, int y) {
        int h = picture.length;
        int w = picture[0].length;

        if (x == 0 || x == h - 1 || y == 0 || y == w - 1)
            energy[x][y] = 195075;
        else
            energy[x][y] = deltaEnergy(picture[x - 1][y], picture[x + 1][y])
                    + deltaEnergy(picture[x][y - 1], picture[x][y + 1]);
    }

    private int deltaEnergy(byte[] p1, byte[] p2) {
        int r = (p1[0] & 0xff) - (p2[0] & 0xff);
        int g = (p1[1] & 0xff) - (p2[1] & 0xff);
        int b = (p1[2] & 0xff) - (p2[2] & 0xff);
        return r * r + g * g + b * b;
    }

    // current picture
    public Picture picture() {
        if (isTransposed)
            transpose();

        Picture pic = new Picture(width, height);
        for (int x = 0; x < height; x++)
            for (int y = 0; y < width; y++) {
                int r = this.picture[x][y][0] & 0xff;
                int g = this.picture[x][y][1] & 0xff;
                int b = this.picture[x][y][2] & 0xff;
                Color c = new Color(r, g, b);
                pic.set(y, x, c);
            }
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
        byte[][][] transPic = new byte[p][q][3];
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

        SeamCarverSP sp = new SeamCarverSP(h, w, energy);
        return sp.getSeam();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (height <= 1)
            throw new IllegalArgumentException("height <= 1");

        if (!isTransposed)
            transpose();
        removeVerticalSeamInternal(seam);
        height--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (width <= 1)
            throw new IllegalArgumentException("width <= 1");

        if (isTransposed)
            transpose();
        removeVerticalSeamInternal(seam);
        width--;
    }

    private void removeVerticalSeamInternal(int[] seam) {
        // Is this a valid seam?
        if (seam.length != picture.length)
            throw new IllegalArgumentException("Incorrect seam length");
        for (int i = 0; i < seam.length; i++)
            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1)
                throw new IllegalArgumentException("Seam entry variance");

        int newWidth = picture[0].length - 1;
        for (int i = 0; i < seam.length; i++) {
            byte[][] oldRow = picture[i];
            byte[][] newRow = new byte[newWidth][3];
            System.arraycopy(oldRow, 0, newRow, 0, seam[i]);
            System.arraycopy(oldRow, seam[i] + 1, newRow, seam[i], newWidth
                    - seam[i]);
            picture[i] = newRow;
        }

        // Recompute energy for affected pixels
        for (int i = 0; i < seam.length; i++) {
            // Pixels seam[i]..newWidth changed in this row

            // Pixels in row above
            for (int pos = seam[i]; i > 0 && pos < newWidth; pos++)
                computeEnergy(i - 1, pos);
            // Pixels in row below
            for (int pos = seam[i]; i < seam.length - 1 && pos < newWidth; pos++)
                computeEnergy(i + 1, pos);
            // Pixel to the left
            if (seam[i] > 0)
                computeEnergy(i, seam[i] - 1);
            // Pixels to the right
            for (int pos = seam[i]; pos < newWidth; pos++)
                computeEnergy(i, pos);
        }
    }
}
