package com.stock.transactions;
import com.stock.ProtectedList;

import java.util.ArrayList;
import java.util.List;

public class WallStreet {
    private ProtectedList<Transaction> sellOffers;
    private ProtectedList<Transaction> buyRequests;
    private ProtectedList<Transaction> terminated;
    private ProtectedList<Transaction> allOffers;


    public WallStreet() {
        sellOffers = new ProtectedList<>();
        buyRequests = new ProtectedList<>();
        terminated = new ProtectedList<>();
        allOffers = new ProtectedList<>();
    }

    public List<Transaction> getSellOffers() {
        return sellOffers.getList();
    }

    public List<Transaction> getBuyRequests() {
        return buyRequests.getList();
    }
    
    public List<Transaction> getAllOffers() {
        return allOffers.getList();
    }

    public List<Transaction> getAllOffersMine(String clientName) {
    	ArrayList<Transaction> allOffersMine = new ArrayList<>();    	
        for (Transaction i:allOffers.getList())
        	{
        	if(i.getClientName().equals(clientName))
        		allOffersMine.add(i);
        	    System.out.println(clientName + "aaaaa");
        	}
        return allOffersMine;
    }    
    
    public List<Transaction> getTerminated() {
        return terminated.getList();

    }

    public void addSellOffer(Transaction t) {
    	List<Transaction> allOffersMine = getAllOffersMine(t.getClientName());
        for (Transaction i : allOffersMine)
        	if(i.getPrice() == t.getPrice() && i.getTransType().equals(t.getTransType()))
        		{
        		System.out.println("got here1");        		
        		sellOffers.remove(i);
        		allOffers.remove(i);
        		}
        sellOffers.add(t);
        allOffers.add(t);
    }

    public void addBuyRequest(Transaction t) {
    	List<Transaction> allOffersMine = getAllOffersMine(t.getClientName());
        for (Transaction i : allOffersMine)
        	if(i.getPrice() == t.getPrice() && i.getTransType().equals(t.getTransType()))
        		{
        		System.out.println("got here2");
        		buyRequests.remove(i);
        		allOffers.remove(i);
        		}    	
        buyRequests.add(t);
        allOffers.add(t);
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
        allOffers.remove(sell);
        allOffers.remove(buy);
        terminated.add(t);
        return true;
    }
}
