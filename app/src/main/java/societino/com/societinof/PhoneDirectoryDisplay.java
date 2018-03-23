package societino.com.societinof;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PhoneDirectoryDisplay extends AppCompatActivity {
    Hero curUser;
    private RecyclerView recyclerView;
    List<DocumentSnapshot> sList = new ArrayList<>();
    PhoneDirectoryAdapter nAdapter;
    FirebaseFirestore db;
    private String socName;
    private String profession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_directory_display);
        Intent intent = getIntent();
        curUser=intent.getParcelableExtra("userDoc");

        profession = intent.getStringExtra("profession");
            socName= curUser.getSocName();
            db=FirebaseFirestore.getInstance();
            recyclerView = findViewById(R.id.phoneList);


            db.collection("Members")
                    .whereEqualTo("societyUniqueCode", socName).whereEqualTo("profession",profession)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    sList.add(document);
                                }

                                if (sList.size() > 0) {
                                    Log.d(String.valueOf(sList.size()), "1");

                                    recyclerView.setLayoutManager(new LinearLayoutManager(PhoneDirectoryDisplay.this));
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.addItemDecoration(new DividerItemDecoration(PhoneDirectoryDisplay.this, DividerItemDecoration.VERTICAL));

                                    nAdapter = new PhoneDirectoryAdapter(sList, PhoneDirectoryDisplay.this,curUser);
                                    //  nAdapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(nAdapter);
                                }


                            }
                        }
                    });





    }
}
