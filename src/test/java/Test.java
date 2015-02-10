import java.util.Random;

public class Test {
    static int   result      = 0;                                        //  最终结果
    static int[] wallHeights = new int[] { 1, 6, 1, 2, 3, 4, 100, 1, 9 }; //  表示所有的墙的高度

    public static void main(String[] args) {
        Random r = new Random();
        wallHeights = new int[500000];
        for (int i = 0; i < 500000; i++) {
            wallHeights[i] = r.nextInt(100);
        }
        long start = System.currentTimeMillis();
        process(0, wallHeights.length - 1);
        System.out.println("CALC 1: " + result);
        System.out.println("CALC 1: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        resolve();
        System.out.println("CALC 2: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        System.out.println("CALC 3: " + calculate(wallHeights));
        System.out.println("CALC 3: " + (System.currentTimeMillis() - start));
    }
    
    public static int calculate(int [] testcase){
        int p_l = 0;
        int p_r = testcase.length - 1;
        int max_l = testcase[p_l];
        int max_r = testcase[p_r];
 
        int volume = 0;
        while (p_r > p_l) {
            if (max_l < max_r){
                p_l++;
                if (testcase[p_l] >= max_l){
                    max_l = testcase[p_l];
                }else{
                    volume += max_l - testcase[p_l];
                }
            }else{
                p_r--;
                if (testcase[p_r] >= max_r){
                    max_r = testcase[p_r];
                }else{
                    volume += max_r - testcase[p_r];
                }
            }
        }
 
        return volume;
    }
    public static void resolve() {
        // 本层第一个块
        int first,last;
        // 从第1层开始
        int cntHeight = 1;
        int allCount = 0;
        do {
            // 本层第一个块
            first = Integer.MAX_VALUE;
            // 本层最后一个块
            last = Integer.MIN_VALUE;
            for (int i = 0; i < wallHeights.length; i++) {
                if (wallHeights[i] >= cntHeight) {
                    // 找到第一块
                    first = i;
                    break;
                }
            }
            for (int i = wallHeights.length - 1; i >= 0; i--) {
                if (wallHeights[i] >= cntHeight) {
                    // 找到最后一块
                    last = i;
                    break;
                }
            }
            if (first < last) {
                for (int i = first; i < last; i++) {
                    if (wallHeights[i] < cntHeight) {
                        // 可以放入水的時候
                        allCount++;
                    }
                }
            } else {
                break;
            }
            cntHeight++;
            // 当first<last的时候，说明还至少有两道墙存在
        } while (true);
        System.out.println("CALC 2: " + allCount);
    }

    public static void process(int start, int end) {
        //  first：start和end之间最高的墙
        //  second：start和end之间第二高的墙
        int first = 0, second = 0;
        //  firstIndex：第一高的墙在wallHeights中的索引
        //  secondIndex：第二高的墙在wallHeights中的索引
        int firstIndex = 0, secondIndex = 0;
        //  两堵墙必须至少有一堵墙的距离
        if (end - start <= 1)
            return;
        //  开始获取第一高和第二高墙的砖数
        for (int i = start; i <= end; i++) {
            if (wallHeights[i] > first) {
                second = first;
                secondIndex = firstIndex;
                first = wallHeights[i];
                firstIndex = i;
            } else if (wallHeights[i] > second) {
                second = wallHeights[i];
                secondIndex = i;
            }
        }

        //  获取左侧墙的索引
        int startIndex = Math.min(firstIndex, secondIndex);
        //  获取右侧墙的索引
        int endIndex = Math.max(firstIndex, secondIndex);
        //  计算距离
        int distance = endIndex - startIndex;
        //  如果第一高的墙和第二高的墙之间至少有一堵墙，那么开始计算这两堵墙之间可以放多少个单位的水
        if (distance > 1) {
            result = result + (distance - 1) * second;
            //  减去这两堵墙之间的砖数
            for (int i = startIndex + 1; i < endIndex; i++) {
                result -= wallHeights[i];
            }

        }
        //  开始递归处理左侧墙距离开始位置能放多少水
        process(start, startIndex);
        //  开始递归处理右侧墙距离结束位置能放多少水
        process(endIndex, end);
    }

}