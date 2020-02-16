package sort;

/**
 * @author BuyWatermelon
 */
public class QuickSort {
    /**
     * 快速排序
     *
     * @param array
     */
    public static void quickSort(int[] array) {
        int len;

        if (array == null || (len = array.length) == 0 || len == 1) {
            return;
        }

        sort(array, 0, len - 1);
    }

    /**
     * 快排核心算法，递归实现
     *
     * @param array
     * @param left
     * @param right
     */
    private static void sort(int[] array, int left, int right) {

        if (left > right) {
            return;
        }

        int base = array[left];

        int i = left, j = right;

        while (i != j) {

            while (array[j] >= base && i < j) {
                j--;
            }

            while (array[i] <= base && i < j) {
                i++;
            }

            if (i < j) {
                int tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
            }
        }

        array[left] = array[i];
        array[i] = base;

        sort(array, left, i - 1);
        sort(array, i + 1, right);
    }
}
