package societino.com.societinof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NoticeImage extends AppCompatActivity {
    private Button b1;

    StorageReference mStorageReference;
    private TextInputEditText t1, t2;
    private String date, subject;

    private String socName;
    Hero curUser;
    private String downloadUrl;
    private ProgressDialog uploadDialog;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private FirebaseFirestore db;
    private byte[] barry;
    private boolean ifUploaded = false;
    private FirebaseStorage storage;
    private StorageReference imagesRef, storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_image);
        db = FirebaseFirestore.getInstance();
        b1 = (Button) findViewById(R.id.uploadImage);
        t1 = (TextInputEditText) findViewById(R.id.noticeDate);
        t2 = (TextInputEditText) findViewById(R.id.noticeSubject);
        uploadDialog = new ProgressDialog(NoticeImage.this);
        uploadDialog.setMessage("Uploading...");
        uploadDialog.setTitle("Societino");
        mStorageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Intent intent = getIntent();
        curUser = intent.getParcelableExtra("userDoc");
        socName = curUser.getSocName();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                date = t1.getText().toString();
                subject = t2.getText().toString();

                if (date.equals("") || subject.equals("")) {
                    Toast.makeText(NoticeImage.this, "Fill in all the blanks.", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                    String path = pictureDirectory.getPath();
                    Uri data = Uri.parse(path);
                    intent.setDataAndType(data, "image/*");
                    startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
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
                    final String curTime = String.valueOf(System.currentTimeMillis());
                    imagesRef = storageRef.child("NoticeImage/" + socName + "/" + curTime);
                    final UploadTask uploadTask = imagesRef.putBytes(barry);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(NoticeImage.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            uploadDialog.dismiss();

                            downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                            Map<String, Object> data = new HashMap<>();
                            data.put("subject", subject);
                            data.put("image", downloadUrl);
                            data.put("date", date);
                            data.put("type", "image");
                            data.put("socName", socName);
                            data.put("path", curTime);
                            db.collection("notice")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(final DocumentReference documentReference) {
                                            Toast.makeText(NoticeImage.this, "Notice upload successful!", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(NoticeImage.this, NavDrawer.class);
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
                                            Toast.makeText(NoticeImage.this, "Image Upload Failed!", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    uploadDialog.dismiss();
                                    Toast.makeText(NoticeImage.this, "Failed Uploading image", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
