
public class BurrowsWheeler {

    // apply Burrows-Wheeler encoding, reading from standard input and writing
    // to standard output
    public static void encode() {
        String s = BinaryStdIn.readString();
        int N = s.length();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        byte[] t = new byte[N];
        for (int i = 0; i < N; i++) {
            int index = csa.index(i);
            t[i] = (byte) s.charAt((index + N - 1) % N);
            if (index == 0) {
                BinaryStdOut.write(i);
            }
        }
        for (int i = 0; i < N; i++)
            BinaryStdOut.write(t[i]);

        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler decoding, reading from standard input and writing
    // to standard output
    public static void decode() {
        int first = BinaryStdIn.readInt();
        int N = 0;
        byte[] tails = new byte[256];
        while (!BinaryStdIn.isEmpty()) {
            // Grow if needed
            if (N >= tails.length) {
                byte[] tmp = new byte[tails.length * 2];
                System.arraycopy(tails, 0, tmp, 0, tails.length);
                tails = tmp;
            }
            tails[N++] = BinaryStdIn.readByte();
        }

        // Compute frequencies of each character
        int[] freq = new int[257];
        for (int i = 0; i < N; i++)
            freq[tails[i] + 1]++;

        // Character i will be found in the range freq[i]..freq[i+1]-1 in the
        // original suffixes array
        for (int r = 0; r < 256; r++)
            freq[r + 1] += freq[r];

        // Find the heads of the sorted suffixes, heads will be sorted
        int pos = 0;
        byte[] heads = new byte[N];
        for (int i = 0; i < 256; i++)
            while (pos < freq[i + 1])
                heads[pos++] = (byte) i;

        // Compute next[]
        int[] next = new int[N];
        for (int i = 0; i < N; i++) {
            byte ch = tails[i];
            next[freq[ch]++] = i;
        }

        // Write the original text using first and heads
        int idx = first;
        for (int i = 0; i < N; i++) {
            BinaryStdOut.write(heads[idx]);
            idx = next[idx];
        }

        BinaryStdOut.flush();
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args) {
        if ("-".equals(args[0]))
            encode();
        else if ("+".equals(args[0]))
            decode();
    }
}