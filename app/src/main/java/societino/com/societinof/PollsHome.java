package societino.com.societinof;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PollsHome extends AppCompatActivity {

    private FloatingActionButton create;
    private String mySoc,type;
    private com.google.firebase.firestore.FirebaseFirestore db;
    private ArrayList<DocumentSnapshot> sList = new ArrayList<>();
    private PollAdapter pAdapter;
    private RecyclerView recyclerView;
    Hero curUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls_home);
        Intent intent = getIntent();
        curUser = intent.getParcelableExtra("userDoc");
        mySoc = curUser.getSocName();
        type=curUser.getUserType();

        recyclerView = findViewById(R.id.pollList);
        db= FirebaseFirestore.getInstance();
        db.collection("polls").whereEqualTo("socName",mySoc)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                sList.add(document);
                            }

                            if (sList.size() > 0) {
                                recyclerView.setLayoutManager(new LinearLayoutManager(PollsHome.this));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                pAdapter = new PollAdapter(sList, PollsHome.this);
                                pAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(pAdapter);
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                findViewById(R.id.tv).setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });



        create=findViewById(R.id.createPoll);
        type=curUser.getUserType();
        if(type.toLowerCase().equals("admin"))
        {
            create.setVisibility(View.VISIBLE);
        }
        else{
            create.setVisibility(View.GONE);
        }





        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PollsHome.this,PollsCreate.class);
                intent.putExtra("userDoc", curUser);
                startActivity(intent);

            }
        });


    }
}
