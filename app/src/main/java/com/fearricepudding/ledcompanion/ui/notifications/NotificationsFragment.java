package com.fearricepudding.ledcompanion.ui.notifications;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.fearricepudding.ledcompanion.MainActivity;
import com.fearricepudding.ledcompanion.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    @RequiresApi(api = Build.VERSION_CODES.R)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final MainActivity main = (com.fearricepudding.ledcompanion.MainActivity) getParentFragment().getActivity();

        final String response = main.sendHttpRequest("status", "GET");
        try {
            JSONObject status = new JSONObject(response);
            JSONArray colors = status.getJSONArray("pixels");
            int[] colArray = new int[191];
            Log.e("Colors:", String.valueOf(colors.length()));
            for(int i=0;i < colors.length(); i++){
                JSONObject col = colors.getJSONObject(i);
                int rgb = 254;
                rgb = (rgb << 8) + 254;
                rgb = (rgb << 8) + 254;
                colArray[i] = 0xFFFFFFFE;
            }
            int white = 255;
            white = (white << 8) + 255;
            white = (white << 8) + 255;
            Log.e("Colors: ", String.valueOf(colArray[190]));
            Log.e("White:", String.valueOf(white));
            TextView gradient = root.findViewById(R.id.grad);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    colArray
                    );
            gradient.setBackgroundDrawable(gd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }
}