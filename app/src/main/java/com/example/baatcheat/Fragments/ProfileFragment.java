package com.example.baatcheat.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.BundleCompat;
import androidx.core.content.ContextCompat;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.emoji.widget.EmojiTextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.bumptech.glide.Glide;
import com.example.baatcheat.BioActivity;
import com.example.baatcheat.LoginActivity;
import com.example.baatcheat.Model.User;
import com.example.baatcheat.R;
import com.example.baatcheat.ShowNumberActivity;
import com.example.baatcheat.StartActivity;
import com.example.baatcheat.UsernameActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.media.MediaRecorder.VideoSource.CAMERA;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView tv_username, myPhoneNumner;
    private ImageView image_profile,btn_change_profile;
    private EmojiTextView myBio;
    private LinearLayout Username;
    private LinearLayout phone;
    private LinearLayout logout;
    private LinearLayout bio;

    private TextView fixedText, Editusername;

    private FirebaseAuth mAuth;

    TashieLoader tashieLoader;

    FirebaseUser firebaseUser;

    private static  final int IMAGE_REGUEST = 1;
    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;

    private Dialog mDialog;
    private RelativeLayout dialog_bg;
    private LinearLayout select_photo,remove_photo;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EmojiCompat.Config config = new BundledEmojiCompatConfig(getActivity());
        EmojiCompat.init(config);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_username = view.findViewById(R.id.tv_username);
        myPhoneNumner = view.findViewById(R.id.myphonenumner);
        image_profile = view.findViewById(R.id.image_profile);
        btn_change_profile = view.findViewById(R.id.btn_change_profile);
        myBio = (EmojiTextView) view.findViewById(R.id.myBio);
        Username = view.findViewById(R.id.username);
        phone = view.findViewById(R.id.phone);
        bio = view.findViewById(R.id.bio);
        logout = view.findViewById(R.id.logout);

        fixedText = view.findViewById(R.id.fixedText);
        fixedText.setText("@");
        Editusername = view.findViewById(R.id.Editusername);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.dialog_change_profile);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        mDialog.getWindow().setAttributes(lp);

        dialog_bg = mDialog.findViewById(R.id.dialog_bg);
        select_photo = mDialog.findViewById(R.id.select_photo);
        remove_photo = mDialog.findViewById(R.id.remove_photo);

        tashieLoader = view.findViewById(R.id.lazyLoader);

        DottedLoader();
        updateProfileInfo();

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CropImage.startPickImageActivity(getActivity());
                mDialog.show();
                select_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,IMAGE_REGUEST);
                        mDialog.cancel();
                    }
                });

                remove_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                       reference.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               HashMap<String, Object> hashMap = new HashMap<>();
                               hashMap.put("imageUrl", "default");
                               reference.updateChildren(hashMap);
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                        mDialog.cancel();
                    }
                });
            }
        });

        btn_change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CropImage.startPickImageActivity(getActivity());
                mDialog.show();
                select_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,IMAGE_REGUEST);
                        mDialog.cancel();
                    }
                });

                remove_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("imageUrl", "default");
                                reference.updateChildren(hashMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        mDialog.cancel();
                    }
                });
            }
        });

        dialog_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        Username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UsernameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowNumberActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BioActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        return view;
    }

    private void updateProfileInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (isAdded()){
                    User users = dataSnapshot.getValue(User.class);
                    Editusername.setText(users.getUsername());
                    tv_username.setText(users.getUsername());
                    myPhoneNumner.setText(users.getPhone());
                    myBio.setText(users.getBio());
                    tashieLoader.setVisibility(View.GONE);
                    if (users.getImageUrl().equals("default")) {
                        image_profile.setBackgroundResource(R.drawable.profile_holder_);
                    } else {
                        Glide.with(getActivity()).load(users.getImageUrl()).into(image_profile);
                    }
                    mDialog.cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (mImageUri != null){

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() +"."+ getFileExtention(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){

                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageUrl", myUrl);

                        reference.updateChildren(hashMap);
                        progressDialog.dismiss();
                    }else {

                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else {

            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REGUEST && resultCode == getActivity().RESULT_OK
        && data != null && data.getData() != null ){

            mImageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }
    }

    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
//            Uri imageUri = CropImage.getPickImageResultUri(getActivity(), data);
//            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {
//                mImageUri = imageUri;
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//            } else {
//                startCrop(imageUri);
//            }
//        }
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (requestCode == getActivity().RESULT_OK) {
//                image_profile.setImageURI(result.getUri());
//                Toast.makeText(getActivity(), "done!!!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void startCrop(Uri imageUri) {
//       Intent intent = CropImage.activity(imageUri)
//                .setAspectRatio(1, 1)
//                .setCropShape(CropImageView.CropShape.OVAL)
//                .getIntent(getContext());
//        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
//    }


    void DottedLoader() {
        TashieLoader tashie = new TashieLoader(
                getContext(), 5,
                30, 10,
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        tashie.setAnimDuration(500);
        tashie.setAnimDelay(100);
        tashie.setInterpolator(new LinearInterpolator());

        tashieLoader.addView(tashie);
        tashieLoader.setVisibility(View.VISIBLE);

    }
}
