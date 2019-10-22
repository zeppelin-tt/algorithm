package algorithm;

public class Util {

    static boolean containsInt(int[] arr, int x) {
        for (int anArr : arr) {
            if (anArr == x)
                return true;
        }
        return false;
    }

}
