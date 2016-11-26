package lohika;

public class StringContainter implements Container {
    private String [] data;

    public StringContainter(String str, String regex) {
        data = str.split("(?=" + regex + ")");
    }

    @Override
    public Iterator iterator() {
        return new StringIterator(this);
    }

    @Override
    public int count() {
        return data.length;
    }

    @Override
    public String get(int index) {
        if(index>=0 && index< data.length){
            return data[index];
        }
        throw new IllegalArgumentException("Wrong index passed");
    }
}
