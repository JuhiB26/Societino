package societino.com.societinof;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Logout extends AppCompatActivity {
    Button btnLogout;
    FirebaseFirestore db;
    DocumentSnapshot userDoc=null;
    FirebaseAuth mAuth;
    FirebaseUser curUser;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        mAuth=FirebaseAuth.getInstance();
        curUser=mAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("Members")
                .whereEqualTo("userId", curUser.getUid()) // get admin soc
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                userDoc = document;
                                Log.d("ifFound", "true");
                            }
                            if (userDoc != null)
                            {
                                Hero user = new Hero();
                                user.setSocName(userDoc.getString("societyUniqueCode"));
                                user.setUid(userDoc.getString("userId"));
                                user.setUname(userDoc.getString("name"));
                                user.setUserType(userDoc.getString("userDetail"));
                                boolean isVerified = userDoc.getBoolean("isVerified");
                                String userType = userDoc.getString("userDetail");
                                if (isVerified)
                                {
                                    if (userType.toLowerCase().equals("admin"))
                                    {
                                        Intent intent = new Intent(Logout.this, NavDrawer.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("userDoc", user);
                                        finish();
                                        startActivity(intent);
                                    }
                                    else
                                        {
                                        Intent intent = new Intent(Logout.this, NavDrawerUser.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("userDoc", user);
                                        finish();
                                        startActivity(intent);
                                    }

                                }
                                else
                                {
                                    progressBar.setVisibility(View.GONE);
                                }


                            }
                            else
                            {
                                Toast.makeText(Logout.this,"error",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Logout.this, NavDrawerUser.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                //intent.putExtra("userDoc", user);
                                finish();
                                startActivity(intent);

                            }
                        }

                        }

                        });


        btnLogout=findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Logout.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
