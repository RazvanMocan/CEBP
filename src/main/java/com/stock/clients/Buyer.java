package com.stock.clients;

import com.stock.transactions.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Buyer extends Client {
    public Buyer(Socket socket, BufferedReader in) throws IOException {
        super(socket, in);
        this.type = "wants to buy";
    }

    @Override
    protected void removeTransactions(List<Transaction> myTransactions) {
        broker.removeAllBuyRequests(myTransactions);
    }

    @Override
    protected void doTransaction(Transaction buy) {
        mytransactionList.add(buy);
        broker.addBuyRequest(buy);

        boolean searching = true;
        Transaction sell;

        while ( (sell = broker.getSellOffer(buy.getPrice())) != null && ( searching || buy.getAmount() != 0 ) )
            searching = isSearching(sell, buy);

        if (buy.getAmount() == 0)
            mytransactionList.remove(buy);
    }
}
