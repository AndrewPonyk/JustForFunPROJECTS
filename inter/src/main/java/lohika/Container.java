package lohika;


public interface Container {
    Iterator iterator();
    int count();
    String get(int index);
}
