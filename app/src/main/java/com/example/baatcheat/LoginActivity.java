package com.example.baatcheat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private boolean isVerificationRunning;

    private EditText phoneNumber, verificationText;
    private TextView wh_number, text, countdownText, countdownTimer;
    private ImageView reset_text, next_phone_empty, backtologin;
    private Button next_phone, phone_done;

    CountryCodePicker ccp;
    String str_phoneNumber;

    ProgressBar progressBar;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    String currentUserId;

    FirebaseAuth mAuth;
    FirebaseException exception;

    private CountDownTimer cTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relativeLayout = findViewById(R.id.relativeLayout);

        phoneNumber = findViewById(R.id.phone_number);
        wh_number = findViewById(R.id.wh_number);
        text = findViewById(R.id.text);
        backtologin = findViewById(R.id.backtologin);
        next_phone = findViewById(R.id.next_phone);
        next_phone_empty = findViewById(R.id.next_phone_empty);
        reset_text = findViewById(R.id.reset_text);
        phone_done = findViewById(R.id.phone_done);
        verificationText = findViewById(R.id.verificationText);
        countdownText = findViewById(R.id.countdownText);
        countdownTimer = findViewById(R.id.countdownTimer);

        ccp = findViewById(R.id.CCP);
        ccp.registerPhoneNumberTextView(phoneNumber);

        progressBar = findViewById(R.id.progress_circular);

        mAuth = FirebaseAuth.getInstance();

        reset_text.setVisibility(View.GONE);
        next_phone.setEnabled(false);
        phone_done.setEnabled(false);
        next_phone.setBackgroundResource(R.drawable.phone_next_disabled);
        phone_done.setBackgroundResource(R.drawable.phone_done_empty);

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().isEmpty()) {
                    next_phone.setEnabled(false);
                    next_phone.setBackgroundResource(R.drawable.phone_next_disabled);
                    reset_text.setVisibility(View.GONE);

                    next_phone.setVisibility(View.VISIBLE);
                    next_phone_empty.setVisibility(View.INVISIBLE);

                    progressBar.setVisibility(View.INVISIBLE);

                } else {

                    reset_text.setVisibility(View.VISIBLE);

                    next_phone.setVisibility(View.VISIBLE);
                    next_phone_empty.setVisibility(View.INVISIBLE);

                    next_phone.setEnabled(true);
                    next_phone.setBackgroundResource(R.drawable.btn_next_phone);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        reset_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber.setText("");
            }
        });

        next_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_phoneNumber = ccp.getFullNumberWithPlus();

                if (phoneNumber.length() < 8 || phoneNumber.length() > 20) {
                    phoneNumber.startAnimation(VibrateError());
                    shakeItBaby();
                } else {
                    next_phone_empty.setVisibility(View.VISIBLE);
                    next_phone.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    /************
                     * Sendind Verification Code
                     */
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            str_phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            LoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks


                }
                hideKeyboard(LoginActivity.this);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    progressBar.setVisibility(View.GONE);
                    next_phone_empty.setVisibility(View.GONE);
                    next_phone.setVisibility(View.VISIBLE);

                    phoneNumber.startAnimation(VibrateError());
                    shakeItBaby();


                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    progressBar.setVisibility(View.GONE);
                    next_phone_empty.setVisibility(View.GONE);
                    next_phone.setVisibility(View.VISIBLE);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("You're sending too many verification code, Please try again later")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("baatCHEAT");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setCancelable(false);
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });
                    alertDialog.show();
                    return;
                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                progressBar.setVisibility(View.GONE);

                backtologin.setVisibility(View.VISIBLE);

                phoneNumber.setVisibility(View.GONE);
                ccp.setVisibility(View.GONE);
                reset_text.setVisibility(View.GONE);

                wh_number.setText("Enter verification code");

                String str_text = ("We've sent an SMS with a verification code to your phone " + str_phoneNumber.toUpperCase());

                SpannableString spannableString = new SpannableString(str_text);
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                spannableString.setSpan(boldSpan, 57, str_text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setText(spannableString);

                verificationText.setVisibility(View.VISIBLE);

                phone_done.setVisibility(View.VISIBLE);

                startTimer();

                isVerificationRunning = true;
            }
        };

        verificationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().isEmpty()) {
                    phone_done.setEnabled(false);
                    phone_done.setBackgroundResource(R.drawable.phone_done_empty);
                } else {
                    phone_done.setEnabled(true);
                    phone_done.setBackgroundResource(R.drawable.btn_phone_done);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_phone_empty.setVisibility(View.VISIBLE);
                phone_done.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                hideKeyboard(LoginActivity.this);

                String verificationCode = verificationText.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);

            }
        });

        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Do you want to stop the verification process?")
                        .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setNegativeButton("STOP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wh_number.setText("What's your mobile number?");
                        text.setText("Please confirm your country code and enter your mobile number.");
                        backtologin.setVisibility(View.GONE);

                        phoneNumber.setVisibility(View.VISIBLE);
                        ccp.setVisibility(View.VISIBLE);
                        reset_text.setVisibility(View.GONE);

                        progressBar.setVisibility(View.GONE);
                        next_phone_empty.setVisibility(View.GONE);
                        next_phone.setVisibility(View.VISIBLE);
                        phone_done.setVisibility(View.INVISIBLE);

                        verificationText.setVisibility(View.GONE);

                        phoneNumber.setText("");
                        verificationText.setText("");

                        countdownText.setVisibility(View.GONE);
                        countdownText.setAlpha(0.5f);
                        countdownText.setTextColor(getResources().getColor(R.color.colorTextDefault));
                        countdownTimer.setVisibility(View.GONE);
                        cancelTimer();
                        isVerificationRunning = false;

                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("baatCHEAT");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                });
                alertDialog.show();
            }
        });

        countdownText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (exception instanceof FirebaseTooManyRequestsException) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("You're sending too many verification code, Please try again later")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    wh_number.setText("What's your mobile number?");
                                    text.setText("Please confirm your country code and enter your mobile number.");
                                    backtologin.setVisibility(View.GONE);

                                    phoneNumber.setVisibility(View.VISIBLE);
                                    ccp.setVisibility(View.VISIBLE);
                                    reset_text.setVisibility(View.GONE);

                                    progressBar.setVisibility(View.GONE);
                                    next_phone_empty.setVisibility(View.GONE);
                                    next_phone.setVisibility(View.VISIBLE);
                                    phone_done.setVisibility(View.INVISIBLE);

                                    verificationText.setVisibility(View.GONE);

                                    phoneNumber.setText("");
                                    verificationText.setText("");

                                    countdownText.setVisibility(View.GONE);
                                    countdownText.setAlpha(0.5f);
                                    countdownText.setTextColor(getResources().getColor(R.color.colorTextDefault));
                                    countdownTimer.setVisibility(View.GONE);
                                    cancelTimer();
                                }
                            });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("baatCHEAT");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setCancelable(false);
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });
                    alertDialog.show();
                } else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            str_phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            LoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks

                    startTimer();
                }
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(LoginActivity.this);
            }
        });
    }

    public void shakeItBaby() {
        int DURATION = 500; // you can change this according to your need
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(DURATION);
        }

    }

    public TranslateAnimation VibrateError() { // Edit text vibrate Animation
        TranslateAnimation vibrate = new TranslateAnimation(0, 10, 0, 0);
        vibrate.setDuration(600);
        vibrate.setInterpolator(new CycleInterpolator(8));
        return vibrate;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            currentUserId = mAuth.getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                            reference.orderByChild("phone").equalTo(str_phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        //it means user already registered
                                        cancelTimer();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();

                                    } else {
                                        //It is new users
                                        //write an entry to your user table
                                        //writeUserEntryToDB();

                                        currentUserId = mAuth.getUid();
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("id", currentUserId);
                                        hashMap.put("phone", str_phoneNumber);
                                        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/fir-5efa8.appspot.com/o/profile-placeholder.png?alt=media&token=0f72e718-b845-4e7b-865c-76d08340f9a8");

                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    cancelTimer();
                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                    finish();
                                                    progressBar.setVisibility(View.GONE);

                                                } else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    verificationText.setText("");
                                                    next_phone_empty.setVisibility(View.INVISIBLE);
                                                    phone_done.setVisibility(View.VISIBLE);
                                                }

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            phone_done.setVisibility(View.VISIBLE);
                            next_phone_empty.setVisibility(View.INVISIBLE);
                            shakeItBaby();
                            verificationText.startAnimation(VibrateError());
