import com.google.common.collect.AbstractIterator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * Created by arttsiom.chuiko on 12/01/2017.
 */
public class Test {

    public static Random r = new Random();

    public static void main(String[] args) {

        System.out.println(LocalDate.ofEpochDay(17152));

        final String line = "193309,340,15,15,15,15,12,11,11,11,11,11,11,11,11,11,22,11,22,12,11,22,12,21,10,8,16,8,9,17,9,8,16,8,9,19,10,20,10,9,18,9,8,16,9,18,8,9,16,8,9,17,8,9,16,8,9,17,8,9,20,10,24,26,13,26,12,12,24,12,22,11,22,11,23,11,22,11,23,12,18,-1094,10,10,10,10,10,10,6,5,6,5,6,6,6,6,5,5,6,6,6,6,6,6,6,5,6,6,7,6,15,6,7,7,19,10,10,18,9,20,10,10,19,9,8,9,17,8,8,18,8,9,17,8,9,17,9,8,14,6,6,7,6,13,6,7,6,7,12,7,6,6,7,12,7,6,7,12,5,5,5,5,6,12,7,7,7,13,6,6,6,6,14,7,8,15,10,10,21,10,11,22,10,10,14,12,23,11,23,11,22,11,23,12,18,16,,17152,80,17150,,0,,67,80,12345,,1,,2,,1,,2,,1,6,6,67,4,7,6,107,4,,1,,3,,1,,2,,1,90,6,97,4,,3,,1,,2,156,6,17,4,,2,,4,88,6,21,4,32,6,36,4,,1,,";

        int firstCommaIndex = line.indexOf(',');

        String routeIdString = line.substring(0, firstCommaIndex);
        System.out.println("RouteId: " + routeIdString);

        String dataString = line.substring(firstCommaIndex + 1);
        // splitting data to 5 blocks:
        // 1 - times data
        // 2 - valid from
        // 3 - valid to
        // 4 - days of week
        // 5 - intervals
        String[] blocks = dataString.split(",,", 5);

        String[] timesData = blocks[0].split(",");
        int maxIndex = timesData.length;

        final List<Long> table = getTimeTable(timesData, blocks[4]);

        final List<Long> f = getValidDates(blocks[1], maxIndex);
        final List<Long> t = getValidDates(blocks[2], maxIndex);

        final List<Long> d = getValidDates(blocks[3], maxIndex);


        System.out.println("Table: " + table);
    }

    private static List<Long> getTimeTable(String[] timesData, String intervals) {
        int timesDataLength = timesData.length;

        List<Long> timetable = new ArrayList<>();
        long previousTime = 0;
        for (String token : timesData) {
            previousTime += Integer.valueOf(token);
            timetable.add(previousTime);
        }

        for (String intervalBlocks : intervals.split(",,", -1)) {
            if (intervalBlocks.isEmpty()) {
                continue;
            }
            String[] deltas = intervalBlocks.split(",", -1);
            int delta = 0;
            int left = 0;
            for (int i = 0; i < deltas.length; i++) {
                if (left <= 0) {
                    delta = 5;
                    left = timesDataLength;
                }

                delta += Integer.valueOf(deltas[i++]) - 5;

                int repeatTimes = (i == deltas.length) ? left : Integer.valueOf(deltas[i]);
                left -= repeatTimes;

                for (int j = 0; j < repeatTimes; j++) {
                    timetable.add(timetable.get(timetable.size() - timesDataLength) + delta);
                }
            }
        }

        return timetable;
    }

    private static List<Long> getValidDates(String block, int maxIndex) {
        return StreamSupport.stream(Spliterators.spliterator(new DataUnpackingIterator(block, maxIndex), maxIndex, 0), false).map(Long::valueOf)
                .collect(toList());
    }
}

class DataUnpackingIterator extends AbstractIterator<String> {

    public static final String VALUE_SEPARATOR = ",";

    private final String[] values;
    private final int[] times;
    private int arrayIndex;
    private int indexInArray;

    public DataUnpackingIterator(String dataString, int totalCount) {
        String[] tokens = dataString.split(VALUE_SEPARATOR);
        int size = tokens.length / 2 + 1;

        values = new String[size];
        for (int i = 0; i < size; i++) {
            values[i] = tokens[i * 2];
        }

        times = new int[size];
        for (int i = 0; i < size - 1; i++) {
            times[i] = Integer.valueOf(tokens[i * 2 + 1]);
        }

        int sum = 0;
        for (int time : times) {
            sum += time;
        }

        times[size - 1] = totalCount - sum;
    }

    @Override
    protected String computeNext() {
        if (arrayIndex >= values.length) {
            return endOfData();
        }

        String result = values[arrayIndex];

        if (++indexInArray >= times[arrayIndex]) {
            arrayIndex++;
            indexInArray = 0;
        }

        return result;
    }
}
