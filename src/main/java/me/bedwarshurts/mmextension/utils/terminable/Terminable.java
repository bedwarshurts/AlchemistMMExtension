package me.bedwarshurts.mmextension.utils.terminable;

public interface Terminable extends AutoCloseable {

    void close();
}