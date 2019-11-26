package com.example.baatcheat.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baatcheat.Model.Chat;
import com.example.baatcheat.Model.ImageMedia;
import com.example.baatcheat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class ImageMediaAdapter extends RecyclerView.Adapter<ImageMediaAdapter.ViewHolder> {

    private Context mContext;
    private List<ImageMedia> imageMediaList;

    private FirebaseUser firebaseUser;

    public static final int MESSAGE_TYPE_LEFT = 0;
    public static final int MESSAGE_TYPE_RIGHT = 1;

    public ImageMediaAdapter(Context mContext, List<ImageMedia> imageMediaList) {
        this.mContext = mContext;
        this.imageMediaList = imageMediaList;
    }

    @NonNull
    @Override
    public ImageMediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_imagemedia_right, parent, false);
            return new ImageMediaAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_imagemedia_left, parent, false);
            return new ImageMediaAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ImageMediaAdapter.ViewHolder holder, int position) {
        ImageMedia imageMedia = imageMediaList.get(position);
        Glide.with(mContext).load(imageMedia.getImageUrl()).into(holder.imageMedia);
    }

    @Override
    public int getItemCount() {
        return imageMediaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageMedia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageMedia = itemView.findViewById(R.id.imageMedia);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (imageMediaList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MESSAGE_TYPE_RIGHT;
        } else {
            return MESSAGE_TYPE_LEFT;
        }
    }
}

