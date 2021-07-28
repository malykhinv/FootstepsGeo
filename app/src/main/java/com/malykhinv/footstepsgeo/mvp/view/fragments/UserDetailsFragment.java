package com.malykhinv.footstepsgeo.mvp.view.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.databinding.FragmentUserDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;

public class UserDetailsFragment extends Fragment {

    private static final long MILLIS_IN_MINUTE = 60000;
    private static final long TIME_STATUS_NOW = 60000;                  // 0s - 60s
    private static final long TIME_STATUS_ONE_MINUTE = 120000;          // 1m - 2m
    private static final long TIME_STATUS_FEW_MINUTES = 300000;         // 2m - 5m
    private static final int INDEX_LATITUDE = 0;
    private static final int INDEX_LONGITUDE = 1;
    private static final int FACTOR_M_TO_KM = 1000;
    private View view;
    private FragmentUserDetailsBinding binding;
    public User currentUser;
    public User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentUserDetailsBinding.inflate(inflater, container, false);
        }

        if (view == null) {
            view = binding.getRoot();
        } else if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@androidx.annotation.NonNull @NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUI(user);
    }

    public void updateUI(User user) {
        showUserImage(user.imageUrl);
        showUserName(user.name);
        showUserDistance(currentUser.position, user.position);
        showUserBatteryLevel(user.batteryLevel);
        showUserLastLocationTime(user.lastLocationTime);
    }

    @SuppressLint("CheckResult")
    private void showUserImage(String url) {
        RequestOptions options = new RequestOptions();
        options.circleCrop();

        Glide.with(this)
                .asBitmap()
                .apply(options)
                .load(url)
                .into(binding.imageUserpic);
    }

    private void showUserName(String name) {
        binding.textUsername.setText(name);
    }

    private void showUserDistance(ArrayList<Double> currentUserPosition, ArrayList<Double> friendUserPosition) {
        binding.textUserInfoDistance.setText(getDistanceLabel(currentUserPosition, friendUserPosition));
    }

    private void showUserBatteryLevel(int batteryLevel) {
        binding.textUserInfoBattery.setText(String.format("%s%s", batteryLevel, getResources().getString(R.string.percent)));
    }

    private void showUserLastLocationTime(long lastLocationTime) {
        binding.textUserInfoTime.setText(getTimeLabel(lastLocationTime));
    }

    private String getTimeLabel(long lastLocationTime) {
        long timeInterval = System.currentTimeMillis() - lastLocationTime;
        if (timeInterval < TIME_STATUS_NOW) {
            return getResources().getString(R.string.time_now);
        } else if (timeInterval < TIME_STATUS_ONE_MINUTE) {
            return getResources().getString(R.string.time_ome_minute_ago);
        } else if (timeInterval < TIME_STATUS_FEW_MINUTES) {
            return getPastMinutesLabel(timeInterval);
        } else {
            return getDateLabel(lastLocationTime);
        }
    }

    private String getPastMinutesLabel(long timeInterval) {
        long minutes = timeInterval / MILLIS_IN_MINUTE;
        return getResources().getString(R.string.time_few_minutes_ago, String.valueOf(minutes));
    }

    private String getDateLabel(long lastLocationTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yy");
        return dateFormat.format(lastLocationTime);
    }

    private String getDistanceLabel(ArrayList<Double> currentUserPosition, ArrayList<Double> friendUserPosition) {
        float distance = getDistance(getLocation(currentUserPosition), getLocation(friendUserPosition));
        if (distance > FACTOR_M_TO_KM) {
            return (int) (distance / FACTOR_M_TO_KM) + getResources().getString(R.string.km);
        } else {
            return (int) distance + getResources().getString(R.string.m);
        }
    }

    private Location getLocation(ArrayList<Double> position) {
        Location location = new Location("location");
        location.setLatitude(position.get(INDEX_LATITUDE));
        location.setLongitude(position.get(INDEX_LONGITUDE));
        return location;
    }

    private float getDistance(Location locationA, Location locationB) {
        return locationA.distanceTo(locationB);
    }
}