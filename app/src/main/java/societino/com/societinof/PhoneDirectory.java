package societino.com.societinof;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PhoneDirectory extends AppCompatActivity {
    private FirebaseFirestore db;
    Hero curUser;
    private CardView c1,c2,c3,c4,c5,c6,c7;

    //PhoneDirectoryAdapter pAdapter;
    private String socName;
    List<DocumentSnapshot> sList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_directory);

        Intent intent = getIntent();
        curUser = intent.getParcelableExtra("userDoc");
        socName=curUser.getSocName();

        Log.d("in Phone - socName",socName);
        db=FirebaseFirestore.getInstance();
        c1=findViewById(R.id.caCard);
        c2=findViewById(R.id.doctorCard);
        c3=findViewById(R.id.lawyerCard);
        c4=findViewById(R.id.pharmacistCard);
        c5=findViewById(R.id.policeCard);
        c6=findViewById(R.id.reaCard);
        c7=findViewById(R.id.teacherCard);


        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curUser != null) {
                    Intent intent = new Intent(PhoneDirectory.this, PhoneDirectoryDisplay.class);
                    intent.putExtra("userDoc", curUser);
                    intent.putExtra("profession","Chartered Accountant");
                    startActivity(intent);
                }
                else
                {
                    Log.d("1","error");
                }
            }

        });


        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curUser != null) {
                    Intent intent = new Intent(PhoneDirectory.this, PhoneDirectoryDisplay.class);
                    intent.putExtra("userDoc", curUser);
                    intent.putExtra("profession","Doctor");
                    startActivity(intent);
                }
                else
                {
                    Log.d("1","error");
                }
            }

        });



        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curUser != null) {
                    Intent intent = new Intent(PhoneDirectory.this, PhoneDirectoryDisplay.class);
                    intent.putExtra("userDoc", curUser);
                    intent.putExtra("profession","Lawyer");
                    startActivity(intent);
                }
                else
                {
                    Log.d("1","error");
                }
            }

        });



        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curUser != null) {
                    Intent intent = new Intent(PhoneDirectory.this, PhoneDirectoryDisplay.class);
                    intent.putExtra("userDoc", curUser);
                    intent.putExtra("profession","Pharmacist");
                    startActivity(intent);
                }
                else
                {
                    Log.d("1","error");
                }
            }

        });



        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curUser != null) {
                    Intent intent = new Intent(PhoneDirectory.this, PhoneDirectoryDisplay.class);
                    intent.putExtra("userDoc", curUser);
                    intent.putExtra("profession","Police officer");
                    startActivity(intent);
                }
                else
                {
                    Log.d("1","error");
                }
            }

        });



        c6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curUser != null) {
                    Intent intent = new Intent(PhoneDirectory.this, PhoneDirectoryDisplay.class);
                    intent.putExtra("userDoc", curUser);
                    intent.putExtra("profession","Real estate agent");
                    startActivity(intent);
                }
                else
                {
                    Log.d("1","error");
                }
            }

        });


        c7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curUser != null) {
                    Intent intent = new Intent(PhoneDirectory.this, PhoneDirectoryDisplay.class);
                    intent.putExtra("userDoc", curUser);
                    intent.putExtra("profession","Teacher");
                    startActivity(intent);
                }
                else
                {
                    Log.d("1","error");
                }
            }

        });







    }
}
