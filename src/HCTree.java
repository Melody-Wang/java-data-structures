import java.io.*;
import java.util.Stack;

/**
 * The Huffman Coding Tree
 */
public class HCTree {

    private static final int NUM_CHARS = 256; // alphabet size of extended ASCII
    private static final int BYTE_BITS = 8; // number of bits in a byte

    private HCNode root; // the root of HCTree
    private HCNode[] leaves = new HCNode[NUM_CHARS]; // the leaves of HCTree that contain all the symbols

    /**
     * The Huffman Coding Node
     */
    protected class HCNode implements Comparable<HCNode> {

        byte symbol; // the symbol contained in this HCNode
        int freq; // the frequency of this symbol
        HCNode c0, c1, parent; // c0 is the '0' child, c1 is the '1' child

        /**
         * Initialize a HCNode with given parameters
         *
         * @param symbol the symbol contained in this HCNode
         * @param freq   the frequency of this symbol
         */
        HCNode(byte symbol, int freq) {
            this.symbol = symbol;
            this.freq = freq;
        }

        /**
         * Getter for symbol
         *
         * @return the symbol contained in this HCNode
         */
        byte getSymbol() {
            return this.symbol;
        }

        /**
         * Setter for symbol
         *
         * @param symbol the given symbol
         */
        void setSymbol(byte symbol) {
            this.symbol = symbol;
        }

        /**
         * Getter for freq
         *
         * @return the frequency of this symbol
         */
        int getFreq() {
            return this.freq;
        }

        /**
         * Setter for freq
         *
         * @param freq the given frequency
         */
        void setFreq(int freq) {
            this.freq = freq;
        }

        /**
         * Getter for '0' child of this HCNode
         *
         * @return '0' child of this HCNode
         */
        HCNode getC0() {
            return c0;
        }

        /**
         * Setter for '0' child of this HCNode
         *
         * @param c0 the given '0' child HCNode
         */
        void setC0(HCNode c0) {
            this.c0 = c0;
        }

        /**
         * Getter for '1' child of this HCNode
         *
         * @return '1' child of this HCNode
         */
        HCNode getC1() {
            return c1;
        }

        /**
         * Setter for '1' child of this HCNode
         *
         * @param c1 the given '1' child HCNode
         */
        void setC1(HCNode c1) {
            this.c1 = c1;
        }

        /**
         * Getter for parent of this HCNode
         *
         * @return parent of this HCNode
         */
        HCNode getParent() {
            return parent;
        }

        /**
         * Setter for parent of this HCNode
         *
         * @param parent the given parent HCNode
         */
        void setParent(HCNode parent) {
            this.parent = parent;
        }

        /**
         * Check if the HCNode is leaf (has no children)
         *
         * @return if it's leaf, return true. Otherwise, return false.
         */
        boolean isLeaf() {
            return this.c0 == null && this.c1 == null;
        }

        /**
         * String representation
         *
         * @return string representation
         */
        public String toString() {
            return "Symbol: " + this.symbol + "; Freq: " + this.freq;
        }

