import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SolutionDelta {
    /*
     * Complete the function below.
     */
    static int[] delta_encode(int[] array) {

        int len = array.length;
        List<Integer> intermidiate = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            if (i == 0) {
                intermidiate.add(array[i]);
            } else {
                int diff = array[i] - array[i - 1];
                if(!(-127 <= diff && 127 >= diff)){
                    intermidiate.add(-128);
                }
                intermidiate.add(diff);
            }
        }

        System.out.println(intermidiate);

        int[] result =  new int[intermidiate.size()];
        for (int i = 0; i <intermidiate.size() ; i++) {
            result[i]= intermidiate.get(i);
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        int[] array = new int[]{25626,
                25757,
                24367,
                24267,
                16,
                100,
                2,
                7277};
        int[] res = delta_encode(array);

        /*
        Scanner in = new Scanner(System.in);
        final String fileName = System.getenv("OUTPUT_PATH");
        BufferedWriter bw = null;
        if (fileName != null) {
            bw = new BufferedWriter(new FileWriter(fileName));
        } else {
            bw = new BufferedWriter(new OutputStreamWriter(System.out));
        }

        int[] res;
        int array_size = 0;
        array_size = Integer.parseInt(in.nextLine().trim());

        int[] array = new int[array_size];
        for (int i = 0; i < array_size; i++) {
            int array_item;
            array_item = Integer.parseInt(in.nextLine().trim());
            array[i] = array_item;
        }

        res = delta_encode(array);
        for (int res_i = 0; res_i < res.length; res_i++) {
            bw.write(String.valueOf(res[res_i]));
            bw.newLine();
        }

        bw.close();
        */
    }
}
