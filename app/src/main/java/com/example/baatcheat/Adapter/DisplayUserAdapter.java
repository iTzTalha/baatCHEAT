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
import androidx.emoji.widget.EmojiTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baatcheat.MessageActivity;
import com.example.baatcheat.Model.Chat;
import com.example.baatcheat.Model.User;
import com.example.baatcheat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayUserAdapter extends RecyclerView.Adapter<DisplayUserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private Dialog mDialog;
    private boolean isChat;

    private String thelastMessage;

    public DisplayUserAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);

        return new DisplayUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User users = mUsers.get(position);

        holder.username.setText(users.getUsername());
//        holder.phoneNumber.setText(users.getPhone());
        if (users.getImageUrl().equals("default")) {
            holder.image_Profile.setBackgroundResource(R.drawable.profile_holder_);
        } else {
            Glide.with(mContext).load(users.getImageUrl()).into(holder.image_Profile);
        }
        if (isChat) {
            lastMessage(users.getId(),holder.lastMsg);
            if (users.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
            } else {
                holder.img_on.setVisibility(View.GONE);
            }
        } else {
            holder.lastMsg.setVisibility(View.GONE);
            holder.img_on.setVisibility(View.GONE);
        }
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MessageActivity.class);
                i.putExtra("userid", users.getId());

                mContext.startActivity(i);
            }
        });
        holder.image_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        i.putExtra("userid", users.getId());
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

        public TextView username;
        public TextView lastMsg;
        public CircleImageView image_Profile;
        public ImageView img_on;
        public RelativeLayout mLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            image_Profile = itemView.findViewById(R.id.image_profile);
            lastMsg = itemView.findViewById(R.id.phoneNumber);
            img_on = itemView.findViewById(R.id.img_on);
            mLayout = itemView.findViewById(R.id.mLayout);

            //dialog ini
            EmojiCompat.Config config = new BundledEmojiCompatConfig(mContext);
            EmojiCompat.init(config);
            mDialog = new Dialog(mContext);
            mDialog.setContentView(R.layout.dialog_contact);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void updateList(List<User> list) {
        mUsers = list;
        notifyDataSetChanged();
    }

    private void lastMessage(final String userid, final TextView last_msg) {
        thelastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                        thelastMessage = chat.getMessage();
                    }
                }

                switch (thelastMessage) {
                    case "default":
                        last_msg.setText("");
                        break;
                    default:
                        last_msg.setText(thelastMessage);
                        break;
                }

                thelastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
