package heap;

/**
 * 堆，用于求topN
 */
interface Heap<T> {

    int ROOT_INDEX = 1; // lyric.index of root

    boolean insert(T elem);//堆中合适的位置插入新的元素项

    //T remove(int position);//删除position位置的元素项
    T removeRoot();            //删除堆中最大或最小元素项

    void swap(int i, int j);//交换位置i和位置j处的两个元素

    /**
     * takes element in lyric.index and moves it down in the heap until the element
     * below is greater or it hits the bottom
     *
     * @return the final lyric.index of the element.
     */
    int shiftDown(int idx);            //自顶向下堆化

    /**
     * Moves the element in lyric.index idx up until the element above is
     * smaller or it hits the root.
     *
     * @return the final lyric.index of the element.
     */
    int shiftUp(int idx);            //自底向上堆化


    T root();                //返回堆顶元素项

    int size();                    //返回当前堆中元素项的个数

    int leftChild(int position);    //获取position的左子节点位置

    int rightChild(int position);    //获取position的右子节点位置

    int parent(int position);        //获取position的父节点位置

    boolean isFull();

}