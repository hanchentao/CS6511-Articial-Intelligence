import java.io.*;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.*;

public class P4 {


    public static Map<Character, String> state = new HashMap<>(){{
        put('0', "Zero");
        put('1', "Aware");
        put('2',"Considering");
        put('3', "Experiencing");
        put('4', "Ready");
        put('5', "Lost");
        put('6', "Satisfied");
    }};
    public static Map<String, Integer> links = new HashMap<>(){{
        put("DEMO", 0);
        put("VIDEO", 1);
        put("TESTIMONIAL", 2);
        put("PRICING", 3);
        put("BLOG", 4);
        put("PAYMENT", 5);
    }};

    public static int linklen = 6;


    //A is a state transition matrix
    //                                            0     1     2     3     4     5
    //                                           Zero  Aware Consi Exper Ready Lost
    public static double[][] A = new double[][]{{0.60, 0.40, 0.00, 0.00, 0.00, 0.00},    //Zero
                                                {0.00, 0.49, 0.30, 0.00, 0.01, 0.20},    //Aware
                                                {0.00, 0.00, 0.48, 0.20, 0.02, 0.30},    //Consi
                                                {0.00, 0.00, 0.00, 0.40, 0.30, 0.30},    //Exper
                                                {0.00, 0.00, 0.00, 0.00, 0.80, 0.20},    //Ready
                                                {0.00, 0.00, 0.00, 0.00, 0.00, 1.00}};   //Lost

    //B is a observe the probability distribution matrix
    //                                            0     1     2     3     4     5
    //                                           Demo  Video Testi Price Blog  Pay
    public static double[][] B = new double[][]{{0.10, 0.01, 0.05, 0.30, 0.50, 0.00},    //Zero   0
                                                {0.10, 0.01, 0.15, 0.30, 0.40, 0.00},    //Aware  1
                                                {0.20, 0.30, 0.05, 0.40, 0.40, 0.00},    //Consi  2
                                                {0.40, 0.60, 0.05, 0.30, 0.40, 0.00},    //Exper  3
                                                {0.05, 0.75, 0.35, 0.20, 0.40, 0.00},    //Ready  4
                                                {0.01, 0.01, 0.03, 0.05, 0.20, 0.00}};    //Lost   5
//                                                {0.40, 0.40, 0.01, 0.05, 0.50, 1.00}};   //Satis  6


    //C is a initial probability distribution array
    //                                     0     1     2     3     4     5     6
    //                                     Zero  Aware Consi Exper Ready Lost  Satis
    public static double[] C = new double[]{1.0, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00};


    public static void main(String[] args) throws IOException {

        File file = new File("C:\\Users\\ALI\\IdeaProjects\\AI_Project4\\src\\Data.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(reader);

        List<String> list = new LinkedList<>();

        String line = "#";
        line = br.readLine();
        while (line != null) {
            if(line.length() == 0 || line.charAt(0) != '#') list.add(line);
            line = br.readLine();
        }
        String[] strings = list.toArray(new String[0]);
        String res = HMM(strings);

        StringBuilder sb = new StringBuilder();
        for(char c : res.toCharArray()){
            sb.append(state.get(c) + "  ->  ");
        }
        sb.delete(sb.length() - 6, sb.length());
        System.out.println(sb.toString());
    }


    static class Node{
        double val;
        String name;
        Node(double val, String name){
            this.val = val;
            this.name = name;
        }
    }


    private static String HMM(String[] S){
        int len = S.length;
        //arr is an matrix record all the information, every step has six situations,
        Node[][] arr = new Node[len][linklen];

//      get the first iteration's result
        String s = S[0];
        double[] temp = get(s);

        if(temp.length == 0){
            StringBuilder sb = new StringBuilder();
            while(sb.length() < len){
                sb.append("6");
            }
            return sb.toString();
        }

//      after we get first result, calculate other
        for(int j = 0; j < linklen; j++){
            arr[0][j] = new Node(C[j] * temp[j], "" + j);
        }
//        System.out.println(Arrays.toString(temp));

        for(int i = 1; i < len; i++){
            s = S[i];
            temp = get(s);
//            System.out.println(Arrays.toString(temp) + "    " + temp.length);
            if(temp.length == 0){

                StringBuilder sb = new StringBuilder(arr[i - 1][4].name);
                while(sb.length() < len){
                    sb.append("6");
                }
                return sb.toString();
            }


            for(int j = 0; j < linklen; j++){     //j代表当前目标位置， 从前转到j
                double[] values = calculate(arr[i - 1], temp, j);  // 下标代表， 转到 j中的 6种情况 // values array means the final possibility

                //we need to find maximum one and record the idx, then put it in a new node
                int maxidx = -1;
                double maxval = -1;
                for(int k = 0; k < values.length; k++){
                    if(values[k] > maxval){
                        maxval = values[k];
                        maxidx = k;
                    }
                }
                arr[i][j] = new Node(maxval,arr[i - 1][maxidx].name + j);
            }
        }

//        for(int i = 0; i < len; i++){
//            for(int j = 0; j < 6; j++){
//                System.out.print(arr[i][j].val + " " + arr[i][j].name + "               ");
//            }
//            System.out.println();
//        }

        Arrays.sort(arr[len - 1], (a, b) -> {
            if(a.val > b.val) return -1;
            else return 1;
        });
        return arr[len - 1][0].name;
    }

    //get function is used to calculate the possibility of now!!
    private static double[] get(String s){
        double[] res = new double[linklen];
        Arrays.fill(res, 1.0);

        //if user have no action, the possibility is (1 - p1) * (1 - p2) * ..... (1 - pn)
        if(s.length() == 0){
            for(int i = 0; i < linklen; i++){
                for(int j = 0; j < linklen; j++){
                    res[i] *= (1 - B[i][j]);
                }
            }
            return res;
        }

        //if user have some actions, we based on formal result, get the new one, if user click the second one, we use p2  to replace (1 - p2)
        String[] strings = s.split(",");
        for(int i = 0; i < strings.length; i++){
            int idx = P4.links.get(strings[i]);

            //when user click p5, jump to satisfy directly!!!, so we return empty array
            if(idx == 5) return new double[0];

            for(int j = 0; j < linklen; j++){
                res[j] = res[j] * B[j][idx] / (1 - B[j][idx]);    //
            }
//            for(int j = 0; j < linklen; j++){
//                res[j] += B[j][idx];
//            }
        }




        return res;
    }

    private static double[] calculate(Node[] nodes, double[] temp, int j){
        double[] values = new double[linklen];
        for(int i = 0; i < linklen; i++){
            // the total possibility == formal state result * transition possibility * now possibility
            values[i] = nodes[i].val * A[i][j] * temp[j];
        }
        return values;
    }
}
