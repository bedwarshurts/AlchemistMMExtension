package me.bedwarshurts.mmextension.skills.mechanics.list;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

public class ListHandler<T> {

    private final String name;
    private final ArrayList<T> registry = new ArrayList<>();
    private final VariableTypes type;

    private static final Map<String, ListHandler<?>> HANDLERS = new WeakHashMap<>();

    public ListHandler(String name, VariableTypes type) {
        this.name = name;
        this.type = type;

        HANDLERS.put(name, this);
    }

    public T get(int index) {
        return registry.get(index);
    }

    public String getName() {
        return this.name;
    }

    protected ArrayList<T> getRegistry() {
        return this.registry;
    }

    public VariableTypes getType() {
        return this.type;
    }

    public void register(T item) {
        registry.add(item);
    }

    public void replace(int index, T item) {
        registry.set(index, item);
    }

    public void remove(int index) {
        registry.remove(index);
    }

    @SuppressWarnings("unchecked")
    public static synchronized <U> ListHandler<U> getListHandler(String name) {
        return (ListHandler<U>) HANDLERS.get(name);
    }

    public static synchronized <U> void createListHandler(String name, VariableTypes type) {
        HANDLERS.put(name, new ListHandler<U>(name, type));
    }
}