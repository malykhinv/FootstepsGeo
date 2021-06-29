package com.malykhinv.footstepsgeo.mvp.view.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.databinding.GroupFriendInfoItemVerticalBinding;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.presenter.fragments.FriendsScreenPresenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class FriendsScrollVerticalAdapter extends RecyclerView.Adapter<FriendsScrollVerticalAdapter.ViewHolder> {

    private final Context context = App.getAppComponent().getContext();
    private final FriendsScreenPresenter presenter;
    public HashMap<String, User> mapOfFriends = new HashMap<>();
    private int lastIndex = -1;

    public FriendsScrollVerticalAdapter(FriendsScreenPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(GroupFriendInfoItemVerticalBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int index) {
        ArrayList<User> listOfFriends = new ArrayList<>(mapOfFriends.values());
        holder.updateUI(listOfFriends.get(index));
        setAnimation(holder.itemView, index);

        holder.b.imageButtonFriendOptions.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, view);
            popup.setOnMenuItemClickListener(item -> {
                if (presenter != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    String userId = listOfFriends.get(index).id;
                    presenter.onFriendOptionWasClicked(item, adapterPosition, userId);
                }
                return false;
            });
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_friend_options, popup.getMenu());
            popup.show();
        });
    }

    private void setAnimation(View view, int index) {
         if (index > lastIndex) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            view.startAnimation(animation);
            lastIndex = index;
         }
    }

    @Override
    public int getItemCount() {
        return mapOfFriends.size();
    }

    public void setItems(HashMap<String, User> mapOfFriends) {
        this.mapOfFriends.putAll(mapOfFriends);
        notifyDataSetChanged();
    }

    public void clearItems() {
        this.mapOfFriends.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static final int CORE_ADDRESS_LINE_INDEX = 0;
        private final GroupFriendInfoItemVerticalBinding b;

        public ViewHolder(@NonNull GroupFriendInfoItemVerticalBinding b) {
            super(b.getRoot());
            this.b = b;
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
                    b.textAddress.setText(address.getAddressLine(CORE_ADDRESS_LINE_INDEX));
                } else {
                    b.textAddress.setText(itemView.getContext().getString(R.string.na));
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
