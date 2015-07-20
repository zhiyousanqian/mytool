package heap;

import java.util.Comparator;

/**
 * 简单 堆 实现
 *
 * 按“小”根堆方式实现，真正是Comparator控制
 *
 * date: 12-8-15 下午12:42
 *
 * @author: yangyang.cong@ttpod.com
 */
public class SimpleHeap<T> implements Heap<T> {


    public Object data[];
    protected int currSize;     //堆当前大小

    Comparator<T> comparator;

    public SimpleHeap(int maxElements, Comparator<T> comparator) {
        data = new Object[maxElements + 1];
        this.comparator = comparator;
    }

    @Override
    public boolean insert(T elem) {
        if (isFull()) {
            return false;
        }

        currSize++;

        data[currSize] = elem;

        shiftUp(currSize);

        return true;
    }

    @Override
    public T removeRoot() {// 删除节点，把尾节点拿过来填空，然后新官上任，接受贫下中农再教育。

        if (size() == 0) {
            return null;
        }

        Object result = data[ROOT_INDEX];
        data[ROOT_INDEX] = data[currSize];
        data[currSize] = null;
        --currSize;
        shiftDown(ROOT_INDEX);//接受贫下中农再教育。

        return (T) result;
    }

    public void swap(int i, int j) {
        Object tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    @Override
    public int shiftDown(int idx) {//相当官，接受贫下中农再教育。
        boolean needSwap = true;
        /* while not at bottom (hasLeftChild) and either child is smaller
             than current swap current with smallest of children */
        while ( needSwap && hasLeftChild(idx)) {
            /* compute smaller of existing chilren of current */
            int minChild = leftChild(idx);
            if (hasRightChild(idx) && comparator.compare((T) data[rightChild(idx)], (T) data[leftChild(idx)]) < 0)
                minChild = rightChild(idx);
            /* if smallest child is smaller than current then swap */
            if (comparator.compare((T) data[minChild], (T) data[idx]) < 0) {
                swap(idx, minChild);
                idx = minChild;
            } else {
                needSwap = false; /* both children are greater - quit */
            }
        }
        return idx;
    }

    @Override
    public int shiftUp(int idx) {//比父节点小就上飘
        /* while not in root and current is smaller than parent
		   swap parent and current */
        while (idx != ROOT_INDEX && comparator.compare((T) data[idx], (T) data[parent(idx)]) < 0) {
            swap(idx, parent(idx));
            idx = parent(idx);
        }
        return idx;
    }

    @Override
    public T root() {
        return (T) data[ROOT_INDEX];
    }

    @Override
    public int size() {
        return currSize;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int leftChild(int position) {
        return position << 1;
    }

    public int rightChild(int position) {
        return (position << 1) + 1;
    }

    public int parent(int position) {
        return position >> 1;
    }

    private boolean hasLeftChild(int i) {
        return leftChild(i) <= currSize;
    }

    private boolean hasRightChild(int i) {
        return rightChild(i) <= currSize;
    }


    @Override
    public boolean isFull() {
        return size() == data.length - 1;
    }
}
