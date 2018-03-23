package societino.com.societinof;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VerifyUser extends AppCompatActivity {
    private ProgressDialog verifyDailog;
    FirebaseFirestore db;
    Hero curUser;
    //DocumentSnapshot curUserDoc;
    List<DocumentSnapshot> mList;
    private RecyclerView recyclerView;
    private TextView tvMsg;
    private UserVerifyAdapter sAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user);

        recyclerView = findViewById(R.id.rView);
        tvMsg = findViewById(R.id.tvMsg);



        verifyDailog = new ProgressDialog(VerifyUser.this);
        verifyDailog.setMessage("Verifying...");
        verifyDailog.setTitle("Societino");
        verifyDailog.show();

        db=FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        curUser=intent.getParcelableExtra("userDoc");
        //Log.d("here",curUserDoc.toString());
        String mySociety  = curUser.getSocName();
        mList = new ArrayList<>();
        db.collection("Members")
                .whereEqualTo("societyUniqueCode",mySociety)  // get admin soc
                .whereEqualTo("isComplete",true)
                .whereEqualTo("isVerified",false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                mList.add(document);
                            }
                            verifyDailog.dismiss();

                            if(mList.size()<=0)
                            {
                                tvMsg.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                            else
                            {
                                recyclerView.setLayoutManager(new LinearLayoutManager(VerifyUser.this));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.addItemDecoration(new DividerItemDecoration(VerifyUser.this, DividerItemDecoration.VERTICAL));

                                sAdapter = new UserVerifyAdapter(mList, VerifyUser.this);
                                sAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(sAdapter);
                            }

                        }
                        else {
                            verifyDailog.dismiss();
                            Toast.makeText(VerifyUser.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
