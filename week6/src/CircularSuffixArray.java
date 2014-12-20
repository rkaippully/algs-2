import java.util.Arrays;

public class CircularSuffixArray {

    private static final class Entry implements Comparable<Entry> {
        private final char[] chars;

        /**
         * This is the start index of the string in chars array
         */
        private final int index;

        public Entry(char[] chars, int index) {
            this.chars = chars;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public int compareTo(Entry that) {
            // We can assume that both entries are of same length
            int thisIndex = this.index;
            int thatIndex = that.index;
            int N = this.chars.length;
            for (int i = 0; i < N; i++) {
                int diff = this.chars[thisIndex] - that.chars[thatIndex];
                if (diff != 0)
                    return diff;
                thisIndex = (thisIndex + 1) % N;
                thatIndex = (thatIndex + 1) % N;
            }
            return 0;
        }
    }

    private Entry[] sortedArray;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        this.sortedArray = new Entry[s.length()];

        // Form the sorted array
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sortedArray[i] = new Entry(chars, i);
        }
        Arrays.sort(sortedArray);
    }

    // length of s
    public int length() {
        return sortedArray.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        return sortedArray[i].getIndex();
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }
}