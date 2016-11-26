package iteratorpatterngeneric;

/**
 * Created by andrii on 22.11.16.
 */
public interface Iterator<T> {
    T first();
    T next();
    Boolean isDone();
    T currentItem();
}
