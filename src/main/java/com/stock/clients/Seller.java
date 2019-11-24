package com.stock.clients;

import com.stock.transactions.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Seller extends Client {
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

        while ( (buy = broker.getBuyOffer(sell.getPrice())) != null && (searching || sell.getAmount() != 0 ) )
            searching = isSearching(sell, buy);

        if (sell.getAmount() == 0)
            mytransactionList.remove(sell);
    }
}
