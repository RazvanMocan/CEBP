package com.stock.clients;

import com.stock.transactions.Transaction;
import com.stock.transactions.WallStreet;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class Client implements Runnable {
    private Socket socket;
    final static WallStreet broker = new WallStreet();
    private BufferedReader input;
    private PrintWriter writer;
    protected String type;
    protected List<Transaction> mytransactionList;

    Client(Socket socket, BufferedReader in) throws IOException {
        System.out.println("created");
        this.socket = socket;
        input = in;
        writer = new PrintWriter(socket.getOutputStream(), true);
        mytransactionList = new ArrayList<>();
    }
    
    protected void sendMsg(String msg) {
        writer.println(msg);
    }

    @Override
    public void run() {
        boolean end = false;
        String command;
        while (!end) {
            try {
                command = readInput();
                if (command == null) {
                    closeConnection();
                    break;
                }
                String name;
                switch (command) {
                    case "end":
                    	unregister();
                        closeConnection();
                        end = true;
                        break;
                    case "offer":
                        name = readInput();
                        int amount = Integer.parseInt(readInput());
                        float price = Float.parseFloat(readInput());

                        if (name.contains("index")) {
                            String[] split = name.split(" index ");
                            int index = Integer.parseInt(split[1]);
                            name = split[0];
                            if( removeTransaction(mytransactionList.get(index))) {
                                mytransactionList.remove(index);
                                doTransaction(new Transaction(name, amount, price, type));
                            }
                        } else
                            doTransaction(new Transaction(name, amount, price, type));
                        break;
                    case "Transactions":
                        sendList(broker.getTerminated());
                        break;
                    case "Sell offers":
                        sendList(broker.getSellOffers());
                        break;
                    case "Buy offers":
                        sendList(broker.getBuyRequests());
                        break;
                    case "All offers":
                        sendList(broker.getAllOffers());
                        break;
                    case  "My offers":
                    	mytransactionList.removeIf(transaction -> transaction.getAmount() == 0);
                        sendList(mytransactionList);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    boolean isSearching(Transaction sell, Transaction buy) {
        Transaction transaction = broker.doTransaction(sell, buy);
        if (broker.finishTransaction(transaction, sell, buy)) {
            sell.setAmount(sell.getAmount() - transaction.getAmount());
            buy.setAmount(buy.getAmount() - transaction.getAmount());

            if (sell.getAmount() > 0)
                broker.addSellOffer(sell);
            if (buy.getAmount() > 0)
                broker.addBuyRequest(buy);
            return false;
        }
        return true;
    }

    private void closeConnection() throws IOException {
        removeTransactions(mytransactionList);
        mytransactionList.clear();
        input.close();
        writer.close();
        socket.close();
    }

    private String readInput() throws IOException {
        return input.readLine();
    }

    private void sendList(List<Transaction> list) {
        writer.println(list.size());
        for (Object o : list) {
            writer.println(o.toString());
        }
    }
    
    protected abstract void unregister();

    protected abstract void doTransaction(Transaction t);

    protected abstract void removeTransactions(List<Transaction> myTransactions);

    protected abstract boolean removeTransaction(Transaction myTransaction);
}
