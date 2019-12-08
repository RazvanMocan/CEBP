package com.stock.clients;

import com.stock.helper.Observer;
import com.stock.transactions.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Buyer extends Client implements Observer {
	
    public Buyer(Socket socket, BufferedReader in) throws IOException {
        super(socket, in);
        this.type = "wants to buy";
    }
    
    @Override
    public boolean verifyReq(String type, float price) {
        if (type.equals("sold to "))
            return true;
        else if(!type.equals(this.type)) {
            for (Transaction sellOffer : broker.getSellOffers()) {
                if (sellOffer.getPrice() == price)
                    return true;
            }
        }
        return false;
    }

    @Override
    protected boolean removeTransaction(Transaction myTransaction) {
        return broker.removeBuyRequest(myTransaction);
    }

    @Override
    protected void removeTransactions(List<Transaction> myTransactions) {
        broker.removeAllBuyRequests(myTransactions);
    }

    @Override
    protected void doTransaction(Transaction buy) {
    	
    	broker.registerObserver(this);
        broker.addBuyRequest(buy);
        mytransactionList.add(buy);

        boolean searching = true;
        Transaction sell;

        while ( (sell = broker.getSellOffer(buy.getPrice())) != null && searching)
            searching = isSearching(sell, buy);

        if (!searching)
            mytransactionList.remove(buy);
    }
    
    @Override
    public synchronized void update(String type) {
        if (type.equals("sold to ")) {
            System.out.println("sold finished");
            sendMsg("notifTransaction finished");
        }
        else {
            System.out.println("intrat");
            List<Transaction> min = mytransactionList;
            for (Transaction buy : min) {
                Transaction sell = broker.getSellOffer(buy.getPrice());
                if (sell != null) {
                    if (!this.isSearching(sell,buy)) {
                        if (buy.getAmount() == 0)
                        	mytransactionList.remove(buy);

                        System.out.println("Transaction done");
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    protected void unregister() {
        broker.unregisterObserver(this);
    }
    
}
