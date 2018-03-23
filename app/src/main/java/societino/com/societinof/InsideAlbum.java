package societino.com.societinof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InsideAlbum extends AppCompatActivity {
    private static final int IMAGE_GALLERY_REQUEST = 200;
    private FloatingActionButton addImage;
    private String socName, albumName,docId;
    private byte[] barry;
    private FirebaseStorage storage;
    private String downloadUrl;
    private FirebaseFirestore db;
    private StorageReference imagesRef, storageRef;
    private ArrayList<String> iList;
    private ImageAdapter iAdapter;
    private RecyclerView recyclerView;
    private ProgressDialog uploadDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_album);
        addImage = findViewById(R.id.addImageBtn);
        Intent intent = getIntent();
        socName = intent.getStringExtra("socName");
        albumName = intent.getStringExtra("albumName");
        iList=intent.getStringArrayListExtra("images");
        docId=intent.getStringExtra("docId");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db=FirebaseFirestore.getInstance();
        this.setTitle(albumName);
        recyclerView=findViewById(R.id.imageList);
        recyclerView.setLayoutManager(new GridLayoutManager(InsideAlbum.this,3 ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        iAdapter = new ImageAdapter(iList, InsideAlbum.this);
        iAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(iAdapter);


        uploadDialog = new ProgressDialog(InsideAlbum.this);
        uploadDialog.setMessage("Uploading...");
        uploadDialog.setTitle("Societino");





        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                String path = pictureDirectory.getPath();
                Uri data = Uri.parse(path);
                intent.setDataAndType(data, "image/*");
                startActivityForResult(intent, IMAGE_GALLERY_REQUEST);

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
                    imagesRef = storageRef.child("Gallery/" + socName + "/" + albumName + "/" + System.currentTimeMillis());
                    final UploadTask uploadTask = imagesRef.putBytes(barry);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(InsideAlbum.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                            iList.add(downloadUrl);
                            iAdapter.notifyDataSetChanged();
                            Map<String, Object> data = new HashMap<>();
                            data.put("images", iList);

                            db.collection("gallery").document(docId)
                                    .set(data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("added","yes");
                                            uploadDialog.dismiss();

                                            Toast.makeText(InsideAlbum.this, "Image upload succesful!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {e.printStackTrace();
                                            uploadDialog.dismiss();
                                            Toast.makeText(InsideAlbum.this,"Upload Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // b2.setText("Upload Successful");
                            // b2.setEnabled(false);
                            //Toast.makeText(InsideAlbum.this, "Upload Succesful!", Toast.LENGTH_SHORT).show();


                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //  uploadDialog.dismiss();
                                    //  Toast.makeText(InsideAlbum.this, "Failed Uploading image", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

