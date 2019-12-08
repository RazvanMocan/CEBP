package com.stock.transactions;
import com.stock.helper.Observer;
import com.stock.helper.ProtectedList;
import java.util.List;

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
        for (Transaction t : transactions) {
            for (Observer observer : observerCollection.getList()) {
                if (observer.verifyReq(t.getTransType(), t.getPrice()))
                        observer.update(t.getTransType());
            }
        }
    }

    //TODO use decorator pattern(maybe look up a bit) fot verifying requirements
    private synchronized void notifyObservers(Transaction t) {
        getObserversToNotify(sellOffers.getList());
        getObserversToNotify(buyRequests.getList());
        getObserversToNotify(terminated.getList());
    }
    private void notifyClients(Transaction t)
    {
        t.getClientWriter1().println("Transaction finished with " + t.getClientName2());
        t.getClientWriter2().println("Transaction finished with " + t.getClientName1());
    }
    public List<Transaction> getSellOffers() {
        return sellOffers.getList();
    }

    public List<Transaction> getBuyRequests() {
        return buyRequests.getList();
    }

    public List<Transaction> getTerminated() {
        return terminated.getList();

    }

    public void addSellOffer(Transaction t) {
        addOffer(sellOffers, t);
    }

    public void addBuyRequest(Transaction t) {
        addOffer(buyRequests, t);
    }

    private void addOffer(ProtectedList<Transaction> l, Transaction t) {
        l.add(t);
        notifyObservers(t);
    }

    public Transaction getSellOffer(float price) {
        return getOffer(price, sellOffers.getList());
    }

    public Transaction getBuyOffer(float price) {
        return getOffer(price, buyRequests.getList());
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

    public boolean finishTransaction(Transaction t, Transaction sell, Transaction buy) {
        if (!sellOffers.contains(sell) || !buyRequests.contains(buy))
            return false;
        sellOffers.remove(sell);
        buyRequests.remove(buy);
        terminated.add(t);
        notifyObservers(t);
        notifyClients(t);
        return true;
    }
}
