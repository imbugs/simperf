package simperf.sample.util;


import java.util.ArrayList;
import java.util.List;

public class StdAvg {
    //平均值
    public static double getAvg(List<Double> numbers) {
        int sum = 0;
        for (int i = 0; i < numbers.size(); i++) {
            sum += numbers.get(i);
        }
        return (double) (sum / numbers.size());
    }

    //标准差
    public static double getStd(List<Double> numbers) {
        double sum = 0;
        double avg = getAvg(numbers);
        for (int i = 0; i < numbers.size(); i++) {
            sum += (numbers.get(i) - avg) * (numbers.get(i) - avg);
        }
        double std = Math.sqrt((double) (sum / (numbers.size() - 1)));
        return std;
    }

    public static void main(String[] args) {
        List<Double> numbers = new ArrayList<Double>();
        numbers.add(73d); //73、72、71、69、68、67
        numbers.add(72d);
        numbers.add(71d);
        numbers.add(69d);
        numbers.add(68d);
        numbers.add(67d);
        System.out.println(StdAvg.getStd(numbers));
    }

}
