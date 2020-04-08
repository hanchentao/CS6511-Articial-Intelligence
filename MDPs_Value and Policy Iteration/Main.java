import java.sql.SQLOutput;
import java.text.DecimalFormat;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        //null means the value need to be calculated.
        //MIN_VALUE means there has a wall, which can not be poss.
        //num is the final number which be set by question, and cannot be changed and once we get there, get the point and exit the game

        double discount1 = 0.9;
        double[] noise1 = new double[]{0.8,0.1,0.0,0.1};
        Double[][] arr1 = new Double[1000][1000];
        Random random = new Random();
        for(int i = 0; i < 1000; i++){
            int j = random.nextInt(1000);
            Double val = random.nextDouble() * 1000;
            arr1[i][j] = val;
        }
//        Double[][] arr1 = new Double[][]{{null, null, null,  1.0, null, null, null},
//                                        {null, null, null, -1.0, null,  1.0, null},
//                                        {-1.0, null, null, -1.0, null,  4.0, null},
//                                        {null,  1.0, null, -1.0, null,  1.0, null},
//                                        {null,100.0, null,-100.0,null,  3.0, null},
//                                        {null,  2.0, null, -1.0, null,  3.0, null},
//                                        { 0.0, null, null, -1.0, null,  1.0, null}};


//        double discount2 = 0.8;
//        double[] noise2 = new double[]{0.5,0.2,0.1,0.2};
//        Double[][] arr2 = new Double[][]{{null, null, null,  1.0, null, null},
//                                        {null, null, null, -1.0, null,  1.0},
//                                        {-1.0, null, null, -1.0, null,  4.0},
//                                        {null,  1.0, null, -1.0, null,  1.0},
//                                        {null,  2.0, null, -1.0, null,  3.0},
//                                        { 0.0, null, null, -1.0, null,  1.0}};


//        double discount = 0.9;
//        double[] noise = new double[]{0.8,0.1,0,0.1};
//        Double[][] arr = new Double[][]{{null, null, null, 1.0},
//                                        {null, Double.MIN_VALUE, null, -1.0},
//                                        {null, null, null, null}};


        System.out.println("Graph 1 : ");
        System.out.println("ValueIteration Result : ");
        ValueIteration(discount1, noise1, arr1);
        System.out.println();
        System.out.println("PolicyIteration Result : ");
        PolicyIteration(discount1, noise1, arr1);

