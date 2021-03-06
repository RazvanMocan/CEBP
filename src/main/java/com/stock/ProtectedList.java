package com.stock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProtectedList<K> {
    private ArrayList<K> list;

    private static int nrReaders = 0;
    private final Lock read = new ReentrantLock(true);
    private final Lock write = new ReentrantLock(true);

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

    public boolean remove(K t) {
        startWrite();
        boolean result = list.remove(t);
        endWrite();
        return result;
    }
}
