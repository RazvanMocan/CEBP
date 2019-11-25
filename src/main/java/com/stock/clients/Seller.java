package com.stock.clients;

import com.stock.transactions.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Seller extends Client {
    @Override
    protected boolean removeTransaction(Transaction myTransaction) {
        return broker.removeSellOffer(myTransaction);
    }

    public  Seller(Socket socket, BufferedReader in) throws IOException {
        super(socket, in);
        this.type = "is selling";
    }

    @Override
    protected void removeTransactions(List<Transaction> myTransactions) {
        broker.removeAllSellOffers(myTransactions);
    }

    @Override
    protected void doTransaction(Transaction sell) {
        mytransactionList.add(sell);
        broker.addSellOffer(sell);
        boolean searching = true;
        Transaction buy;

        while ( (buy = broker.getBuyOffer(sell.getPrice())) != null && searching )
            searching = isSearching(sell, buy);

        if (!searching)
            mytransactionList.remove(sell);
    }
}
