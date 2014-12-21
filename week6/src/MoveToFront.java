import java.util.LinkedList;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to
    // standard output
    public static void encode() {
        LinkedList<Byte> chars = new LinkedList<>();
        for (int i = 0; i < 256; i++)
            chars.add((byte) i);

        while (!BinaryStdIn.isEmpty()) {
            byte ch = BinaryStdIn.readByte();
            int pos = chars.indexOf(ch);
            BinaryStdOut.write((byte) pos);
            // Move ch to the begining
            if (pos > 0) {
                byte b = chars.remove(pos);
                chars.addFirst(b);
            }
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to
    // standard output
    public static void decode() {
        LinkedList<Byte> chars = new LinkedList<>();
        for (int i = 0; i < 256; i++)
            chars.add((byte) i);

        while (!BinaryStdIn.isEmpty()) {
            int pos = BinaryStdIn.readByte();
            byte ch = chars.remove(pos);
            BinaryStdOut.write(ch);
            // Move ch to the begining
            chars.addFirst(ch);
        }
        BinaryStdOut.flush();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if ("-".equals(args[0]))
            encode();
        else if ("+".equals(args[0]))
            decode();
    }
}