import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class Solution {
    /*
    * Complete the function below.
    */

    static String findNumber(int[] arr, int k) {

        System.out.println(arr.toString());
        System.out.println(k);

        // Lazy one
        List<Integer> list = Arrays.stream(arr).boxed().collect(Collectors.toList());
        String ret = list.contains(k) ? "YES" :"NO";
        System.out.println(ret);

        //
        ret = "NO";
        for (int val : arr){
            if(val == k){
                ret = "YES";
                break;
            }
        }
        System.out.println(ret);

        return ret;
    }

    public static void main(String[] args) throws IOException {

        Scanner in = new Scanner(System.in);
        final String fileName = ( System.getenv("OUTPUT_PATH") != null ) ? System.getenv("OUTPUT_PATH") : "demo.txt" ;
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        String res;

        int _arr_size = 0;
        _arr_size = Integer.parseInt(in.nextLine().trim());
        int[] _arr = new int[_arr_size];
        int _arr_item;
        for (int _arr_i = 0; _arr_i < _arr_size; _arr_i++) {
            _arr_item = Integer.parseInt(in.nextLine().trim());
            _arr[_arr_i] = _arr_item;
        }

        int _k;
        _k = Integer.parseInt(in.nextLine().trim());

        res = findNumber(_arr, _k);
        bw.write(res);
        bw.newLine();

        bw.close();
    }
}
