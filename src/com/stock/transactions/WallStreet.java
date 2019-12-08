package com.stock.transactions;
import java.util.ArrayList;
import java.util.List;

import com.stock.helper.Observer;
import com.stock.helper.ProtectedList;

public class WallStreet {
	
    private ProtectedList<Transaction> sellOffers;
    private ProtectedList<Transaction> buyRequests;
    private ProtectedList<Transaction> terminated;
    
    private ProtectedList<Observer> observerCollection;

    public WallStreet() {
        sellOffers = new ProtectedList<>();
        buyRequests = new ProtectedList<>();
        terminated = new ProtectedList<>();
        observerCollection = new ProtectedList<>();
    }
    
    public  void registerObserver(Observer o) {
        if (!observerCollection.contains(o))
            observerCollection.add(o);
    }

    public  void unregisterObserver(Observer o) {
        if (observerCollection.contains(o))
            observerCollection.remove(o);
    }

    private void getObserversToNotify(List<Transaction> transactions) {

        for (Observer observer : observerCollection.getList()) {
            for (Transaction t : transactions) {
                if (observer.verifyReq(t.getTransType(), t.getPrice())) {
                    observer.update(t.getTransType());
                    break;
                }
            }
        }
    }

    public List<Transaction> getSellOffers() {
        return sellOffers.getList();
    }

    public List<Transaction> getBuyRequests() {
        return buyRequests.getList();
    }
    
    public List<Transaction> getAllOffers() {
        List<Transaction> all = getSellOffers();
        all.addAll(getBuyRequests());
        return all;
    }
    
    public List<Transaction> getTerminated() {
        return terminated.getList();
    }

    public void addSellOffer(Transaction t) {
        sellOffers.add(t);
        getObserversToNotify(buyRequests.getList());
        getObserversToNotify(sellOffers.getList());
    }

    public void addBuyRequest(Transaction t) {
        buyRequests.add(t);
    }

    public synchronized void removeAllSellOffers(List<Transaction> list) {
        for (Transaction t : list)
            sellOffers.remove(t);
    }

    public synchronized void removeAllBuyRequests(List<Transaction> list) {
        for (Transaction t : list)
            buyRequests.remove(t);
    }

    public Transaction getSellOffer(float price) {
        return getOffer(price, sellOffers.getList());
    }

    public Transaction getBuyOffer(float price) {
        return getOffer(price, buyRequests.getList());
    }
    
    public boolean removeBuyRequest(Transaction myTransaction) {
        return buyRequests.remove(myTransaction);
    }

    public boolean removeSellOffer(Transaction myTransaction) {
        return sellOffers.remove(myTransaction);
    }

    private Transaction getOffer(float price, List<Transaction> offers) {
        for (Transaction offer : offers) {
            if (offer.getPrice() == price)
                return offer;
        }
        return null;
    }

    public Transaction doTransaction(Transaction sell, Transaction buy) {
        return new Transaction(sell, buy);
    }

    public synchronized boolean finishTransaction(Transaction t, Transaction sell, Transaction buy) {
        if (!sellOffers.contains(sell) || !buyRequests.contains(buy))
            return false;
        sellOffers.remove(sell);
        buyRequests.remove(buy);
        terminated.add(t);
        List<Transaction> l = new ArrayList<>();
        l.add(t);
        getObserversToNotify(l);
        return true;
    }
    
}
