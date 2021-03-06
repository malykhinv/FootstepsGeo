package com.malykhinv.footstepsgeo.mvp.view.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.databinding.GroupFriendInfoItemHorizontalBinding;
import com.malykhinv.footstepsgeo.di.App;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsScrollHorizontalAdapter extends RecyclerView.Adapter<FriendsScrollHorizontalAdapter.ViewHolder> {

    private final Context context = App.getAppComponent().getContext();
    public HashMap<String, User> mapOfFriends = new HashMap<>();
    private int lastIndex = -1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(GroupFriendInfoItemHorizontalBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsScrollHorizontalAdapter.ViewHolder holder, int index) {
        ArrayList<User> listOfFriends = new ArrayList<>(mapOfFriends.values());
        holder.updateUI(listOfFriends.get(index));
        setAnimation(holder.itemView, index);
    }

    private void setAnimation(View view, int index) {
        if (index > lastIndex) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.animation_bounce);
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

        private final GroupFriendInfoItemHorizontalBinding binding;

        public ViewHolder(@NonNull GroupFriendInfoItemHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void updateUI(User friendUser) {
            updateFriendUserName(friendUser.name);
            updateFriendUserImage(friendUser.imageUrl);
        }

        private void updateFriendUserName(String name) {
            if (name != null) {
                binding.textFriendName.setText(name);
            }
        }

        @SuppressLint("CheckResult")
        private void updateFriendUserImage(String imageUrl) {
            if (imageUrl != null) {
                RequestOptions options = new RequestOptions();
                options.circleCrop();
                Glide.with(itemView)
                        .load(imageUrl)
                        .apply(options)
                        .into(binding.imageFriend);
            }
        }
    }
}
