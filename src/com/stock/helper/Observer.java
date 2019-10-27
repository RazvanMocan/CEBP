package com.stock.helper;

import java.util.List;

public interface Observer {
    void update();

    List<String> getinterestedTypes();
}
