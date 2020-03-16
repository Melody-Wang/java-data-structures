/**
 * Name: Jiayun Wang
 * PID: A155538515
 */

import java.util.NoSuchElementException;
/**
 * This class builds array based binary max-heap and min-heap
 * @param <T> Generics
 */
public class BinaryHeap <T extends Comparable<? super T>> {

    private static final int DEFAULT_CAPACITY = 5; // default initial capacity
    private static final int EXPAND_FACTOR = 2; // resizing factor
    private static final int INDEXHELPER = 2;

    private T[] heap;          // heap array
    private int nelems;        // number of elements
    private boolean isMaxHeap; // boolean to indicate whether heap is max or min

    /**
     * Initializes a binary max heap with capacity = 5
     */
    @SuppressWarnings("unchecked")
    public BinaryHeap() {
        this.heap = (T[]) new Comparable[DEFAULT_CAPACITY];
        this.nelems = 0;
        this.isMaxHeap = true;
    }

    /**
     * Initializes a binary max heap with a given initial capacity.
     *
     * @param heapSize The initial capacity of the heap.
     */
    @SuppressWarnings("unchecked")
    public BinaryHeap(int heapSize) {
        this.heap = (T[]) new Comparable[heapSize];
        this.nelems = 0;
        this.isMaxHeap = true;
    }

    /**
     * Initializes a binary heap with a given initial capacity.
     *
     * @param heapSize  The initial capacity of the heap.
     * @param isMaxHeap indicates whether the heap should be max or min
     */
    @SuppressWarnings("unchecked")
    public BinaryHeap(int heapSize, boolean isMaxHeap) {
        this.heap = (T[]) new Comparable[heapSize];
        this.nelems = 0;
        this.isMaxHeap = isMaxHeap;
    }

    /**
     * Returns the number of elements stored in the heap.
     *
     * @return The number of elements stored in the heap.
     */
    public int size() {
        return this.nelems;
    }

    /**
     * Clears all the items in the heap
     * Heap will be empty after this call returns
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        for (int i = 0; i < nelems; i++) {
            heap[i] = null;
        }
        nelems = 0;
    }

    /**
     * Adds the specified element to the heap; data cannot be null.
     * Resizes the storage if full.
     *
     * @param data The element to add.
     * @throws NullPointerException if o is null.
     */
    public void add(T data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException();
        }
        if (nelems == heap.length) {
            resize();
        }
        // first add the new element to the end of the heap
        heap[nelems] = data;
        // bubble up until the heap property is fulfilled
        bubbleUp(nelems);
        nelems++;
    }

    /**
     * Removes and returns the element at the root. If the
     * heap is empty, then this method throws a NoSuchElementException.
     *
     * @return The element at the root stored in the heap.
     * @throws java.util.NoSuchElementException if the heap is empty
     */
    public T remove() throws NoSuchElementException {
        if (nelems == 0) {
            throw new NoSuchElementException();
        }
        T removed = heap[0];
        heap[0] = heap[nelems - 1];
        heap[nelems - 1] = null;
        nelems--;
        trickleDown(0);
        return removed;
    }

    /**
     * Retrieves, but does not remove, the element at the root.
     *
     * @return item at the root of the heap
     * @throws NoSuchElementException - if this heap is empty
     */
    public T element() throws NoSuchElementException {
        if (nelems == 0) {
            throw new NoSuchElementException();
        }
        return heap[0];
    }

    /**
     * Bubble up the element until the ordering property of the heap
     * is satisfied
     *
     * @param index the index of the element to be trickled down
     */
    private void bubbleUp(int index) {
        if (isMaxHeap) {
            while (parent(index) >= 0 && heap[index].compareTo(heap[parent(index)]) > 0) {
                swap(index, parent(index));
                index = parent(index);
            }
        } else {
            while (parent(index) >= 0 && heap[index].compareTo(heap[parent(index)]) < 0) {
                swap(index, parent(index));
                index = parent(index);
            }
        }
    }

    /**
     * Trickle down the element until the ordering property of the heap
     * is satisfied
     *
     * @param index the index of the element to be trickled down
     */
    private void trickleDown(int index) {
        int leftChild = leftChild(index);
        int rightChild = rightChild(index);

        if (leftChild < nelems) {
            int toSwap = leftChild;
            if (rightChild < nelems && trickleDownSwapper(leftChild, rightChild)) {
                toSwap = rightChild;
            }
            if (trickleDownSwapper(index, toSwap)) {
                swap(index, toSwap);
            }
            trickleDown(toSwap);
        }
    }

    private boolean trickleDownSwapper(int idx1, int idx2) {
        if (isMaxHeap) {
            if (heap[idx1].compareTo(heap[idx2]) < 0) {
                return true;
            } else {
                return false;
            }
        } else {
            if (heap[idx1].compareTo(heap[idx2]) > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Double the size of the heap
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        T[] resized = (T[]) new Comparable[EXPAND_FACTOR * heap.length];
        for (int i = 0; i < heap.length; i++) {
            resized[i] = heap[i];
        }
        heap = resized;
    }

    private int parent(int index) {
        if (index == 0) {
            return -1;
        }
        return (int) ((index - 1) / INDEXHELPER);
    }

    private int leftChild(int index) {
        return INDEXHELPER * index + 1;
    }

    private int rightChild(int index) {
        return INDEXHELPER * index + INDEXHELPER;
    }

    private void swap(int idx1, int idx2) {
        T tmp = heap[idx1];
        heap[idx1] = heap[idx2];
        heap[idx2] = tmp;
    }

}
