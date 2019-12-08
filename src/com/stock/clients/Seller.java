package com.stock.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.stock.helper.Observer;
import com.stock.transactions.Transaction;

public class Seller extends Client implements Observer {

	public Seller(Socket socket, BufferedReader in) throws IOException {
		super(socket, in);
		this.type = "is selling";
	}

	@Override
	public boolean verifyReq(String type, float price) {
		return type.equals("sold to ");
	}

	@Override
	protected boolean removeTransaction(Transaction myTransaction) {
		return broker.removeSellOffer(myTransaction);
	}

	@Override
	protected void removeTransactions(List<Transaction> myTransactions) {
		broker.removeAllSellOffers(myTransactions);
	}

	@Override
	protected void doTransaction(Transaction sell) {

		broker.registerObserver(this);
		broker.addSellOffer(sell);
		mytransactionList.add(sell);
		
		boolean searching = true;
		Transaction buy;

		while ((buy = broker.getBuyOffer(sell.getPrice())) != null && searching)
			searching = isSearching(sell, buy);

		if (!searching)
			mytransactionList.remove(sell);
	}

	@Override
	public void update(String type) {
	}

	@Override
	protected void unregister() {
		broker.unregisterObserver(this);
	}

}
