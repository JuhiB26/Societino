package societino.com.societinof;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplainHome extends AppCompatActivity {
    private FloatingActionButton addComplaint;
    private ProgressDialog uploadDialog;
    StorageReference mStorageReference;
    private String date, subject,text,name;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private byte[] barry;
    private StorageReference imagesRef,storageRef;
    private String downloadUrl;
    private String socName;
    Hero curUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String curTime;
    private RecyclerView recyclerView;
    List<DocumentSnapshot> sList = new ArrayList<>();
    ComplaintAdapter nAdapter;
    boolean imageUploaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_home);
        Intent intent = getIntent();
        curUser = intent.getParcelableExtra("userDoc");
        socName = curUser.getSocName();
        name=curUser.getUname();
        addComplaint=findViewById(R.id.addComplaintBtn);
        db=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        recyclerView = findViewById(R.id.complaintList);

        addComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog;
                dialog = new Dialog(ComplainHome.this);
                dialog.setContentView(R.layout.complaintdialog);
                dialog.setTitle("Verification");
                dialog.show();
                mStorageReference = FirebaseStorage.getInstance().getReference();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                Button b1=(Button)dialog.findViewById(R.id.complaintUploadImage);
                Button b2=(Button)dialog.findViewById(R.id.complaintSubmit);
                final TextInputEditText t1 = (TextInputEditText) dialog.findViewById(R.id.complaintDate);
                final TextInputEditText t2 = (TextInputEditText) dialog.findViewById(R.id.complaintSubject);
                final TextInputEditText t3 = (TextInputEditText) dialog.findViewById(R.id.complaintText);


                uploadDialog = new ProgressDialog(ComplainHome.this);
                uploadDialog.setMessage("Uploading...");
                uploadDialog.setTitle("Societino");

                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                            Intent intent = new Intent(Intent.ACTION_PICK);
                            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                            String path = pictureDirectory.getPath();
                            Uri data = Uri.parse(path);
                            intent.setDataAndType(data, "image/*");
                            startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
                            imageUploaded=true;


                    }
                });



                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        date = t1.getText().toString();
                        subject = t2.getText().toString();
                        text=t3.getText().toString();

                        if (date.equals("") || subject.equals("")) {
                            Toast.makeText(ComplainHome.this, "Fill in all the blanks.", Toast.LENGTH_SHORT).show();
                        }

                        else{
                        Map<String, Object> data = new HashMap<>();
                        data.put("subject", subject);
                        data.put("image", downloadUrl);
                        data.put("date", date);
                        data.put("username",name);
                        data.put("isResolved",false);
                        data.put("complaint", text);
                        data.put("imageUploaded",imageUploaded);
                        data.put("socName", socName);

                        data.put("path", curTime);
                        db.collection("complaint")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(final DocumentReference documentReference) {
                                        Toast.makeText(ComplainHome.this, "Complaint uploaded successfully!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ComplainHome.this, NavDrawer.class);
                                        intent.putExtra("userDoc", curUser);
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
                                        Toast.makeText(ComplainHome.this, "Complaint upload unsuccessful!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                    }
                });





            }
        });



        db.collection("complaint")
                .whereEqualTo("socName", socName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                sList.add(document);
                            }

                            if (sList.size() > 0) {
                                recyclerView.setLayoutManager(new LinearLayoutManager(ComplainHome.this));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.addItemDecoration(new DividerItemDecoration(ComplainHome.this, DividerItemDecoration.VERTICAL));

                                nAdapter = new ComplaintAdapter(sList, ComplainHome.this,curUser);
                                nAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(nAdapter);
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                findViewById(R.id.tv).setVisibility(View.VISIBLE);
                            }


                        } else {
                        }
                    }
                });













    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                InputStream inputStream;
                try {
                    uploadDialog.show();
                    inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap d = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Log.d("Creating Bmap", "Success");
                    d.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                    scaled.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    barry = baos.toByteArray();
                    curTime = String.valueOf(System.currentTimeMillis());
                    imagesRef = storageRef.child("ComplaintsImage/" + socName + "/" + curTime);
                    final UploadTask uploadTask = imagesRef.putBytes(barry);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ComplainHome.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            uploadDialog.dismiss();

                            downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                            Toast.makeText(ComplainHome.this, "Image Upload Successful!", Toast.LENGTH_SHORT).show();


                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    uploadDialog.dismiss();
                                    Toast.makeText(ComplainHome.this, "Failed Uploading image", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
