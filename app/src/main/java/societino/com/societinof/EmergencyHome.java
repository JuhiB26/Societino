package societino.com.societinof;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.katepratik.msg91api.MSG91;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class EmergencyHome extends AppCompatActivity {

    private ImageButton fire, police, earthquake, firebrigade, ambulance, emergency;
    private FirebaseFirestore db;
    Hero curUser;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_home);
        db = FirebaseFirestore.getInstance();
        fire = findViewById(R.id.fireBtn);
        police = findViewById(R.id.policeBtn);
        earthquake = findViewById(R.id.earthquakeBtn);
        firebrigade = findViewById(R.id.fireBrigadeBtn);
        ambulance = findViewById(R.id.ambulanceBtn);
        emergency = findViewById(R.id.emergencyBtn);
        Intent intent = getIntent();
        curUser = intent.getParcelableExtra("userDoc");
        dialog = new Dialog(EmergencyHome.this);
        dialog.setContentView(R.layout.album_name);
        dialog.setTitle("Societino");


        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dial = "tel:" + "number";
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
            }
        });
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dial = "tel:" + "number";
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
            }
        });

        firebrigade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dial = "tel:" + "number";
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
            }
        });


        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dial = "tel:" + "number";
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
            }
        });


        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyHome.this);

                builder.setTitle("Alert");
                builder.setMessage("Clicking on 'Yes' will send an emergency SMS to all members of the society. Are you sure you want to Continue?");

                builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        // Do nothing but close the dialog


                        final ArrayList<String> mobileNumbers = new ArrayList<>();
                        String mysociety = curUser.getSocName();

                        db.collection("Members")
                                .whereEqualTo("socName", mysociety)  // get admin soc
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                   /* for (DocumentSnapshot document : task.getResult()) {
                                        MSG91 msg91 = new MSG91("197174ArKfwN4BRP5a7b18ae", true);
                                        //String validate = msg91.validate();
                                        //mobileNumbers.add(document.getString("number"));
                                        msg91.composeMessage("SOCKET", "FIRE EMERGENCY!" +
                                                "Evacuate Immediately!\nSent by Societino!");
                                        msg91.to(document.getString("number"));
                                        msg91.setRoute("4");

                                        String sendStatus = msg91.send();
                                        Log.d("response", sendStatus);

                                    }*/
                                            Toast.makeText(EmergencyHome.this, "Your message has been sent!", Toast.LENGTH_SHORT).show();
                                            fire.setClickable(false);
                                            fire.setAlpha((float) 0.2);
                                        }
                                    }
                                });


                        dialog.dismiss();

                    }
                });


                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                AlertDialog alert = builder.create();
                alert.show();


            }
        });


        earthquake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyHome.this);

                builder.setTitle("Alert");
                builder.setMessage("Clicking on 'Yes' will send an emergency SMS to all members of the society. Are you sure you want to Continue?");

                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing, but close the dialog
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing

                        final ArrayList<String> mobileNumbers = new ArrayList<>();

                        db.collection("Members")
                                .whereEqualTo("socName", "mysoc")  // get admin soc
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                  /*  for (DocumentSnapshot document : task.getResult()) {
                                        mobileNumbers.add(document.getString("number"));
                                        MSG91 msg91 = new MSG91("197174ArKfwN4BRP5a7b18ae", true);
                                       // String validate = msg91.validate();
                                        //mobileNumbers.add("1234567890");
                                        msg91.composeMessage("SOCKET", "EARTHQUAKE EMERGENCY!" +
                                                "Evacuate Immediately!\nSent by Societino!");
                                        msg91.to(document.getString("number"));
                                        msg91.setRoute("4");
                                        String sendStatus = msg91.send();
                                        Log.d("response", sendStatus);

                                        Log.d("HERE",document.getString("number"));
                                    }*/

                                            Toast.makeText(EmergencyHome.this, "Your message has been sent!", Toast.LENGTH_SHORT).show();
                                            earthquake.setClickable(false);
                                            earthquake.setAlpha((float) 0.2);
                                        }

                                    }
                                });


                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }

        });


    }
}

