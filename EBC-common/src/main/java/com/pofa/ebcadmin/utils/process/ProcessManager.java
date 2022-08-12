package com.pofa.ebcadmin.utils.process;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProcessManager{
    private static final HashMap<Object, Lock> processLock = new HashMap<>();

    public static void lock(Object id) {
        if (!processLock.containsKey(id)) {
            processLock.put(id, new ReentrantLock(true));
        }
        processLock.get(id).lock();
    }

    public static void unlock(Object id) {
        processLock.get(id).unlock();
        if (processLock.get(id).tryLock()){
            processLock.remove(id);
        }
    }
}
