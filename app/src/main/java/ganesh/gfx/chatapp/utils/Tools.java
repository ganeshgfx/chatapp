package ganesh.gfx.chatapp.utils;


import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Tools {
    private static final String TAG = "gfx";
    public static Gson gson = new Gson();
    public static String MD5(final String s) {
        final String MD5 = "MD5";
        try{
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void ServeTost(String msg,Context Context){
        Toast.makeText(Context, msg, Toast.LENGTH_LONG).show();
    }
    public static void verCek(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("vadati").document("ved");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "data: " + document.get("version_info"));
                } else {
                    //Log.d(TAG, "No such document");
                }
            } else {
                //Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
    public static int genRandom(int Min,int Max){
        return  (int)(Math.random() * (Max - Min + 1) + Min);
    }
    public static String ByteArrToJsonStr(byte[] array) {
        char[] chars = new char[array.length];
        for (int i = 0;i<array.length;i++) {
            chars[i]= (char) array[i];
        }
        return gson.toJson(chars);
    }
    public static String encode(byte[] data){
        //return Base64.getEncoder().encodeToString(data);
        return Base64.encodeToString(data, Base64.URL_SAFE);
    }
    public static byte[] decode(String data){
        return  Base64.decode(data, Base64.URL_SAFE);
    }

}
