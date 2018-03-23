package societino.com.societinof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NoticeText extends AppCompatActivity {
    private Button b1;
    private TextInputEditText t1, t2, t3;
    private String date,subject,text;
    private FirebaseFirestore db;
    FirebaseAuth mAuth;
    private String socName;
    private ProgressDialog uploadDialog;
    Hero curUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_text);
        t1 = (TextInputEditText) findViewById(R.id.noticeDate);
        t2 = (TextInputEditText) findViewById(R.id.noticeSubject);
        t3 = (TextInputEditText) findViewById(R.id.noticeText);
        b1=(Button)findViewById(R.id.noticeUpload);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Intent intent =getIntent();
        curUser=intent.getParcelableExtra("userDoc");
        socName=curUser.getSocName();
        uploadDialog = new ProgressDialog(NoticeText.this);
        uploadDialog.setMessage("Uploading...");
        uploadDialog.setTitle("Societino");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = t1.getText().toString();
                subject = t2.getText().toString();
                text=t3.getText().toString();
                if(date.equals("")||subject.equals("")||text.equals("")){
                    Toast.makeText(NoticeText.this, "Fill in all the blanks.", Toast.LENGTH_SHORT).show();
                }
                else{
                    date = t1.getText().toString();
                    subject=t2.getText().toString();
                    text=t3.getText().toString();
                    Map<String, Object> user = new HashMap<>();
                    user.put("date",date);
                    user.put("subject",subject);
                    user.put("noticeText",text);
                    user.put("type","notice");
                    user.put("socName",socName);
                    db.collection("notice")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(final DocumentReference documentReference) {
                                    Toast.makeText(NoticeText.this, "Notice uploaded successfully!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(NoticeText.this, NavDrawer.class);
                                    intent.putExtra("userDoc",curUser);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(NoticeText.this, "Notice upload unsuccessful!", Toast.LENGTH_SHORT).show();

                                }
                            });
                }

            }
        });




    }
}
