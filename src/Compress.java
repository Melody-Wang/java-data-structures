import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Compress the first given file to the second given file using Huffman coding
 */
public class Compress {

    private static final int EXP_ARG = 2; // number of expected arguments
    private static final int NUM_CHARS = 256;

    /**
     * The main method compresses the file and writes the encoded information to the
     * output stream.
     *
     * @param args command line arguments to provide the source of the files
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // Check if the number of arguments is correct
        if (args.length != EXP_ARG) {
            System.out.println("Invalid number of arguments.\n" +
                    "Usage: ./compress <infile outfile>.\n");
            return;
        }

        // read all the bytes from the given file and make it to a byte array
        byte[] input = Files.readAllBytes(Paths.get(args[0]));

        FileOutputStream file = new FileOutputStream(args[1]);
        DataOutputStream out  = new DataOutputStream(file);
        BitOutputStream bitOut = new BitOutputStream(out);

        // build the frequency array
        int byteCount = input.length;
        int[] freq = new int[NUM_CHARS];
        for (int i = 0; i < byteCount; i++) {
            freq[input[i] & 0xff]++;
        }

        // construct HCTree
        HCTree tree = new HCTree();
        tree.buildTree(freq);

        // write number of bytes to out file
        if (byteCount > 0) {
            out.writeInt(byteCount);
        }

        /*
        // write a bit to indicate whether the file contains multiple type of characters
        if (tree.getRoot().isLeaf()) {
            bitOut.writeBit(0);
        } else {
            bitOut.writeBit(1);
        }

         */

        // encode HCTree and every byte
        tree.encodeHCTree(tree.getRoot(), bitOut);
        for (int i = 0; i < byteCount; i++) {
            tree.encode(input[i], bitOut);
        }

        // There might be several padding bits in the bitOut that we haven't written, so flush it first.
        bitOut.flush();
        out.close();
        file.close();
    }

}
