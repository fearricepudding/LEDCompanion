package com.fearricepudding.ledcompanion;

import android.app.DownloadManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    // TODO This needs to be stored during app setup
    final String apiAddress = "192.168.1.222";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    /**
     * Send a HTTP request with NO data to the API
     *
     * @param path - Request path
     * @param method - Request method (POST / GET)
     * @return String - response from API
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public String sendHttpRequest(String path, String method) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = null;
        try {
            url = new URL("http://"+apiAddress+":8080/"+path); // IP is temp for testing cri - should prob just have a virt device
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
            if (200 <= http.getResponseCode() && http.getResponseCode() <= 399) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(((HttpURLConnection) con).getErrorStream()));
            }
            return br.readLine();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}"; // Just return nothing if failed
    }

    /**
     * Send HTTP request WITH data to the API
     *
     * @param path - Request path
     * @param method - Request method (POST / GET)
     * @param data - Request data
     * @return - HTTP response from API
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public String sendHttpRequest(String path, String method, String data) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = null;
        try {
            url = new URL("http://"+apiAddress+":8080/"+path); // IP is temp for testing cri - should prob just have a virt device
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod(method);
            http.setDoOutput(true);
            String dataOut = "data="+data;
            Log.e("HTTP POST", dataOut);
            byte[] out = dataOut.getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput( true );
            http.setInstanceFollowRedirects( false );
            http.setRequestProperty( "charset", "utf-8");
            http.setRequestProperty( "Content-Length", Integer.toString( length ));
            http.setUseCaches( false );
            try( DataOutputStream wr = new DataOutputStream( http.getOutputStream())) {
                wr.write( out );
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
            if (200 <= http.getResponseCode() && http.getResponseCode() <= 399) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(((HttpURLConnection) con).getErrorStream()));
            };
            return br.readLine();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}"; // Just return nothing if failed
    };
};