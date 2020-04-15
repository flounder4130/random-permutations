import java.util.*;
import java.util.stream.IntStream;

public class PermutationsTest {
    static int runs = 10; // number of test runs for each strategy
    static boolean detailed = true; // set this to false to only see the outcome
    static int numPersons = 100; // set the number of participants
    static int numAttempts = numPersons/2; // each person has numPersons/2 attempts

    public static void main(String[] args) {

        Map<Integer, Integer> drawers = new HashMap<>();
        testNRuns(new RandomStrategy(), drawers, runs);
        testNRuns(new SmartStrategy(), drawers, runs);
    }

    static void testNRuns(Strategy strategy, Map<Integer, Integer> drawers, int runs) {
        int lucky = 0;
        for (int run = 0; run < runs; run++) {
            if (detailed) System.out.print("Strategy:" + strategy.getClass().toString().replace("class", "") + " run: " + run);
            drawers.clear();
            populateMap(drawers);
            if (testRun(strategy, drawers)) lucky++;
        }
        System.out.print("For strategy" + strategy.getClass().toString().replace("class", "") + ": " + lucky + " successful attempts out of " + runs + "\n\n");
    }

    static boolean testRun(Strategy strategy, Map<Integer, Integer> drawers) {
        NEXT_PERSON:
        for (int person = 1; person <= numPersons; person++) {
            ArrayList<Integer> tried = new ArrayList<>();
            int lastValue = 0;
            for (int attempt = 0; attempt < numAttempts; attempt++) {
                int guess;
                guess = strategy.guess(tried, person, lastValue); // guessing the number to open
                lastValue = tryOne(drawers, guess); // opening the drawer and storing the number
                if (lastValue == person) { // checking if the number matches
                    continue NEXT_PERSON;
                }
                tried.add(guess);
            }
            if (detailed) System.out.print(" failed at person " + person + "\n");
            return false;
        }
        if (detailed) System.out.print(" success\n");
        return true;
    }

    static void populateMap(Map<Integer, Integer> map) {
        LinkedList<Integer> available = new LinkedList<>();
        IntStream.rangeClosed(1, numPersons).forEach(available::add);
        Collections.shuffle(available);
        for (int i = 1; i <= numPersons; i++) map.put(i, available.poll());
    }

    static int random() {
        return new Random().nextInt(numPersons) + 1;
    }

    static Integer tryOne(Map<Integer, Integer> drawers, int guess) {
        return drawers.get(guess);
    }
}

interface Strategy {
    Integer guess(List<Integer> tried, int person, int lastValue);
}

class RandomStrategy implements Strategy {
    public Integer guess(List<Integer> tried, int person, int lastValue) {
        int guess;
        // try a random number that has not been tried before
        do guess = PermutationsTest.random(); while (tried.contains(guess));
        return guess;
    }
}

class SmartStrategy implements Strategy {
    public Integer guess(List<Integer> tried, int person, int lastValue) {
        if (!tried.contains(person)) {
            return person;
        } else if (!tried.contains(lastValue)) {
            return lastValue;
        } else {
            int out;
            do out = PermutationsTest.random();
            while (tried.contains(out));
            return out;
        }
    }
}
