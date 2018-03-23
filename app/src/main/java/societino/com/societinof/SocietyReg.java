package societino.com.societinof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SocietyReg extends AppCompatActivity {



    private TextInputEditText t1, t2, t4, t5, t6, t7;
    private TextView t8;
    private FirebaseFirestore db;
    private ProgressDialog progressDoalog;
    private Spinner sp1;
    private ArrayAdapter<CharSequence> adapter1;
    private boolean isVerified = false;
    private String uname;
    private ProgressDialog verifyDailog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_society_reg);

        Button reg = (Button) findViewById(R.id.soc_regbtn);
        final Button verify = (Button) findViewById(R.id.soc_verifybtn);
        t1 = (TextInputEditText) findViewById(R.id.soc_name);
        t2 = (TextInputEditText) findViewById(R.id.soc_address);
        sp1 = (Spinner) findViewById(R.id.soc_state);
        adapter1 = ArrayAdapter.createFromResource(this, R.array.statenames, android.R.layout.simple_spinner_item);
        sp1.setAdapter(adapter1);
        t4 = (TextInputEditText) findViewById(R.id.soc_city);
        t5 = (TextInputEditText) findViewById(R.id.soc_pincode);
        t6 = (TextInputEditText) findViewById(R.id.soc_email);
        t7 = (TextInputEditText) findViewById(R.id.soc_username);
        t8 = (TextView) findViewById(R.id.soc_eg);
        db = FirebaseFirestore.getInstance();

        verifyDailog = new ProgressDialog(SocietyReg.this);
        verifyDailog.setMessage("Verifying...");
        verifyDailog.setTitle("Societino");




        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uname = t7.getText().toString().trim().toLowerCase();


                if(uname.equals(""))
                {
                    Toast.makeText(SocietyReg.this,"Enter Username!",Toast.LENGTH_LONG).show();
                }

                else {


                    Log.d("entered", "0 here");

                    verifyDailog.show();
                    db.collection("society")
                            .whereEqualTo("username", uname)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    Log.d("entered", "1: here");
                                    if (task.isSuccessful()) {
                                        int size = task.getResult().size();
                                        Log.d("totals res", "size: " + size);

                                        if (size == 0) {
                                            Log.d("doc not found", "Valid username", task.getException());
                                            verifyDailog.dismiss();
                                            Toast.makeText(SocietyReg.this, "Successfully Verified!", Toast.LENGTH_SHORT).show();
                                            isVerified = true;
                                            t7.setEnabled(false);
                                            verify.setEnabled(false);
                                            verify.setText("Verified");
                                            t8.setVisibility(View.GONE);

                                        } else {
                                            verifyDailog.dismiss();
                                            Toast.makeText(SocietyReg.this, "Already Existing.", Toast.LENGTH_SHORT).show();


                                        }
                                    } else {
                                        verifyDailog.dismiss();
                                        Log.e("error", "error");
                                        Toast.makeText(SocietyReg.this, "Failed to verify!", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });
                }


            }
        });


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = t1.getText().toString();
                String address = t2.getText().toString();
                String text1 = sp1.getSelectedItem().toString();
                Integer indexValue1 = sp1.getSelectedItemPosition();
                String city = t4.getText().toString();
                String pincode = t5.getText().toString();
                String email = t6.getText().toString();
                //String username = t7.getText().toString();
                if (name.equals("") || address.equals("") || indexValue1 == 0 || city.equals("") || pincode.equals("") || email.equals("") || uname.equals("") || isVerified == false) {
                    Toast.makeText(SocietyReg.this, "Fill in all the blanks.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDoalog = new ProgressDialog(SocietyReg.this);
                    progressDoalog.setMessage("Registering...");
                    progressDoalog.setTitle("Societino");
                    progressDoalog.show();
                    Random rand = new Random();
                    final String secret = String.format("%08d", rand.nextInt(100000000));
                    Map<String, Object> user = new HashMap<>();
                    Log.d("secret", secret);
                    user.put("name", name);
                    user.put("address", address);
                    user.put("state", text1);
                    user.put("city", city);
                    user.put("pincode", pincode);
                    user.put("email", email);
                    user.put("username", uname);
                    user.put("secret", secret);
                    user.put("isVerified", false);
                    db.collection("society")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("onsuccess", "DocumentSnapshot added with ID: " + documentReference.getId());

                                    Intent intent = new Intent(SocietyReg.this, ChairmanReg.class);
                                    intent.putExtra("secret", secret);
                                    intent.putExtra("socId", documentReference.getId());
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("onfail", "Error adding document", e);
                                    Toast.makeText(SocietyReg.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}











