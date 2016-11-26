package iteratorpatterngeneric;

import java.util.ArrayList;

public class ConcreteAggregate<String> implements Aggregate<String>{

    private ArrayList<String> items = new ArrayList<>();

    @Override
    public Iterator iterator() {
        return new ConcreteIterator(this);
    }

    @Override
    public int count() {
        return items.size();
    }

    @Override
    public String get(int index) {
        return items.get(index);
    }

    @Override
    public void add(String object) {
        items.add(object);
    }
}
