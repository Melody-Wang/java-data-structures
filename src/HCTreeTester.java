import static org.junit.Assert.*;
import org.junit.*;

public class HCTreeTester {
    private static final int NUM_CHARS = 256;
    int[] freq = new int[NUM_CHARS];
    HCTree tree = new HCTree();

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < NUM_CHARS; i++) {
            freq[i] = 0;
        }
        freq[10] = 1;
        freq[97] = 17;
        freq[98] = 8;
        freq[99] = 7;
        freq[100] = 14;
        freq[102] = 1;

        tree.buildTree(freq);
    }

    public void inorder(HCTree.HCNode root) {
        if (root != null) {
            inorder(root.getC0());
            System.out.println(root.toString());
            inorder(root.getC1());
        }
    }

    @Test
    public void testBuildTree() {
        inorder(tree.getRoot());
    }


}