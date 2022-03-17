package ganesh.gfx.chatapp.qr;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;

import ganesh.gfx.chatapp.R;
import ganesh.gfx.chatapp.data.Friend;
import ganesh.gfx.chatapp.data.db.Contacts;
import ganesh.gfx.chatapp.main.MainpageActivity;

public class ZqrActivity extends AppCompatActivity {

    class Friend {
        public String name;
        public String uid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public Friend() {

        }

        public Friend(String name, String uid) {
            this.name = name;
            this.uid = uid;
        }
    }

    ListenerRegistration registration;
    Query query;
    FirebaseFirestore db;
    String myUserId, myUserName;
    Gson gson;
    Contacts contacts;

    private static final String TAG = "appgfx";
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            backHome();
        } else {


            String qr = result.getContents();

            Log.d(TAG, "qr : " + qr);

            if (qr.equals("")) {
                tosty("");
            }

            Friend friendFromQr = null;

            try {
                friendFromQr = gson.fromJson(qr, Friend.class);
                if (friendFromQr.uid == null) {
                    tosty(qr);
                }


                // Log.d(TAG, "Friend Name : "+ friend.name);
                //Log.d(TAG, "Friend ID : "+ friend.uid);


                // FIXME: 3/6/22
                Map<String, Object> user = new HashMap<>();
                user.put("userId", myUserId);
                user.put("name", myUserName);
                user.put("timestamp", System.currentTimeMillis());


                if (contacts.getContact(friendFromQr.uid).getID().equals("")) {
                    Friend finalFriendFromQr = friendFromQr;
                    db
                            .collection("contacts")
                            .document(friendFromQr.uid)
                            .collection("list")
                            .document(myUserId)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
                                new Contacts(this).addContact(new ganesh.gfx.chatapp.data.Friend(finalFriendFromQr.uid, finalFriendFromQr.name));

                                backHome();
                            })
                            .addOnFailureListener(e -> Log.d(TAG, "Error contacting", e));
                } else {

                    Toast.makeText(this, "Already added", Toast.LENGTH_SHORT).show();
                    backHome();
                }

                Map<String, Object> userMe = new HashMap<>();
                userMe.put("userId", friendFromQr.uid);
                userMe.put("name", friendFromQr.name);

            } catch (Exception err) {
                Log.d(TAG, "Err : "+err.getMessage());
                tosty(qr);

            }
        }
    });

    private void tosty(String qr) {
        Toast.makeText(this, "\uD83E\uDD14 " + qr + " \uD83D\uDC40", Toast.LENGTH_LONG).show();
        backHome();
    }

    private void backHome() {
        ZqrActivity.this.startActivity(new Intent(ZqrActivity.this,
                MainpageActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zqr);

        gson = new Gson();

        contacts = new Contacts(this);

        //        DB
        myUserId = FirebaseAuth.getInstance().getUid();
        myUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        db = FirebaseFirestore.getInstance();
        //

        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(false);
        options.setOrientationLocked(false);
        options.setBarcodeImageEnabled(false);
        options.setPrompt("Scanning...");

        barcodeLauncher.launch(options);
    }
}