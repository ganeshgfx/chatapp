package ganesh.gfx.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputLayout;

public class login extends AppCompatActivity {

    TextInputLayout myNameSrt, myNoSrt;
    String nameError;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;

        myNameSrt = (TextInputLayout) findViewById(R.id.MyName);
        myNoSrt = (TextInputLayout) findViewById(R.id.MyNo);

        myNameSrt.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validateName(myNameSrt.getEditText().getText().toString())) {
                    myNameSrt.setError(nameError);
                } else {
                    myNameSrt.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        myNoSrt.getEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        findViewById(R.id.okMyNo).setOnClickListener(v -> {

            String str_name = myNameSrt.getEditText().getText().toString();
            //Log.d(Constants.TAG, "MY NAME"+str_name);
            String str_no = myNoSrt.getEditText().getText().toString();

            boolean isNumberGood = android.util.Patterns.PHONE.matcher(str_no).matches();

//                TextInputLayout textInputLayout = findViewById(R.id.custom_end_icon);
//                String text = textInputLayout.getEditText().getText();

            if (!validateName(str_name)) {
                myNameSrt.setError(nameError);
            } else {
                myNameSrt.setError("");
            }

            if (str_no.equals("")) {
                //Toast.makeText(number_inpt.this, "☎️ INPUT NUMBER !", Toast.LENGTH_SHORT).show();
                myNoSrt.setError("☎️ INPUT NUMBER !");
            } else if (str_no.length() != 10) {
                //Toast.makeText(number_inpt.this, "☎️ INPUT VALID NUMBER !", Toast.LENGTH_SHORT).show();
                myNoSrt.setError("☎️ INPUT VALID NUMBER !");
            } else if (str_no.length() == 10 && validateName(str_name)) {

                SharedPreferences settings = getApplicationContext().getSharedPreferences("vadati", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("myName", str_name);
                editor.apply();

                skipToHome(str_no,str_name);
            }
        });
    }

    void skipToHome(String num,String name) {

        hideKeyboard();

        Intent myIntent = new Intent(login.this, otp.class);
        myIntent.putExtra("number", "+91" + num);
        myIntent.putExtra("name", name);
        login.this.startActivity(myIntent);
        finish();
    }

    boolean validateName(String Name) {

        boolean isValid = false;

        if (Name.length() == 0) {
            nameError = "Field cannot be empty";
            isValid = false;
        } else if (Name.charAt(0) == ' ') {
            nameError = "You cannot use 'SPACE' at beginning";
            isValid = false;
        } else if (!Name.matches("[a-zA-Z0-9 ]+")) {
            nameError = "Enter only A-Z, a-z, 0-9 character";
            isValid = false;
        } else {
            isValid = true;
            //Toast.makeText(MainActivity.this,"Validation Successful",Toast.LENGTH_LONG).show();
        }
        return isValid;
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