        /**
         * Compare two nodes
         *
         * @param o node to compare
         * @return int positive if this node is greater
         */
        public int compareTo(HCNode o) {
            // low frequency gets higher priority
            if (this.freq > o.freq) {
                return 1;
            } else if (this.freq < o.freq) {
                return -1;
            } else {
                // smaller ascii value gets higher priority when frequency equals
                if ((this.symbol & 0xff) < (o.symbol & 0xff)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }

    /**
     * Returns the root node
     *
     * @return root node
     */
    public HCNode getRoot() {
        return root;
    }

    /**
     * Sets the root node
     *
     * @param root node to set
     */
    public void setRoot(HCNode root) {
        this.root = root;
    }

    /**
     * This method builds the HCTree based on the frequency of each symbol
     *
     * @param freq an array recording the frequency of each symbol at the index of its ascii value
     */
    public void buildTree(int[] freq) {
        int sum = 0;
        // create leaf nodes and put into array leaves
        for (int i = 0; i < NUM_CHARS; i++) {
            if (freq[i] != 0) {
                leaves[i] = new HCNode((byte) i, freq[i]);
                // get the sum of the frequencies
                sum += freq[i];
            }
        }

        // add tree nodes to the priority queue
        MyPriorityQueue<HCNode> pq = new MyPriorityQueue<>(NUM_CHARS);
        for (HCNode leaf: leaves) {
            if (leaf != null) {
                pq.offer(leaf);
            }
        }

        while (pq.size() != 0) {
            // pop two nodes each time
            HCNode c0 = pq.poll();
            HCNode c1 = pq.poll();
            // create the parent node and set c0 and c1
            HCNode parent = new HCNode(c0.getSymbol(), c0.getFreq() + c1.getFreq());
            parent.setC0(c0);
            parent.setC1(c1);
            c0.setParent(parent);
            c1.setParent(parent);
            // add parent node back into the priority queue
            pq.offer(parent);
            // terminate the process when the sum of the parent node is equal to the sum of all frequencies
            if (c0.getFreq() + c1.getFreq() == sum) {
                root = parent;
                break;
            }
        }
    }

    /**
     * This method encodes a single symbol using the HCTree
     *
     * @param symbol byte to be encoded using the HCTree
     * @param out BitOutputStream as the data source
     * @throws IOException
     */
    public void encode(byte symbol, BitOutputStream out) throws IOException {
        // string to record the bits on the path
        String bits = "";
        int ascii = symbol & 0xff;
        HCNode target = leaves[ascii];

        HCNode curNode = target;
        while (curNode != root) {
            // determine if the current node is c0 or c1 of its parent node
            if (curNode.getParent().getC0() == curNode) {
                bits = "0" + bits;
            } else {
                bits = "1" + bits;
            }
            curNode = curNode.getParent();
        }

        for (int i = 0; i < bits.length(); i++) {
            out.writeBit(Integer.parseInt(bits.substring(i, i + 1)));
        }
    }

    /**
     * This method decodes a single symbol read from the BitInputStream
     *
     * @param in BitInputStream as the data source
     * @return decoded byte value of a single symbol
     * @throws IOException
     */
    public byte decode(BitInputStream in) throws IOException {
        HCNode curNode = root;

        while (!curNode.isLeaf()) {
            int next = in.readBit();
            if (next == 0) {
                curNode = curNode.getC0();
            } else {
                curNode = curNode.getC1();
            }
        }
        return curNode.getSymbol();
    }

    /**
     * This method encodes the tree structure built by buildTree method
     *
     * @param node current node to process
     * @param out BitOutPutStream as the data source
     * @throws IOException
     */
    public void encodeHCTree(HCNode node, BitOutputStream out) throws IOException {
        if (node != null) {
            if (node.isLeaf()) {
                out.writeBit(1);
                out.writeByte(node.getSymbol());
            } else {
                out.writeBit(0);
            }
            encodeHCTree(node.getC0(), out);
            encodeHCTree(node.getC1(), out);
        }
    }

    /**
     * This method decodes the tree structure encoded by the encodeHCTree method
     *
     * @param in BitInputStream as the data source
     * @return root node of the tree
     * @throws IOException
     */
    public HCNode decodeHCTree(BitInputStream in) throws IOException {
        if (in.readBit() == 0) {
            HCNode c0 = decodeHCTree(in);
            HCNode c1 = decodeHCTree(in);
            HCNode parent = new HCNode(c0.getSymbol(), 0);
            parent.setC0(c0);
            parent.setC1(c1);
            return parent;
        } else if (in.readBit() == 1) {
            Byte symbol = in.readByte();
            int ascii = symbol & 0xff;
            leaves[ascii] = new HCNode(in.readByte(), 0);
            return leaves[ascii];
        }
        return null;
    }

}