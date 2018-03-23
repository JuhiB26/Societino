package societino.com.societinof;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class NoticePDF extends AppCompatActivity {
    private Button b1;

    StorageReference mStorageReference;
    private TextInputEditText t1, t2;
    private String date,subject;
    FirebaseFirestore db;
    private String socName;
    Hero curUser;
    private String downloadUrl;
    //Pdf request code
    private int PICK_PDF_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Uri filePath;
    private ProgressDialog uploadDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_pdf);
        requestStoragePermission();
        b1=(Button)findViewById(R.id.uploadPdf);
        t1 = (TextInputEditText) findViewById(R.id.noticeDate);
        t2 = (TextInputEditText) findViewById(R.id.noticeSubject);
        uploadDialog = new ProgressDialog(NoticePDF.this);
        uploadDialog.setMessage("Uploading...");
        uploadDialog.setTitle("Societino");
        mStorageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        Intent intent =getIntent();
        curUser=intent.getParcelableExtra("userDoc");
        socName=curUser.getSocName();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                date = t1.getText().toString();
                subject = t2.getText().toString();

                if(date.equals("")||subject.equals("")){
                    Toast.makeText(NoticePDF.this, "Fill in all the blanks.", Toast.LENGTH_SHORT).show();
                }
                else {

                    showFileChooser();

                }

            }
        });



    }



  /*  private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file 
                uploadFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    private void uploadFile(Uri data) {
        uploadDialog.show();
        final String curTime = String.valueOf(System.currentTimeMillis());
        StorageReference sRef = mStorageReference.child( "NoticePDF/"+socName+"/"+ curTime + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                        Map<String, Object> data = new HashMap<>();
                        data.put("subject", subject);
                        data.put("pdf",downloadUrl);
                        data.put("date",date);
                        data.put("type","pdf");
                        data.put("socName",socName);
                        data.put("path",curTime);
                        db.collection("notice")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(final DocumentReference documentReference) {
                                        Toast.makeText(NoticePDF.this, "Notice upload successful!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(NoticePDF.this, NavDrawer.class);
                                        intent.putExtra("userDoc",curUser);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                        uploadDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        uploadDialog.dismiss();
                                        Toast.makeText(NoticePDF.this, "Registeration failed", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }




    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadFile(filePath);

        }
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                //Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
















}
