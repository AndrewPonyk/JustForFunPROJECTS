package iteratorpatterngeneric;

public interface Aggregate<T> {
    Iterator iterator();
    int count();
    T get(int index);
    void add(T object);
}
