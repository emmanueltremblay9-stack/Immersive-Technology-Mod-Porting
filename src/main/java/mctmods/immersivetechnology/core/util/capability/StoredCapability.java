package mctmods.immersivetechnology.core.util.capability;

public class StoredCapability<T> {
    private final T value;

    public StoredCapability(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
