package com.stock.clients;

import com.stock.transactions.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class Buyer extends Client {
    public Buyer(Socket socket, BufferedReader in) throws IOException {
        super(socket, in);
        this.type = "buys";
    }

    @Override
    protected void doTransaction(Transaction buy) {
        broker.addBuyRequest(buy);

        boolean searching = true;
        Transaction sell;

        while ( (sell = broker.getSellOffer(buy.getPrice())) != null && searching )
            searching = isSearching(sell, buy);
    }
}
