package com.stock;

import sun.awt.Mutex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtectedList<K> {
    private ArrayList<K> list;

    private static int nrReaders = 0;
    private final Mutex read = new Mutex();
    private final Mutex write = new Mutex();

    public ProtectedList() {
        this.list = new ArrayList<>();
    }

    private void startRead() {
        read.lock();
        nrReaders++;
        if (nrReaders == 1)
            write.lock();
        read.unlock();
    }

    private void endRead() {
        read.lock();
        nrReaders--;
        if (nrReaders == 0)
            write.unlock();
        read.unlock();
    }

    private void startWrite() {
        write.lock();
    }

    private void endWrite() {
        write.unlock();
    }

    public List<K> getList() {
        startRead();
        List<K> l = new ArrayList<>(list);
        endRead();
        return l;
    }

    public void add(K t) {
        startWrite();
        list.add(t);
        endWrite();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(K t) {
        startRead();
        boolean answer = list.contains(t);
        endRead();
        return answer;
    }

    public void remove(K t) {
        startWrite();
        list.remove(t);
        endWrite();
    }
}
