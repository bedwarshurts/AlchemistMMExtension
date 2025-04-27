package me.bedwarshurts.mmextension.utils.terminable;

public interface TerminableConsumer {

    TerminableConsumer with(AutoCloseable terminable);
}
