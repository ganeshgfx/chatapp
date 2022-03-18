package ganesh.gfx.chatapp.main;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import java.util.List;
import java.util.Random;

import ganesh.gfx.chatapp.R;
import ganesh.gfx.chatapp.data.Friend;
import ganesh.gfx.chatapp.data.db.Contacts;

import ganesh.gfx.chatapp.databinding.ActivityMainpageBinding;
import ganesh.gfx.chatapp.main.settings.Settings;
import ganesh.gfx.chatapp.qr.ScanQR;
import ganesh.gfx.chatapp.qr.ZqrActivity;
import ganesh.gfx.chatapp.utils.Tools;


public class MainpageActivity extends AppCompatActivity {

    private static final String TAG = "appgfx";
    private AppBarConfiguration appBarConfiguration;
    private static ActivityMainpageBinding binding;
    private static boolean isContactPage = true;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.top_app_bar, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    static MaterialToolbar toolbar;
    static ImageButton settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding = ActivityMainpageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        toolbar = (MaterialToolbar) findViewById(R.id.toolbar);

        settings = findViewById(R.id.imageButtonSettings);
        settings.setOnClickListener(view -> MainpageActivity.this.startActivity(new Intent(MainpageActivity.this,
                Settings.class)));

//        toolbar.setOnMenuItemClickListener(item -> {
//            switch (item.getItemId()){
//                case R.id.action_settings_top:
//
//                    return true;
//            }
//            return false;
//        });

        binding.fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

               // Log.d(TAG, "onClick: Cam "+checkCamPermision());
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    Snackbar.make(view, "📷 Please allow camera permission", Snackbar.LENGTH_LONG).setAction("Allow", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainpageActivity.this, new String[] {Manifest.permission.CAMERA}, 100);
                        }
                    }).show();
                }
                else
                    showDiolog();


            }
        });
        dbTest(this);
        handleFire();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main2);

        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public static void hideFab(){
        binding.fab.setVisibility(View.INVISIBLE);
        binding.textLayoutBox.setVisibility(View.VISIBLE);
        isContactPage = false;
        toolbar.setNavigationIcon(R.drawable.ic_sharp_arrow_back_ios_24);
        settings.setVisibility(View.GONE);
    }
    public static void showFab(){
        binding.fab.setVisibility(View.VISIBLE);
        binding.textLayoutBox.setVisibility(View.GONE);
        isContactPage = true;
        settings.setVisibility(View.VISIBLE);
    }
    Random rand;
    MaterialCardView qrCard;
    void showDiolog(){

        rand = new Random();

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.scan_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button showQR = (Button)dialogView.findViewById(R.id.showQR);
        final Button scanQR = (Button)dialogView.findViewById(R.id.scanQR);

        scanQR.setOnClickListener(view -> {
            Intent myIntent = new Intent(MainpageActivity.this, ZqrActivity.class);
            MainpageActivity.this.startActivity(myIntent);
        });

        showQR.setOnClickListener(view -> {
            Intent myIntent = new Intent(MainpageActivity.this, ScanQR.class);
            MainpageActivity.this.startActivity(myIntent);
        });

        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

        dialogBuilder.setTitle(" ");

        dialogBuilder.show();

        setQR(dialogView);
    }

    private void setQR(View v) {
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        String text = "{\"uid\":" + FirebaseAuth.getInstance().getUid() + ",\"name\":\"" + name + "\"}";

        //Log.d(TAG, "OR : "+text);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ImageView imageViewQR =
                    (ImageView) v.findViewById(R.id.popImageViewQR);
            imageViewQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        rand = new Random();

        qrCard = (MaterialCardView) v.findViewById(R.id.popScanCard);
        qrCard.setOnClickListener(view -> {

            setRanCols();

        });

        setRanCols();


    }

    private void handleFire() {
       // Log.d(TAG, "handleFire: HERE");

    }

    private boolean checkCamPermision()
    {
        String permission = android.Manifest.permission.CAMERA;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
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
    void dbTest(Context context){

        //Log.d(TAG, "dbTest: HERE");

//        Contacts db = new Contacts(context);

        // Inserting Contacts
//        db.addContact(new Friend( "9A100000000","Ravi"));
//        db.addContact(new Friend( "91999999dsdsfd99","Srinivas"));
//        db.addContact(new Friend("95222SS22222","Tommy"));
//        db.addContact(new Friend("953333c3333","Karthik"));

        //List<Friend> contacts = db.getAllContacts();

//        for (Friend cn : contacts) {
//            String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() ;
//            // Writing Contacts to log
//            Log.d(TAG, log);
//        }
    }
}