package ganesh.gfx.chatapp.qr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ganesh.gfx.chatapp.R;
import ganesh.gfx.chatapp.data.Friend;
import ganesh.gfx.chatapp.data.db.Contacts;
import ganesh.gfx.chatapp.main.MainpageActivity;
import ganesh.gfx.chatapp.utils.Tools;

public class ScanQR extends AppCompatActivity {

    private static final String TAG = "appgfx";
    MaterialCardView qrCard;
    Random rand;

    ListenerRegistration registration;
    Query query;
    FirebaseFirestore db;
    String myUserId;

    Contacts contacts;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registration.remove();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        contacts = new Contacts(this);

//        DB
        myUserId = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();
        query = db.collection("contacts").document(myUserId).collection("list");

        //Log.d(TAG, "My id: " + myUserId);
//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarShowQR);
        toolbar.setNavigationOnClickListener(view -> {
            // Log.d(TAG, "onCreate: GG");
            backHome();
        });


        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        String text = "{\"uid\":" + myUserId + ",\"name\":\"" + name + "\"}";

        //Log.d(TAG, "OR : "+text);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ImageView imageViewQR =
                    (ImageView) findViewById(R.id.imageViewQR);
            imageViewQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        rand = new Random();

        qrCard = (MaterialCardView) findViewById(R.id.scanCard);
        qrCard.setOnClickListener(view -> {

            setRanCols();

        });

        setRanCols();

        detectFriendScan();
    }

    private void detectFriendScan() {
        registration = query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.d(TAG, "listen:error - QRShow", e);
                return;
            }
            for (DocumentChange dc : value.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        String friendUserId = dc.getDocument().getString("userId");
                        String friendUserName = dc.getDocument().getString("name");

                        if (contacts.getContact(friendUserId).getID().equals("")) {

                            //Log.d(TAG, "onEvent: "+contacts.getContact(friendUserId).toString());

                            Log.d(TAG, "New: " + friendUserId);

                            Map<String, Object> user = new HashMap<>();
                            user.put("userId", FirebaseAuth.getInstance().getUid());
                            user.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                            db
                                    .collection("contacts")
                                    .document(friendUserId)
                                    .collection("list")
                                    .document(myUserId).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ScanQR.this, "Contact Added",
                                                Toast.LENGTH_SHORT).show();
                                        new Contacts(getBaseContext()).addContact(new Friend(friendUserId, friendUserName));
                                        Log.d(TAG, "Contacted");

                                        backHome();
                                    })
                                    .addOnFailureListener(er -> Log.d(TAG, "Error contacting", er));

                        }else {
                            //Toast.makeText(ScanQR.this, "Already added",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MODIFIED:
                        Friend frd = new Friend(dc.getDocument().getString("userId"),
                                dc.getDocument().getString("name"));
                        if (!contacts.getContact(frd.getID()).getID().equals("")) {
                            Toast.makeText(ScanQR.this, "Already added", Toast.LENGTH_SHORT).show();
                            backHome();
                        }
                        //Log.d(TAG, "Modified: " + Tools.gson.toJson(frd));
                        break;
                    case REMOVED:
                        //Log.d(TAG, "Removed: " + dc.getDocument().getData());
                        break;
                }
            }
        });
    }

    private void backHome() {
        ScanQR.this.startActivity(new Intent(ScanQR.this,MainpageActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    public static void createQR(String data, String path,
                                String charset, Map hashMap,
                                int height, int width)
            throws WriterException, IOException {

        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(data.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, width, height);
    }

    void setRanCols() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };

        int[] vals = getRandColorValue();
        int[] colors = new int[]{
                Color.argb(255, vals[0], vals[1], vals[2]),
                Color.argb(255, vals[0], vals[1], vals[2]),
                Color.argb(255, vals[0], vals[1], vals[2]),
                Color.argb(255, vals[0], vals[1], vals[2])
        };

        qrCard.setRippleColor(new ColorStateList(states, colors));
    }

    int[] getRandColorValue() {
        int vals[] = new int[9];
        switch (rand.nextInt(6)) {
            case 0:
                vals = new int[]{0, 255, rand.nextInt(256)};
                ;
                break;
            case 1:
                vals = new int[]{255, 0, rand.nextInt(256)};
                break;
            case 2:
                vals = new int[]{0, rand.nextInt(256), 255};
                break;
            case 3:
                vals = new int[]{255, rand.nextInt(256), 0};
                break;
            case 4:
                vals = new int[]{rand.nextInt(256), 0, 255};
                break;
            case 5:
                vals = new int[]{rand.nextInt(256), 255, 0};
                break;
        }
        return vals;
    }
}