package com.example.guest.congress.ui;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.congress.R;
import com.example.guest.congress.adapter.RepAdapter;
import com.example.guest.congress.models.Representative;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends ListActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private String mZipcode;
    private ArrayList<Representative> mRepresentatives;


    @Bind(R.id.zipCodeInput) EditText mZipCodeInput;
    @Bind(R.id.submitButton) Button mSubmitButton;
    @Bind (R.id.newSearchButton) Button mNewSearchButton;
    private ListView mRepList;
    private RepAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRepList = (ListView) findViewById(android.R.id.list);

        mRepresentatives = new ArrayList<Representative>();

        mAdapter = new RepAdapter(this, mRepresentatives);
        setListAdapter(mAdapter);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZipcode = mZipCodeInput.getText().toString();
                getRepresentatives(mZipcode);
                toggleViews();
                mZipCodeInput.setText("");
            }
        });

        mNewSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleViews();
            }
        });
    }


    private void toggleViews() {
        if (mZipCodeInput.getVisibility() == View.VISIBLE) {
            mZipCodeInput.setVisibility(View.INVISIBLE);
            mSubmitButton.setVisibility(View.INVISIBLE);
            mRepList.setVisibility(View.VISIBLE);
            mNewSearchButton.setVisibility(View.VISIBLE);

        } else {
            mZipCodeInput.setVisibility(View.VISIBLE);
            mSubmitButton.setVisibility(View.VISIBLE);
            mRepList.setVisibility(View.INVISIBLE);
            mNewSearchButton.setVisibility(View.INVISIBLE);
        }
    }


    private void getRepresentatives(String zipcode) {
        String apiKey = "5efb63837f3b4d9cbbd5dafebc7571cc";
        String sunlightUrl = "https://congress.api.sunlightfoundation.com/legislators/locate?zip=" + zipcode + "&apikey=" + apiKey;


        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(sunlightUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            getRepDetails(jsonData);
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "Main UI code is running!");
    }


    private void getRepDetails(String jsonData) throws JSONException {
        mRepresentatives.clear();
        JSONObject data = new JSONObject(jsonData);
        JSONArray representatives = data.getJSONArray("results");
       for (int index = 0; index < representatives.length(); index++) {
           JSONObject repJSON = representatives.getJSONObject(index);
           String repName = repJSON.getString("first_name") + " " + repJSON.getString("last_name");
           String repParty = repJSON.getString("party");
           String repGender = repJSON.getString("gender");
           String repBirthday = repJSON.getString("birthday");
           String repPhone = repJSON.getString("phone");
           String repOffice = repJSON.getString("office");

           Representative representative = new Representative();

           representative.setName(repName);
           representative.setParty(repParty);
           representative.setGender(repGender);
           representative.setBirthday(repBirthday);
           representative.setPhone(repPhone);
           representative.setOffice(repOffice);

           mRepresentatives.add(representative);
           mAdapter.notifyDataSetChanged();
       }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}
