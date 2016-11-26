package iteratorpatterngeneric;

import java.util.NoSuchElementException;

public class ConcreteIterator<T> implements Iterator<T> {
    private final Aggregate<T> aggregate;
    private int current;

    public ConcreteIterator(Aggregate aggregate) {
        this.aggregate = aggregate;
        current = 0;
    }

    @Override
    public T first() {
        return aggregate.get(0);
    }

    @Override
    public T next() {
        if(current <= (aggregate.count()-1)){
            return aggregate.get(current++);
        }
        throw new NoSuchElementException("No next element!");
    }

    @Override
    public Boolean isDone() {
        return current >= (aggregate.count());
    }

    @Override
    public T currentItem() {
        return aggregate.get(current);
    }
}
