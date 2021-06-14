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
import com.malykhinv.footstepsgeo.databinding.GroupListItemBinding;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.presenter.fragments.UserlistScreenPresenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class UserlistAdapter extends RecyclerView.Adapter<UserlistAdapter.UserlistViewHolder> {

    private static final int FRIEND_OPTIONS_COUNT = 2;
    private final Context context = App.getAppComponent().getContext();
    private final UserlistScreenPresenter presenter;
    public ArrayList<User> friendUsers = new ArrayList<>();

    public UserlistAdapter(UserlistScreenPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    @NonNull
    public UserlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserlistViewHolder(GroupListItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserlistAdapter.UserlistViewHolder holder, int index) {
        holder.bind(friendUsers.get(index));
        holder.b.imageButtonFriendOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.setOnMenuItemClickListener(item -> {
                    if (presenter != null) {
                        presenter.onFriendOptionWasClicked(item, holder.getAdapterPosition());
                    }
                    return false;
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_friend_options, popup.getMenu());
                popup.show();
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(UserlistViewHolder holder) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(UserlistViewHolder holder) {
        holder.itemView.clearAnimation();
        super.onViewDetachedFromWindow(holder);
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

            b.imageButtonFriendOptions.setOnClickListener(view -> {
//                showFriendOptionsPopup(view);
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
