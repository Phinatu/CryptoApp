package com.example.android.cryptoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cryptoapp.RateTracker.MoneyRate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by Phinatu on 25/10/2017.
 */

public class MainActivity extends AppCompatActivity {

    LinearLayout mainView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ListView listView;
    public RateTracker rateTracker;
    RequestQueue requestQueue;
    String requestUrl;

    SharedPreferences settings;
    int orderMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mainView = (LinearLayout) findViewById(R.id.main_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        listView = (ListView) findViewById(R.id.rates_list_view);
        //Remove dividers from the list view
        listView.setDivider(null);

        rateTracker = new RateTracker();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        settings = getSharedPreferences("mSettings", MODE_PRIVATE);
        orderMode = settings.getInt("orderMode", CryptoStatics.ORDER_ALPHABETICAL);

  //Display the various currencies
        requestUrl = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=" +
                "USD,EUR,NGN,RUB,CAD,JPY,GBP,AUD,INR,HKD,IDR,SGD,CHF,CNY,ZAR,THB,SAR,KRW,GHS,BRL";
//Download the rates from the API provided
        downloadRates();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadRates();
            }
        });

    }


    private static class CryptoViewHolder {
        private TextView currencyTextView;
        private TextView btcTextView;
        private TextView ethTextView;
    }


    private class CryptoAdapter extends BaseAdapter {

        ArrayList<MoneyRate> rates;

        private CryptoAdapter(ArrayList<MoneyRate> ratesInstance) {
            rates = ratesInstance;
        }

        public int getCount() {
            return rates.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("DefaultLocale")
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            CryptoViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.crypto_list, parent, false);
                holder = new CryptoViewHolder();
                holder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_name);
                holder.btcTextView = (TextView) convertView.findViewById(R.id.btc);
                holder.ethTextView = (TextView) convertView.findViewById(R.id.eth);
                convertView.setTag(holder);
            }
            else {
                holder = (CryptoViewHolder) convertView.getTag();
            }

            final MoneyRate rateRow = rates.get(position);

            final String crossCurrency = rateRow.getCurrency();
            final double crossBtc = rateRow.getBtcRate();
            final double crossEth = rateRow.getEthRate();

            holder.currencyTextView.setText(crossCurrency);
            holder.btcTextView.setText(String.format("%1$,.2f", crossBtc));
            holder.ethTextView.setText(String.format("%1$,.2f", crossEth));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, CryptoConversionActivity.class);
                    intent.putExtra(CryptoStatics.EXTRA_CURRENCY, crossCurrency);
                    intent.putExtra(CryptoStatics.EXTRA_BTC_RATE, crossBtc);
                    intent.putExtra(CryptoStatics.EXTRA_ETH_RATE, crossEth);
                    startActivity(intent);
                    overridePendingTransition(R.animator.slide_in_right, R.animator.no_animation);
                }
            });

            return convertView;

        }
    }


    //This Method GET's the rates of the 20 currencies in the URL, Requests JSON response, parses response and displays the rates
    public void downloadRates() {
        rateTracker = new RateTracker();

        JsonObjectRequest requestNameAvatar = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject btc_rates = response.getJSONObject("BTC".trim());
                            JSONObject eth_rates = response.getJSONObject("ETH".trim());

                            Iterator<?> keysBTC = btc_rates.keys();
                            Iterator<?> keysETH = eth_rates.keys();

                            while(keysBTC.hasNext() && keysETH.hasNext()) {
                                String keyBTC = (String) keysBTC.next();
                                String keyETH = (String) keysETH.next();

                                rateTracker.add(keyBTC, btc_rates.getDouble(keyBTC), eth_rates.getDouble(keyETH));
                            }

                            mSwipeRefreshLayout.setRefreshing(false); //to remove the progress bar for refresh
                            rateTracker.orderList(orderMode);
                            listView.setAdapter(new CryptoAdapter(rateTracker.ratesArrayList));


                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mainView, CryptoStatics.REFRESH_ERROR, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }



        });

        requestQueue.add(requestNameAvatar);
    }

    @Override
    protected void onStop() {
        super.onStop();
        requestQueue.cancelAll(this);
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu and add items to the action bar if any.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //No inspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            //Show the progress bar for refresh
            mSwipeRefreshLayout.setRefreshing(true);
            downloadRates();
            return true;
        } else if(id == R.id.action_order) {
            if(orderMode == CryptoStatics.ORDER_ALPHABETICAL) orderMode = CryptoStatics.ORDER_BY_RATE;
            else orderMode = CryptoStatics.ORDER_ALPHABETICAL;
            rateTracker.orderList(orderMode);
            listView.setAdapter(new CryptoAdapter(rateTracker.ratesArrayList));
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("orderMode", orderMode);
            editor.apply();
        }

        return super.onOptionsItemSelected(item);
    }

}