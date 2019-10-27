package com.stock.clients;

import com.stock.helper.Observer;
import com.stock.transactions.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;


public class Seller extends Client implements Observer {
    public  Seller(Socket socket, BufferedReader in) throws IOException {
        super(socket, in);
        this.type = "sells";
    }

    @Override
    public boolean verifyReq(String type, float price) {
        return type.equals("sold to ");
    }

    @Override
    protected void doTransaction(Transaction sell) {
        broker.registerObserver(this);
        broker.addSellOffer(sell);
    }

    @Override
    public void update(String type) {
    }

    @Override
    protected void unregister() {
        broker.unregisterObserver(this);
    }
}
