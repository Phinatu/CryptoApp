package com.example.android.cryptoapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Phinatu on 15/10/2017.
 */

public class CryptoConversionActivity extends AppCompatActivity {
    String currencyCode;
    String currencyFullName;
    EditText btcValueEdit, ethValueEdit, flatValueEdit;
    Button btcConvertButton, ethConvertButton, flatConvertButton, closeButton;

    double btcRate;
    double ethRate;

    TextView moneyCodeView;
    TextView fullNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crypto_currency_conversion);

        moneyCodeView = (TextView) findViewById(R.id.money_code_view);
        fullNameView = (TextView) findViewById(R.id.full_name_view);
        btcValueEdit = (EditText) findViewById(R.id.btc_value_edit);
        ethValueEdit = (EditText) findViewById(R.id.eth_value_edit);
        flatValueEdit = (EditText) findViewById(R.id.flat_value_edit);
        btcConvertButton = (Button) findViewById(R.id.btc_convert_button);
        ethConvertButton = (Button) findViewById(R.id.eth_convert_button);
        flatConvertButton = (Button) findViewById(R.id.flat_convert_button);
        closeButton = (Button) findViewById(R.id.button_close);

        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                overridePendingTransition(R.animator.slide_in_right, R.animator.no_animation);
                finish();
            }
        });

        Intent intent = getIntent();
        currencyCode = intent.getStringExtra(CryptoStatics.EXTRA_CURRENCY);
        btcRate = intent.getDoubleExtra(CryptoStatics.EXTRA_BTC_RATE, 0);
        ethRate = intent.getDoubleExtra(CryptoStatics.EXTRA_ETH_RATE, 0);
        currencyFullName = getFullName(currencyCode);
        String conversionMessage = currencyFullName + " Conversion";

        moneyCodeView.setText(currencyCode);
        fullNameView.setText(conversionMessage);
        if(getActionBar() != null) getActionBar().setTitle(currencyFullName);
        if(getSupportActionBar() != null) getSupportActionBar().setTitle(currencyFullName);

    }

    //Get full name of money from the currency code
    public String getFullName(String moneyCode) {
        switch (moneyCode) {
            case "USD": return "US Dollar";
            case "EUR": return "Euro";
            case "NGN": return "Naira";
            case "RUB": return "Russian Ruble";
            case "CAD": return "Canadian Dollar";
            case "JPY": return "Yen";
            case "GBP": return "Pound Sterling";
            case "AUD": return "Australian Dollar";
            case "INR": return "ndian Rupee";
            case "HKD": return "Hong Kong Dollar";
            case "IDR": return "Rupiah";
            case "SGD": return "Singapore Dollar";
            case "CHF": return "Swiss Franc";
            case "CNY": return "Renminbi (Yuan)";
            case "ZAR": return "Rand";
            case "THB": return "Thai Baht";
            case "SAR": return "Saudi Riyal";
            case "KRW": return "Won";
            case "GHS": return "Cedi";
            case "BRL": return "Brazilian Real";
            default: return "";
        }
    }

    @SuppressLint("DefaultLocale")
    //Method to do the conversion from one currency to BTC and ETH
    public void doConvert(View view) {
        if(view == btcConvertButton) {
            try {
                double btcAmount = Double.parseDouble(btcValueEdit.getText().toString());
                flatValueEdit.setText(String.format("%1$,.2f", (btcAmount * btcRate)));
                ethValueEdit.setText(String.format("%1$,.2f", (btcAmount * (ethRate / btcRate))));
            } catch (NumberFormatException e) {
                Snackbar.make(findViewById(R.id.main_scroll_view), CryptoStatics.INVALID_CONVERSION, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        } else if(view == ethConvertButton) {
            try {
                double ethAmount = Double.parseDouble(ethValueEdit.getText().toString());
                flatValueEdit.setText(String.format("%1$,.2f", (ethAmount * ethRate)));
                btcValueEdit.setText(String.format("%1$,.2f", (ethAmount * (btcRate / ethRate))));
            } catch (NumberFormatException e) {
                Snackbar.make(findViewById(R.id.main_scroll_view), CryptoStatics.INVALID_CONVERSION, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        } else if(view == flatConvertButton) {
            try {
                double flatAmount = Double.parseDouble(flatValueEdit.getText().toString());
                btcValueEdit.setText(String.format("%1$,.2f", (flatAmount / btcRate)));
                ethValueEdit.setText(String.format("%1$,.2f", (flatAmount / ethRate)));
            } catch (NumberFormatException e) {
                Snackbar.make(findViewById(R.id.main_scroll_view), CryptoStatics.INVALID_CONVERSION, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }
}
