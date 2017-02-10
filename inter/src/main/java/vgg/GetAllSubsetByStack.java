package vgg;

import java.util.Stack;

public class GetAllSubsetByStack {

    public static void main(String[] args) {
        int[] incoming = {100, 100, 225, 300, 473, 80};
        int[] outgoing = {180, 773, 225, 100};

        int[] DATA = { 5, 5, 15, 2, 7, 1, 3, 4, 5, 6,  8, 9, 10, 11, 13,
                14 };
        GetAllSubsetByStack get = new GetAllSubsetByStack();
        get.populateSubset(DATA, 0, DATA.length);

    }

    public static final int TARGET_SUM = 7;

    private Stack<Integer> stack = new Stack<Integer>();
    private Stack<Integer> stackIndexes = new Stack<Integer>();
    private int sumInStack = 0;


    public void populateSubset(int[] data, int fromIndex, int endIndex) {
        if (sumInStack == TARGET_SUM) {
            stack.forEach(System.out::println);
            stackIndexes.forEach(e->{System.out.println("["+e+"]");});
            System.out.println("======");
        }

        for (int currentIndex = fromIndex; currentIndex < endIndex; currentIndex++) {

            if (sumInStack + data[currentIndex] <= TARGET_SUM) {
                stack.push(data[currentIndex]);
                stackIndexes.push(currentIndex);

                sumInStack += data[currentIndex];

                populateSubset(data, currentIndex + 1, endIndex);
                sumInStack -= (Integer) stack.pop();
                stackIndexes.pop();
            }
        }
    }
}