//                            String msg = task.getException().toString();
//                            Toast.makeText(LoginActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished / 1000);
                String timeformat = String.format(Locale.getDefault(), "%02d", seconds);

                countdownTimer.setText(timeformat);

            }

            public void onFinish() {
                countdownText.setTextColor(getResources().getColor(R.color.colorPrimary));
                countdownTimer.setVisibility(View.VISIBLE);
                countdownTimer.setVisibility(View.GONE);
                countdownText.setAlpha(1f);
                countdownText.setClickable(true);
                cTimer.cancel();
            }
        }.start();
        countdownText.setAlpha(0.5f);
        countdownText.setTextColor(getResources().getColor(R.color.colorTextDefault));
        countdownText.setClickable(false);
        countdownText.setVisibility(View.VISIBLE);
        countdownTimer.setVisibility(View.VISIBLE);
    }


    //cancel timer
    void cancelTimer() {
            cTimer.cancel();
    }

    //Closing keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (isVerificationRunning){
            wh_number.setText("What's your mobile number?");
            text.setText("Please confirm your country code and enter your mobile number.");
            backtologin.setVisibility(View.GONE);

            phoneNumber.setVisibility(View.VISIBLE);
            ccp.setVisibility(View.VISIBLE);
            reset_text.setVisibility(View.GONE);

            progressBar.setVisibility(View.GONE);
            next_phone_empty.setVisibility(View.GONE);
            next_phone.setVisibility(View.VISIBLE);
            phone_done.setVisibility(View.INVISIBLE);

            verificationText.setVisibility(View.GONE);

            phoneNumber.setText("");
            verificationText.setText("");

            countdownText.setVisibility(View.GONE);
            countdownText.setAlpha(0.5f);
            countdownText.setTextColor(getResources().getColor(R.color.colorTextDefault));
            countdownTimer.setVisibility(View.GONE);
            cancelTimer();
            isVerificationRunning = false;
        }else {
            super.onBackPressed();
        }
    }
}

