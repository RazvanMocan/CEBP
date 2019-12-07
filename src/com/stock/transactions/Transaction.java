package com.stock.transactions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    private String clientName;
    private Socket clientSocket;
    private PrintWriter clientWriter;
    private String secondClient = null;
    private Socket secondClientSocket = null;
    private PrintWriter secondClientWriter = null;
    private int amount;
    private float price;
    private String transType;
    private Date date;

    private SimpleDateFormat format;

    public Transaction(String clientName, int amount, float price, String type, Socket socket, PrintWriter writer) {
        this.clientName = clientName;
        this.clientSocket = socket;
        this.clientWriter = writer;
        this.amount = amount;
        this.price = price;
        transType = type;
        date = new Date(System.currentTimeMillis());
        format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    Transaction(Transaction sell, Transaction buy) {
        this.clientName = sell.clientName;
        this.secondClient = buy.clientName;
        this.clientSocket = sell.clientSocket;
        this.secondClientSocket = buy.clientSocket;
        this.clientWriter = sell.clientWriter;
        this.secondClientWriter = buy.clientWriter;
        this.price = sell.price;
        this.amount = Math.min(sell.amount, buy.amount);
        transType = "sold to ";
        date = new Date(System.currentTimeMillis());
        format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    public String getTransType() {
        return transType;
    }

    public int getAmount() {

        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        StringBuilder delimiter = new StringBuilder(" ");
        if (secondClient != null)
            delimiter.append(secondClient).append(" ");

        return clientName + " " + transType + delimiter.toString() + amount + " stocks for " + price +
                " per stock. Started date: " + format.format(date);
    }
}
