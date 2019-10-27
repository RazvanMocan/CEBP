package com.stock.clients;

import com.stock.helper.Observer;
import com.stock.transactions.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Seller extends Client implements Observer {
    public  Seller(Socket socket, BufferedReader in) throws IOException {
        super(socket, in);
        this.type = "sells";
    }

    @Override
    protected void doTransaction(Transaction sell) {
        broker.addSellOffer(sell);

        boolean searching = true;
        Transaction buy;

        while ( (buy = broker.getBuyOffer(sell.getPrice())) != null && searching )
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
