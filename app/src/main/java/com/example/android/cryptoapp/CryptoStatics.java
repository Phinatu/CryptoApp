package com.example.android.cryptoapp;


/**
 * Created by Phinatu on 25/10/2017.
 */

public interface CryptoStatics {
    String EXTRA_CURRENCY = "extra currency";
    String EXTRA_BTC_RATE = "extra btc";
    String EXTRA_ETH_RATE = "extra eth";

    int ORDER_ALPHABETICAL = 1;
    int ORDER_BY_RATE = 2;

    String INVALID_CONVERSION = "Sorry, you provided an Invalid conversion value";
    String REFRESH_ERROR = "Rates could not refresh";
}
