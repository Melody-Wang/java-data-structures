import java.io.*;

/**
 * Decompress the first given file to the second given file using Huffman coding
 */
public class Decompress {
    private static final int EXP_ARG = 2; // number of expected arguments

    /**
     * The main method decompresses the compressed file and writes the recovered
     * information to the output stream
     *
     * @param args command line arguments to provide the source of the files
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // Check if the number of arguments is correct
        if (args.length != EXP_ARG) {
            System.out.println("Invalid number of arguments.\n" +
                    "Usage: ./decompress <infile outfile>.\n");
            return;
        }

        FileInputStream inFile = new FileInputStream(args[0]);
        DataInputStream in = new DataInputStream(inFile);
        BitInputStream bitIn = new BitInputStream(in);

        FileOutputStream outFile = new FileOutputStream(args[1]);
        DataOutputStream out = new DataOutputStream(outFile);

        // read the number of byte from the file
        int byteCount;
        try {
            byteCount = in.readInt();
        } catch (EOFException e) {
            byteCount = 0;
        }

        // decode and build the tree from the "header"
        HCTree tree = new HCTree();
        tree.setRoot(tree.decodeHCTree(bitIn));

        // decode the file and write the results
        while (byteCount > 0) {
            byte symbol = tree.decode(bitIn);
            //System.out.println((char) symbol & 0xff);
            out.writeByte(symbol);
            byteCount--;
        }

        inFile.close();
        in.close();
        outFile.close();
        out.close();
        return;
    }

}
