package ganesh.gfx.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ganesh.gfx.chatapp.utils.Tools;

public class otp extends AppCompatActivity {

    Activity activity;

    String TAG = "appgfx";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private FirebaseAuth mAuth;

    private boolean mVerificationInProgress = false;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    Button okOtp,reSndOtp;
    TextInputLayout otpInput;
    ProgressBar loading;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp2);

        activity = this;

        otpInput = (TextInputLayout)findViewById(R.id.oto_inpt);
        okOtp = findViewById(R.id.okOtp);
        reSndOtp = findViewById(R.id.reSndOtp);
        loading = findViewById(R.id.otpLoad);

        okOtp.setEnabled(false);
        reSndOtp.setEnabled(false);

        setLoading(true);

        otpAuth(getIntent().getStringExtra("number"));

        userName = getIntent().getStringExtra("name");

        okOtp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setLoading(true);
                String code = otpInput.getEditText().getText().toString();
                //Tools.ServeTost("Please Wait",getApplicationContext());
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
                //Log.d(TAG, "mVerificationId:" + mVerificationId);
                // Log.d(TAG, "code:" + code);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }
    void setLoading(boolean flag){
        if(flag){
          hideKeyboard();
            okOtp.setVisibility(View.INVISIBLE);
            reSndOtp.setVisibility(View.INVISIBLE);
            otpInput.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
        }else{
            okOtp.setVisibility(View.VISIBLE);
            reSndOtp.setVisibility(View.VISIBLE);
            otpInput.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
        }
    }
    void otpAuth(String num){
        sendAuthReq(num);
        SharedPreferences settings = getApplicationContext().getSharedPreferences("vadati", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("myNo",num);
        editor.apply();
        //Toast.makeText(this.getApplication(), num, Toast.LENGTH_SHORT).show();
    }
    void sendAuthReq(String num){
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                //Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                //updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                //signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                //Log.e(TAG, "onVerificationFailed"+e.toString());
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    //mBinding.fieldPhoneNumber.setError("Invalid phone number.");
                    //Log.d(TAG,"Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    // Log.d(TAG,"SMS Quota exceeded.");
//                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
//                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                //updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                okOtp.setEnabled(true);
                setLoading(false);

                // [START_EXCLUDE]
                // Update UI
                //updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(num)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName).build();

                            user.updateProfile(profileUpdates);

                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("name",userName);

                            FirebaseFirestore.getInstance().collection("profiles").document(
                                    user.getUid()).set(userInfo)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Profile updated");
                                        Tools.ServeTost("Successful",getApplicationContext());
                                        Intent myIntent = new Intent(otp.this, MainActivity.class);
                                        otp.this.startActivity(myIntent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Log.d(TAG, "Error updating profile",
                                            e));

                            //Log.d(TAG, "FirebaseUser : "+Tools.gson.toJson(user));


                            // ...
                        } else {
                            setLoading(false);
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Tools.ServeTost("Invalid OTP",getApplicationContext());
                                Log.d(TAG, "The verification code entered was invalid");
                                Log.d(TAG, "OTP ERR: "+task.getException().getMessage());
                            }
                        }
                    }
                });
    }
    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}