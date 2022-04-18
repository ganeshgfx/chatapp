package ganesh.gfx.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import ganesh.gfx.chatapp.main.MainpageActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView imageView = (ImageView)findViewById(R.id.image_spash);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(700);
        imageView.startAnimation(animation);
        imageView.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(() -> {
            SharedPreferences settings = getSharedPreferences("vadati",0);
            String myNo = settings.getString("myNo", "NULL");

            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent myIntent = new Intent(MainActivity.this, MainpageActivity.class);
                MainActivity.this.startActivity(myIntent);
                finish();
            }else{
                Intent myIntent = new Intent(MainActivity.this, login.class);
                MainActivity.this.startActivity(myIntent);
                finish();
            }
        }, 800);

    }
}