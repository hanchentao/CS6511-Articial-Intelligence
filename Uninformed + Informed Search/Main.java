import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        File file = new File("C:\\Users\\ALI\\IdeaProjects\\hanbaobao\\src\\graphs");
        File[] arr = file.listFiles();

        int idx = 1;
        for(int i = 0; i < arr.length; i++){
            Map<Integer, List<int[]>> map = new HashMap<>(); // build map
            Map<Integer, Integer> square = new HashMap<>();  // put vertex into square

            String pathname1 = arr[i] + "/e.txt";
            String pathname2 = arr[i] + "/v.txt";

            try {
                File filename = new File(pathname1);
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
                BufferedReader br = new BufferedReader(reader);

                String line = "";
                line = br.readLine();
                while (line != null) {
                    line = br.readLine();
                    if(line == null) continue;
                    String[] s = line.split(",");
                    int a = Integer.parseInt(s[0]);
                    int b = Integer.parseInt(s[1]);
                    int c = Integer.parseInt(s[2]);
                    if(!map.containsKey(a)) map.put(a, new LinkedList<>());
                    map.get(a).add(new int[]{b, c});
                    if(!map.containsKey(b)) map.put(b, new LinkedList<>());
                    map.get(b).add(new int[]{a, c});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                File filename = new File(pathname2);
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
                BufferedReader br = new BufferedReader(reader);

                String line = "";
                line = br.readLine();
                while (line != null) {
                    line = br.readLine();
                    if(line == null || line.charAt(0) == '#') continue;
                    String[] s = line.split(",");
                    int a = Integer.parseInt(s[0]);
                    int b = Integer.parseInt(s[1]);
                    square.put(a, b);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Random random = new Random();
            int val1 = random.nextInt(map.size());  // choose start point and end point randomly
            int val2 = random.nextInt(map.size());

            long startTime1 = System.currentTimeMillis();  // sava the time when start and end
            int[] res1 = Dijkstra(map, val1, val2);  //run the Dijkstra
            long endTime1 = System.currentTimeMillis();
            int[] res2 = Astar(map, square, val1, val2);  //run the Astar
            long endTime2 = System.currentTimeMillis();

            System.out.println("Graph" + idx++ + ": ");
            System.out.println("          Start point is :" + val1 + "          End point is :" + val2);
            System.out.print("          Dijkstra: " + " Result = " + res1[0]);
            System.out.println("          Steps = " + res1[1] + "          Runtime = " + (endTime1 - startTime1) + "ms");
            System.out.print("          Astar: " + " Result = " + res2[0]);
            System.out.println("          Steps = " + res2[1] + "          Runtime = " + (endTime2 - endTime1) + "ms");
            System.out.println("");

        }

        return;

    }

    private static int[] Dijkstra(Map<Integer, List<int[]>> graph, int sta, int end){  // the Astar is based on this algorithm, so I do not want to write something here

        Map<Integer, Integer> map = new HashMap<>();
        PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> map.get(a) - map.get(b));

        map.put(sta, 0);
        heap.add(sta);
        int steps = 0;

        while(!heap.isEmpty()){
            int temp = heap.remove();
            steps++;
            int dis = map.get(temp);
            if(temp == end) return new int[]{dis, steps};
            List<int[]> list = graph.get(temp);

            for(int[] arr : list){
                if(!map.containsKey(arr[0]) || map.get(arr[0]) > dis + arr[1]){
                    map.put(arr[0], dis + arr[1]);
                    heap.add(arr[0]);
                }
            }
        }
        return new int[]{-1, -1};
    }

    private static int[] Astar(Map<Integer, List<int[]>> graph, Map<Integer, Integer> square, int sta, int end){

        Map<Integer, Double> map = new HashMap<>();  // A map to save the calculate cost between now point and end point
        Map<Integer, Integer> res = new HashMap<>();  // A map to save the real cost between now point and end point
        PriorityQueue<Integer> heap = new PriorityQueue<>(Comparator.comparingDouble(map::get));  // which can always provide the shortest calculate cost for me

        map.put(sta, getDis(square, sta, end));
        res.put(sta, 0);
        heap.add(sta);
        int steps = 0;

        while(!heap.isEmpty()) {
            int temp = heap.remove();
            steps++;
            if (temp == end) {  // when the end point deque, It means that we find the shortest path.
                return new int[]{res.get(temp), steps};
            }
            List<int[]> list = graph.get(temp);

            for (int[] arr : list) {
                double distance = getDis(square, arr[0], end);
                int dis = res.get(temp) + arr[1];
                if (!map.containsKey(arr[0]) || map.get(arr[0]) > dis + distance) {
                    res.put(arr[0], dis);
                    map.put(arr[0], dis + distance);
                    heap.add(arr[0]);
                }
            }
        }
        return new int[]{-1, -1};
    }

    private static double getDis(Map<Integer, Integer> square, int a, int b){  // calculate the euclidean distance
        int t1 = square.get(a);
        int t2 = square.get(b);
        int v1 = 0, v2 = 0;
        if(Math.abs(t1 % 10 - t2 % 10) > 2){
            v1 = Math.abs(t1 % 10 - t2 % 10) - 1;
        }
        if(Math.abs(t1/ 10 - t2 / 10) > 2){
            v2 = Math.abs(t1/ 10 - t2 / 10) - 1;
        }
        return Math.sqrt(v1 * v1 + v2 * v2);
    }
}
