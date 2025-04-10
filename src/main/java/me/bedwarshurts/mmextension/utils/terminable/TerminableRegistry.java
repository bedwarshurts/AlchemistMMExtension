package me.bedwarshurts.mmextension.utils.terminable;

import me.bedwarshurts.mmextension.utils.exceptions.TerminableStorageException;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TerminableRegistry implements TerminableConsumer, Terminable {
    private final Queue<AutoCloseable> terminables = new ConcurrentLinkedQueue<>();

    public TerminableConsumer with(AutoCloseable autoCloseable) {
        this.terminables.add(autoCloseable);
        return this;
    }

    public void close() {
        AutoCloseable terminable;
        while ((terminable = this.terminables.poll()) != null) {
            try {
                terminable.close();
            } catch (Exception e) {
                throw new TerminableStorageException("Failed to close terminable: " + terminable + e);
            }
        }
    }

    public ArrayList<Exception> closeSilently() {
        ArrayList<Exception> exceptions = new ArrayList<>();
        AutoCloseable terminable;
        while ((terminable = this.terminables.poll()) != null) {
            try {
                terminable.close();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        return exceptions;
    }
}
