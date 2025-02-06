package factories;

public interface Factory<T> {
    T create(Object... args);
}
