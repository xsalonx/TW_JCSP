import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public class exp {
    public static void main(String[] args) {
        Integer[] arr1 = new Integer[]{4, 3, 5, 2, 4};
        Integer[] arr2 = new Integer[]{2, 4};

        System.out.println(Arrays.toString(concatWithStream(arr1, arr2)));

    }

    static <T> T[] concatWithStream(T[] array1, T[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
                .toArray(size -> (T[]) Array.newInstance(array1.getClass().getComponentType(), size));
    }
}
