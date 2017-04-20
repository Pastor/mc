package mc.engine;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public final class ConcurrentObject<Item> {
    private final ReadWriteLock locker = new ReentrantReadWriteLock();
    private final Lock writeLock = locker.writeLock();
    private final Lock readLock = locker.readLock();
    private final Item item;

    public ConcurrentObject(Item item) {
        this.item = item;
    }

    public <R> R write(Function<Item, R> write) {
        writeLock.lock();
        try {
            return write.apply(item);
        } finally {
            writeLock.unlock();
        }
    }

    public <R> R read(Function<Item, R> read) {
        readLock.lock();
        try {
            return read.apply(item);
        } finally {
            readLock.unlock();
        }
    }
}
