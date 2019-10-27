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

    @Override
    public List<String> getinterestedTypes() {
        return null;
    }

    @Override
    public void update() {

    }
}
