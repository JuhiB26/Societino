package societino.com.societinof;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class SplashScreen extends AppCompatActivity {
    private ImageView imgView;
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    boolean isComplete,ifFound;
    private DocumentSnapshot userDoc=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imgView=findViewById(R.id.imgView);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        mAuth= FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();


        Glide.with(this)
                .load(R.drawable.ot1)
                .into(imgView);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                updateUI(currentUser);

            }
        }, SPLASH_DISPLAY_LENGTH);

    }


    private void updateUI(final FirebaseUser currentUser) {
        if(currentUser!=null)
        {
            //signInButton.setEnabled(false);
            Log.d("userId",currentUser.getUid());
            db.collection("Members")
                    .whereEqualTo("userId", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot document : task.getResult()) {
                                    userDoc=document;
                                    ifFound=true;
                                    Log.d("ifFound","true");
                                }

                                if (ifFound)
                                {
                                    isComplete = userDoc.getBoolean("isComplete");
                                    Log.d("ifComplete", String.valueOf(isComplete));
                                }


                                if(isComplete)
                                {

                                 //   Toast.makeText(SplashScreen.this, "Signed IN!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SplashScreen.this,Logout.class);
                                    intent.putExtra("accId",currentUser.getUid());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                  //  Toast.makeText(SplashScreen.this, "Signed IN!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SplashScreen.this,UserReg.class);
                                    intent.putExtra("accId",currentUser.getUid());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }



                            } else {
                                Log.d("1", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else
        {

            Intent intent = new Intent(SplashScreen.this,MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
