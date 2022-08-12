package com.pofa.ebcadmin.utils.process;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProcessLock implements AutoCloseable {
    private final Object id;

    public ProcessLock(Object id) {
        this.id = id;
        ProcessManager.lock(id);
    }

    public void unlock() {
        ProcessManager.unlock(id);
    }

    @Override
    public void close() {
        unlock();
    }
}
