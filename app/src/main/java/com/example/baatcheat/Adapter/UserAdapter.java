package com.example.baatcheat.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baatcheat.MessageActivity;
import com.example.baatcheat.Model.User;
import com.example.baatcheat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private Dialog mDialog;

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        ViewHolder mviewHolder = new ViewHolder(view);

        return mviewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final User users = mUsers.get(position);


        holder.username.setText(users.getUsername());
        holder.phoneNumber.setText(users.getPhone());
//        Glide.with(mContext).load(users.getImageUrl()).into(holder.image_Profile);

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView dialog_username = mDialog.findViewById(R.id.dialog_username);
                TextView dialog_phone = mDialog.findViewById(R.id.dialog_phone);
                ImageView dialog_imageProfile = mDialog.findViewById(R.id.dialog_ImageProfile);
                ImageView normalchat = mDialog.findViewById(R.id.normalchat);
                ImageView audiochat = mDialog.findViewById(R.id.audiochat);
                ImageView videochat = mDialog.findViewById(R.id.videochat);

                dialog_username.setText(users.getUsername());
                dialog_phone.setText(users.getBio());
                mDialog.show();

                normalchat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, MessageActivity.class);
                        i.putExtra("userid",users.getId());
                        mContext.startActivity(i);
                        mDialog.cancel();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, phoneNumber;
        public CircleImageView image_Profile;
        public RelativeLayout mLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            image_Profile = itemView.findViewById(R.id.image_profile);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            mLayout = itemView.findViewById(R.id.mLayout);

            //dialog ini
            EmojiCompat.Config config = new BundledEmojiCompatConfig(mContext);
            EmojiCompat.init(config);
            mDialog = new Dialog(mContext);
            mDialog.setContentView(R.layout.dialog_contact);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
