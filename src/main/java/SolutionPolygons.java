import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class SolutionPolygons {
    /*
     * Complete the function below.
     */
    static int polygon(int a, int b, int c, int d) {
        List<Integer> data = Arrays.asList(a, b, c, d);

        if (data.contains(0)) {
            return 0;
        }

        boolean check = data.stream().allMatch(integer -> Integer.signum(a) == Integer.signum(integer));
        if (!check) {
            return 0;
        }

        //Square or rectangle
        if (a == c && b == d) {
            if (a == b) {
                return 2;
            }
            return 1;
        }
        // Need angle data to discriminate diamonds and parallelograms
        return 0;
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        final String fileName = System.getenv("OUTPUT_PATH");
        BufferedWriter bw = null;
        if (fileName != null) {
            bw = new BufferedWriter(new FileWriter(fileName));
        } else {
            bw = new BufferedWriter(new OutputStreamWriter(System.out));
        }

        int res;
        int a;
        a = Integer.parseInt(in.nextLine().trim());

        int b;
        b = Integer.parseInt(in.nextLine().trim());

        int c;
        c = Integer.parseInt(in.nextLine().trim());

        int d;
        d = Integer.parseInt(in.nextLine().trim());

        res = polygon(a, b, c, d);
        bw.write(String.valueOf(res));
        bw.newLine();

        bw.close();
    }

    /*
    * Complete the function below.
    */
    static int howManyAgentsToAdd(int noOfCurrentAgents, int[][] callsTimes) {
        int max = 0;
        for (int i = 0; i < callsTimes.length; i++) {
            int overlapsNo = 0;
            for (int j = i + 1; j < callsTimes.length; j++) {
                Date is = new Date(callsTimes[i][0]);
                Date ie = new Date(callsTimes[i][1]);
                Date es = new Date(callsTimes[j][0]);
                Date ee = new Date(callsTimes[j][1]);
                if (overlaps(is, ie, es, ee)) {
                    overlapsNo++;
                }
            }
            max = Math.max(max, overlapsNo);
        }
        return 0;
    }

    static boolean overlaps(Date start1, Date end1, Date start2, Date end2) {
        return start1.getTime() <= end2.getTime() && start2.getTime() <= end1.getTime();
    }
}