//        System.out.println();
//        System.out.println();
//        System.out.println();
//
//        System.out.println("Graph 2 : ");
//        System.out.println("ValueIteration Result : ");
//        ValueIteration(discount2, noise2, arr2);
//        System.out.println();
//        System.out.println("PolicyIteration Result : ");
//        PolicyIteration(discount2, noise2, arr2);
    }

    //Vaule Iteration
    private static void ValueIteration(double d, double[] n, Double[][] arr){

        //every position can go four directions, and each direction has noises
        int[] row = new int[]{-1,0,1,0};
        int[] col = new int[]{0,1,0,-1};

        int lrow = arr.length, lcol = arr[0].length;

        // each position saves five value, the first four represent four direction values, the last one is the maximum of previous four values
        double[][][] res = new double[lrow][lcol][5];
        double[][][] temp = new double[lrow][lcol][5];

        //count the times iteration
        int num = 0;

        do {
            num++;
            copy(res, temp);  // if the result of two times are different, we need to copy the new one to result matrix, and then calculate the new value again
            for(int i = 0; i < lrow; i++){
                for(int j = 0; j < lcol; j++){

                    if(arr[i][j] != null && arr[i][j] != Double.MIN_VALUE) continue;  //if is wall or final number, we do not need to calculate then

                    for(int k = 0; k < 4; k++){  //four directions
                        double val = 0;
                        for(int l = 0; l < 4; l++){  //may have noises, so we neet to calculate four possibilities
                            int r = i + row[(k + l) % 4];
                            int c = j + col[(k + l) % 4];
                            if(r < 0 || c < 0 || r >= lrow || c >= lcol || (arr[r][c] != null && arr[r][c] == Double.MIN_VALUE)) val += d * n[l] * res[i][j][4];
                            else if(arr[r][c] != null) val += d * n[l] * arr[r][c];  // if it is a exit position, go , count exit num
                            else val += d * n[l] * res[r][c][4];  // if it is a wall, stay , count itself
                        }
                        temp[i][j][k] = val;
                    }
                    temp[i][j][4] = Math.max(Math.max(temp[i][j][0], temp[i][j][1]), Math.max(temp[i][j][2], temp[i][j][3]));  // calculate the maximum result in four situations
                }
            }
        } while(!same(res, temp));

//        DecimalFormat df = new DecimalFormat("#0.00");
//
//        for(int i = 0; i < lrow; i++){
//            for(int j = 0; j < lcol; j++){
//                if(arr[i][j] == null) System.out.print(df.format(res[i][j][4]) + " ");
//                else if(arr[i][j] == Double.MIN_VALUE) System.out.print("XXXX" + " ");
//                else System.out.print(df.format(arr[i][j]) + " ");
//            }
//            System.out.println();
//        }
        System.out.println("Times Running:" + num + " * 4" );
    }


    //Policy Iteration
    private static void PolicyIteration(double d, double[] n, Double[][] arr){

        int[] row = new int[]{-1,0,1,0};
        int[] col = new int[]{0,1,0,-1};

        int lrow = arr.length, lcol = arr[0].length;

        // each position saves two value, the first represents the direction, the next is values
        double[][][] res = new double[lrow][lcol][2];
        double[][][] temp = new double[lrow][lcol][2];
        double[][][] dir = new double[lrow][lcol][2];

        int num = 0;

        // we need to do two iterations, we set a initial direction, then we calculate the value until converge, then we change the direction, do it again and again until the first step converge
        do {
            copy(res, dir);
            copy(temp, dir);

            do {
                num++;
                copy(res, temp);
                //calculate the final converge value
                for(int i = 0; i < lrow; i++){
                    for(int j = 0; j < lcol; j++){
                        if(arr[i][j] != null && arr[i][j] != Double.MIN_VALUE) continue;
                        double val = 0;
                        int k = (int) res[i][j][0];
                        for(int l = 0; l < 4; l++) {
                            int r = i + row[(k + l) % 4];
                            int c = j + col[(k + l) % 4];
                            if (r < 0 || c < 0 || r >= lrow || c >= lcol || (arr[r][c] != null && arr[r][c] == Double.MIN_VALUE))
                                val += d * n[l] * res[i][j][1];
                            else if (arr[r][c] != null) val += d * n[l] * arr[r][c];
                            else val += d * n[l] * res[r][c][1];
                        }
                        temp[i][j][1] = val;
                    }
                }
            } while(!same(res, temp));

            // calculate the best direction based on last converge value
            for(int i = 0; i < lrow; i++){
                for(int j = 0; j < lcol; j++){
                    if(arr[i][j] != null && arr[i][j] != Double.MIN_VALUE) continue;

                    int dire = -1;
                    double tt = Integer.MIN_VALUE;

                    for(int k = 0; k < 4; k++){

                        double val = 0;
                        for(int l = 0; l < 4; l++){
                            int r = i + row[(k + l) % 4];
                            int c = j + col[(k + l) % 4];
                            if(r < 0 || c < 0 || r >= lrow || c >= lcol || (arr[r][c] != null && arr[r][c] == Double.MIN_VALUE))
                                val += d * n[l] * res[i][j][1];
                            else if(arr[r][c] != null) val += d * n[l] * arr[r][c];
                            else val += d * n[l] * res[r][c][1];
                        }
                        if(val > tt) {
                            tt = val;
                            dire = k;
                        }

                    }
                    dir[i][j][0] = dire;
                    dir[i][j][1] = tt;
                }
            }

        } while(!same(res, dir));

//        DecimalFormat df = new DecimalFormat("#0.00");
//
//        for(int i = 0; i < lrow; i++){
//            for(int j = 0; j < lcol; j++){
//                if(arr[i][j] == null) System.out.print(df.format(res[i][j][1]) + " ");
//                else if(arr[i][j] == Double.MIN_VALUE) System.out.print("XXXX" + " ");
//                else System.out.print(df.format(arr[i][j]) + " ");
//            }
//            System.out.println();
//        }

        System.out.println("Times Running:" + num);
    }

    //judge whether two matrix are same, which means it is converge now
    private static boolean same(double[][][] now, double[][][] temp){
        for(int i = 0; i < temp.length; i++){
            for(int j = 0; j < temp[0].length; j++){
                for(int k = 0; k < temp[0][0].length; k++){
                    if(Math.abs(temp[i][j][k] - now[i][j][k]) > 0.0001){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // copy matrix, put the new value to the old matrix
    private static void copy(double[][][] now, double[][][] temp){
        for(int i = 0; i < temp.length; i++){
            for(int j = 0; j < temp[0].length; j++){
                for(int k = 0; k < temp[0][0].length; k++){
                    now[i][j][k] = temp[i][j][k];
                }
            }
        }
    }
}