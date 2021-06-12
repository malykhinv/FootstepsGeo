package com.malykhinv.footstepsgeo.mvp.view.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.databinding.GroupListItemBinding;
import com.malykhinv.footstepsgeo.di.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class UserlistAdapter extends RecyclerView.Adapter<UserlistAdapter.UserlistViewHolder> {

    private static final int FRIEND_OPTIONS_COUNT = 2;
    private final Context context = App.getAppComponent().getContext();
    public ArrayList<User> friendUsers = new ArrayList<>();

    @Override
    @NonNull
    public UserlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("LLL", "onCreateViewHolder: ");
        return new UserlistViewHolder(GroupListItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserlistAdapter.UserlistViewHolder holder, int index) {
        Log.d("F0", "onBindViewHolder: " + friendUsers.get(0).name);
        holder.bind(friendUsers.get(index));
    }

    @Override
    public int getItemCount() {
        return friendUsers.size();
    }

    public void setItems(ArrayList<User> friendUsers) {
        this.friendUsers.addAll(friendUsers);
        notifyDataSetChanged();
    }

    public void clearItems() {
//        this.friendUsers.clear();
        notifyDataSetChanged();
    }

    static class UserlistViewHolder extends RecyclerView.ViewHolder {

        private GroupListItemBinding b;

        public UserlistViewHolder(@NonNull GroupListItemBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        public void bind(User friendUser) {
            updateUI(friendUser);

            b.imageButtonFriendOptions.setOnClickListener(v -> {
                // TODO
            });
        }

        private void updateUI(User friendUser) {
            updateFriendUserName(friendUser.name);
            updateFriendUserAddress(friendUser.position);
            updateFriendUserImage(friendUser.imageUrl);
        }

        private void updateFriendUserName(String name) {
            if (name != null) {
                b.textName.setText(name);
            }
        }

        private void updateFriendUserAddress(ArrayList<Double> position) {
            if (position != null) {
                Address address = getAddress(position);
                if (address != null) {
                    b.textUserAddress.setText(address.toString());
                } else {
                    b.textUserAddress.setText(itemView.getContext().getString(R.string.na));
                }
            }
        }

        private Address getAddress(ArrayList<Double> position) {
            if (position != null) {
                Geocoder geocoder = new Geocoder(itemView.getContext(), Locale.getDefault());
                Double latitude = position.get(0);
                Double longitude = position.get(1);
                Address address = null;
                try {
                    address = geocoder.getFromLocation(latitude, longitude, 1).get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return address;
            } else return null;
        }

        @SuppressLint("CheckResult")
        private void updateFriendUserImage(String imageUrl) {
            if (imageUrl != null) {
                RequestOptions options = new RequestOptions();
                options.circleCrop();
                Glide.with(itemView)
                        .load(imageUrl)
                        .apply(options)
                        .into(b.imageUser);
            }
        }

    }
}
