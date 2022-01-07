import java.util.Arrays;

/**
 * @auther zhz
 * @Date 2021-12-28 00:32
 */
public class HeapSort {
    public static void main(String[] args) {
        int[] arr = {1, 1, 22, 34, 4, 55, 67, 78};
        int size = arr.length;
        for (int i = 1; i < size; i++) {
            heapInsert(arr, i);
        }
        System.out.println(Arrays.toString(arr));
        while (size > 1) {
            swap(arr, 0, --size);
            heapify(arr, 0, size);
        }
        System.out.println(Arrays.toString(arr));
    }

    public static void heapify(int[] arr, int index, int size) {
        int left = 2 * index + 1;
        int large = (left + 1 < size) && (arr[left] < arr[left + 1]) ? left + 1 : left;
        while ((left < size) && (arr[index] < arr[large])) {
            swap(arr, index, large);
            index = large;
        }

    }

    public static void heapInsert(int[] arr, int index) {
        while (arr[index] > arr[(index - 1) / 2]) {
            swap(arr, index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    public static void swap(int[] arr, int i, int j) {
        arr[i] = arr[i] ^ arr[j];
        arr[j] = arr[i] ^ arr[j];
        arr[i] = arr[j] ^ arr[i];
    }
}

