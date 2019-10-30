package com.example.baatcheat.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baatcheat.Model.User;
import com.example.baatcheat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private Dialog mDialog;

    FirebaseUser firebaseUser;

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

        //dialog ini
        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_contact);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return mviewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User users = mUsers.get(position);


        holder.username.setText(users.getUsername());
        holder.phoneNumber.setText(users.getPhone());
//        Glide.with(mContext).load(users.getImageurl()).into(holder.image_Profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView dialog_username = mDialog.findViewById(R.id.dialog_username);
                TextView dialog_phone = mDialog.findViewById(R.id.dialog_phone);
                ImageView dialog_imageProfile = mDialog.findViewById(R.id.dialog_ImageProfile);

                dialog_username.setText(users.getUsername());
                dialog_phone.setText(users.getPhone());
                mDialog.show();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            image_Profile = itemView.findViewById(R.id.image_profile);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
        }
    }
}
