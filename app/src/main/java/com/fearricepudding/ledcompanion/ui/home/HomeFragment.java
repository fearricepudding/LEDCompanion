package com.fearricepudding.ledcompanion.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.fearricepudding.ledcompanion.MainActivity;
import com.fearricepudding.ledcompanion.R;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    TextView statusLabel;
    TextView brightnessLabel;
    SeekBar brightnessSeek;


    @RequiresApi(api = Build.VERSION_CODES.R)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final MainActivity main = (MainActivity) getParentFragment().getActivity();
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        statusLabel = root.findViewById(R.id.statusLabel);
        brightnessLabel = root.findViewById(R.id.brightnessLabel);
        brightnessSeek = root.findViewById(R.id.brightnessSeek);

        brightnessSeek.setMax(100);
        brightnessSeek.setMin(0);

        // Get initial values for statistics
        final String response = main.sendHttpRequest("status", "GET");
        if(!updateStats(response)){
            Log.e("White button", "Failed to update statistics");
        };


        // Change brightness using seekbar
        brightnessSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                double newBrightness = (float) progress / 100;
                String response = main.sendHttpRequest("brightness", "POST", String.format("%.2f", newBrightness));
                if(!updateStats(response)){
                    Log.e("White button", "Failed to update statistics");
                };
            };
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {};
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {};
        });

        // Toggle lights button
        final Button toggleBtn = root.findViewById(R.id.toggle_btn);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            public void onClick(View v) {
                String response = main.sendHttpRequest("toggle", "POST");
                if(!updateStats(response)){
                    Log.e("White button", "Failed to update statistics");
                };
            }
        });

        // Change lights to white button
        final Button whiteBtn = root.findViewById(R.id.whiteResetBtn);
        whiteBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            public void onClick(View v) {
                String response = main.sendHttpRequest("white", "POST");
                if(!updateStats(response)){
                    Log.e("White button", "Failed to update statistics");
                };
            };
        });

        // Change lights to rainbow button
        final Button rainbowBtn = root.findViewById(R.id.rainbowBtn);
        rainbowBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            public void onClick(View v) {
                String response = main.sendHttpRequest("rainbow", "POST");
                if(!updateStats(response)){
                    Log.e("White button", "Failed to update statistics");
                };
            };
        });
        return root;
    };

    /**
     * Update the LED status values on the main view
     *
     * @param response - response from HTTP request
     * @return boolean - success or nah
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public boolean updateStats(String response) {
        try {
            JSONObject status = new JSONObject(response);
            statusLabel.setText(status.getString("state"));
            brightnessLabel.setText(String.format("en.US", "%.0f percent", (status.getDouble("brightness") * 100)));
            int updateSlider = (int) ((float) status.getDouble("brightness") * 100);
            Log.e("set Slider", String.valueOf(updateSlider));
            brightnessSeek.setProgress(updateSlider, true);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Json Error", "Failed to parse new json data after toggle");
        } catch (NullPointerException e) {
            Log.e("Json Error", "NULL Pointer after toggle");
        };
        return false;
    };
};