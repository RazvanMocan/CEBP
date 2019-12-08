package com.stock.helper;


public interface Observer {
    void update(String type);

    boolean verifyReq(String type, float price);
}
