package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_search);

        final EditText editText = findViewById(R.id.search_box);
        Button search_btn = findViewById(R.id.search_btn);
        final ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard();
                String query = editText.getText().toString();
                loadingIndicator.setVisibility(View.VISIBLE);

                // check network availability status
                TextView noInternetConnection = findViewById(R.id.no_internet_connection);
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                boolean isConnected = netInfo != null && netInfo.isConnected();

                if (isConnected) {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);

                    loadingIndicator.setVisibility(View.GONE);
                    noInternetConnection.setVisibility(View.GONE);
                } else {
                    loadingIndicator.setVisibility(View.GONE);
                    noInternetConnection.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
