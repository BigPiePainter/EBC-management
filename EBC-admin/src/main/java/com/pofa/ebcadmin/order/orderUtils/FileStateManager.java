package com.pofa.ebcadmin.order.orderUtils;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FileStateManager {
    private static final HashMap<String, FileState> states = new HashMap<>();


    public static void newFile(String name, FileState fileState) {
        states.put(name, fileState);
    }


    public static void removeFile(String name) {
        states.remove(name);
    }


    public static HashMap<String, FileState> getStates() {
        return states;
    }

    public static FileState getState(String name) {
        return states.get(name);
    }
}
