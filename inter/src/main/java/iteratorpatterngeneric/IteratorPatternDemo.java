package iteratorpatterngeneric;

public class IteratorPatternDemo {
    public static void main(String[] args) {
        ConcreteAggregate<String> aggregate = new ConcreteAggregate();
        aggregate.add("First element");
        aggregate.add("Second element");
        aggregate.add("Fourth element");

        Iterator aggregateIterator = aggregate.iterator();
        while (!aggregateIterator.isDone()){
            System.out.println(aggregateIterator.next());
        }

    }
}
