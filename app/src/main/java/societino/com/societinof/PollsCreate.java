package societino.com.societinof;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PollsCreate extends AppCompatActivity {
    private Button addPoll;
    private TextInputEditText t1,t2,t3,t4,ques;
    private FirebaseFirestore db;
    private String socName;
    Hero curUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls_create);
        addPoll=findViewById(R.id.addPoll);
        Intent intent = getIntent();
        curUser = intent.getParcelableExtra("userDoc");
        t1=findViewById(R.id.opt1);
        t2=findViewById(R.id.opt2);
        t3=findViewById(R.id.opt3);
        t4=findViewById(R.id.opt4);
        ques=findViewById(R.id.pollQuestion);
        //FirebaseApp.initializeApp(this);
        db=FirebaseFirestore.getInstance();
        socName=curUser.getSocName();



        addPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String question = ques.getText().toString();
                String opt1 = t1.getText().toString();
                String opt2 = t2.getText().toString();
                String opt3 = t3.getText().toString();
                String opt4 = t4.getText().toString();

                if(question.equals("")||opt1.equals("")||opt2.equals("")){
                    Toast.makeText(PollsCreate.this, "Fill in the required fields!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d( "here","stage1");
                    final ArrayList<String> options = new ArrayList<>();
                    options.add(opt1);
                    options.add(opt2);
                    if(!opt3.equals(""))
                    {
                        options.add(opt3);
                    }
                    if(!opt4.equals(""))
                    {
                        options.add(opt4);
                    }


                    Map<String, Object> user = new HashMap<>();
                    user.put("socName", socName);
                    user.put("ques", question);
                    user.put("totalOptions",options.size());
                    db.collection("polls")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("message", "sent");

                                    for (int i = 0; i < options.size(); i++) {
                                        String opt = options.get(i);
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("option", opt);
                                        data.put("totalCount", 0);
                                        db.collection("polls").document(documentReference.getId()).collection("options")
                                                .add(data);
                                    }

                                    Toast.makeText(PollsCreate.this,"Poll successfully created!",Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(PollsCreate.this,PollsHome.class);
                                    i.putExtra("userDoc", curUser);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    finish();
                                    startActivity(i);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("message","failed");
                                }
                            });
                }
            }
        });



    }
}
