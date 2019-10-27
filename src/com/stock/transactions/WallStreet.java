package com.stock.transactions;
import com.stock.helper.ProtectedList;

import java.util.List;

public class WallStreet {
    private ProtectedList<Transaction> sellOffers;
    private ProtectedList<Transaction> buyRequests;
    private ProtectedList<Transaction> terminated;

    public WallStreet() {
        sellOffers = new ProtectedList<>();
        buyRequests = new ProtectedList<>();
        terminated = new ProtectedList<>();
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
        sellOffers.add(t);
    }

    public void addBuyRequest(Transaction t) {
        buyRequests.add(t);
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
        return true;
    }
}
