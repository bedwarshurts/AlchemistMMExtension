package me.bedwarshurts.mmextension.utils.events;

import java.util.ArrayList;
import java.util.List;

public interface Terminable {
    List<Runnable> tasks = new ArrayList<>();

    default void onTerminate(Runnable task) {
        tasks.add(task);
    }

    default void terminate() {
        tasks.forEach(Runnable::run);
        tasks.clear();
    }
}