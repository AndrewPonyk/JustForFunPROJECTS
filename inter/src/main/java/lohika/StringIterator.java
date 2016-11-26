package lohika;

import java.util.NoSuchElementException;

public class StringIterator implements Iterator{
    private final Container container;
    private int current = 0;

    public StringIterator(Container containter) {
        this.container = containter;
    }

    @Override
    public String next() {
        if(current >= container.count()){
            throw new NoSuchElementException("No next element in container!!!");
        }
        return container.get(current++);
    }

    @Override
    public Boolean hasNext() {
        return current < container.count();
    }
}