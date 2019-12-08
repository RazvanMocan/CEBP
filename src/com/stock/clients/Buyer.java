package com.stock.clients;

import com.stock.helper.Observer;
import com.stock.helper.ProtectedList;
import com.stock.transactions.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Buyer extends Client implements Observer {
    private ProtectedList<Transaction> mine;

    public Buyer(Socket socket, BufferedReader in) throws IOException {
        super(socket, in);
        this.type = "buys";
        mine = new ProtectedList<>();
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
    protected void doTransaction(Transaction buy) {
        broker.registerObserver(this);
        mine.add(buy);
        broker.addBuyRequest(buy);
    }

    @Override
    public void update(String type) {
        if (type.equals("sold to ")) {
            System.out.println("sold finished");
            sendMsg("notifTransaction finished");
        }
        else {
            System.out.println("intrat");
            List<Transaction> min = mine.getList();
            for (Transaction buy : min) {
                Transaction sell = broker.getSellOffer(buy.getPrice());
                if (sell != null) {
                    if (!this.isSearching(sell,buy)) {
                        if (buy.getAmount() == 0)
                            mine.remove(buy);

                        System.out.println("Transaction done");
                        break;
                    }
                    // TODO Add some communication with the client
                }
            }
        }
    }

    @Override
    protected void unregister() {
        broker.unregisterObserver(this);
    }
}
