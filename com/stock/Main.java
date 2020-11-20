package com.stock;

import com.stock.clients.Buyer;
import com.stock.clients.Client;
import com.stock.clients.Seller;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {
        try(ServerSocket serverSocket = new ServerSocket(400)) {
            //noinspection InfiniteLoopStatement
            while(true) {
                Socket client = serverSocket.accept();

                Client stockClient = getClient(client);
                System.out.println(stockClient);
                new Thread(stockClient).start();
            }
        } catch(IOException e) {
            System.out.println("Server exception " + e.getMessage());
        }
    }

    private static Client getClient(Socket client) throws Exception {
        BufferedReader input = new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        String clientType = input.readLine();
        //System.out.println(client);
        if (clientType.equals("Seller"))
            return new Seller(client, input);
        else if (clientType.equals("Buyer"))
            return new Buyer(client, input);

        return null;
    }
}
