package heap;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 利用堆的性质 求出topN
 */
public class TopN<T extends Comparable> {
    private int n;
    private Heap<T> minHeap = null;

    public TopN(int n) {
        this(n, true);
    }

    public TopN(int n, final boolean originSort) {
        this.n = n;

        Comparator<T> cmp = originSort ? new Comparator<T>() {
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        } : new Comparator<T>() {
            public int compare(T o1, T o2) {
                return o2.compareTo(o1);
            }
        };
        minHeap = new SimpleHeap<T>(n, cmp);
    }

    public void insert(T entry) {

        if (!minHeap.isFull()) {
            minHeap.insert(entry);
        } else {
            T root = minHeap.root();
            if (entry.compareTo(root) > 0) {
                minHeap.removeRoot();
                minHeap.insert(entry);
            }
        }
    }
    public List<T> getAll() {
        List<T> res = new ArrayList<T>();
        while (minHeap.size() != 0) {
            res.add(minHeap.removeRoot());
        }
        return res;
    }
}
