package societino.com.societinof;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PollVote extends AppCompatActivity {
    private TextView t1;
    private String docId,question;
    private Button o1,o2,o3,o4;
    private Long totalOptions;
    private FirebaseFirestore db;
    private String userId,type;
    ArrayList<DocumentSnapshot> oList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_vote);
        t1=findViewById(R.id.pollQuestionView);
        o1=findViewById(R.id.opt1View);
        o2=findViewById(R.id.opt2View);
        o3=findViewById(R.id.opt3View);
        o4=findViewById(R.id.opt4View);
        db=FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //userId = "dummy";


        totalOptions = intent.getLongExtra("totalOptions",0);
        docId = intent.getStringExtra("docId");
        question=intent.getStringExtra("question");
        if(totalOptions==4)
        {
            o3.setVisibility(View.VISIBLE);
            o4.setVisibility(View.VISIBLE);
        }
        else if(totalOptions==3){
            o3.setVisibility(View.VISIBLE);
        }




        db.collection("votes").whereEqualTo("pollId",docId)
                .whereEqualTo("userId",userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size()>0)
                            {
                                o1.setEnabled(false);
                                o2.setEnabled(false);
                                o3.setEnabled(false);
                                o4.setEnabled(false);

                            }
                        }
                    }
                });





        db.collection("polls").document(docId).collection("options")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for (DocumentSnapshot document : task.getResult()) {
                                oList.add(document);
                            }
                            o1.setText(oList.get(0).getString("option")+" ("+oList.get(0).getLong("totalCount")+")");
                            o2.setText(oList.get(1).getString("option")+" ("+oList.get(1).getLong("totalCount")+")");
                            if(totalOptions==3)
                                o3.setText(oList.get(2).getString("option")+" ("+oList.get(2).getLong("totalCount")+")");
                            if(totalOptions==4){
                                o3.setText(oList.get(2).getString("option")+" ("+oList.get(2).getLong("totalCount")+")");
                                o4.setText(oList.get(3).getString("option")+" ("+oList.get(3).getLong("totalCount")+")");
                            }


                        }
                    }
                });


        o1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long count = oList.get(0).getLong("totalCount")+1;
                Map<String, Object> data = new HashMap<>();
                data.put("totalCount", count);

                db.collection("polls").document(docId).collection("options")
                        .document(oList.get(0).getId())
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String, Object> obj = new HashMap<>();
                                obj.put("userId", userId);
                                obj.put("pollId",docId);
                                obj.put("optionId",oList.get(0).getId());

                                db.collection("votes").document().set(obj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PollVote.this, "Vote casted successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            }
                        });
            }
        });
        o2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long count = oList.get(1).getLong("totalCount")+1;
                Map<String, Object> data = new HashMap<>();
                data.put("totalCount", count);

                db.collection("polls").document(docId).collection("options")
                        .document(oList.get(1).getId())
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String, Object> obj = new HashMap<>();
                                obj.put("userId", userId);
                                obj.put("pollId",docId);
                                obj.put("optionId",oList.get(1).getId());

                                db.collection("votes").document().set(obj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PollVote.this, "Vote casted successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            }
                        });
            }
        });
        o3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long count = oList.get(2).getLong("totalCount")+1;
                Map<String, Object> data = new HashMap<>();
                data.put("totalCount", count);

                db.collection("polls").document(docId).collection("options")
                        .document(oList.get(2).getId())
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String, Object> obj = new HashMap<>();
                                obj.put("userId", userId);
                                obj.put("pollId",docId);
                                obj.put("optionId",oList.get(2).getId());

                                db.collection("votes").document().set(obj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PollVote.this, "Vote casted successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            }
                        });
            }
        });
        o4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long count = oList.get(3).getLong("totalCount")+1;
                Map<String, Object> data = new HashMap<>();
                data.put("totalCount", count);

                db.collection("polls").document(docId).collection("options")
                        .document(oList.get(3).getId())
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String, Object> obj = new HashMap<>();
                                obj.put("userId", userId);
                                obj.put("pollId",docId);
                                obj.put("optionId",oList.get(3).getId());

                                db.collection("votes").document().set(obj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PollVote.this, "Vote casted successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                            }
                        });
            }
        });



    }
}
