package Kuboid.manager.utils;

public class Pair<T, U> {

    public T p1;
    public U p2;

    public Pair(T a, U b) {
        this.p1 = a;
        this.p2 = b;
    }

    public void setValue(T a, U b) {
        this.p1 = a;
        this.p2 = b;
    }

    public Pair getValue() {
        return this;
    }
}
