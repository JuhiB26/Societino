package societino.com.societinof;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryHome extends AppCompatActivity {
    private FloatingActionButton addAlbum;
    private TextInputEditText albumName;
    private RecyclerView recyclerView;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private Dialog dialog;
    private ProgressDialog progressDailog;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference imagesRef, storageRef, showImage;
    //  private Button b2;
    private ProgressDialog uploadDialog;
    private byte[] barry;
    private String downloadUrl;
    private String socName;
    private List<DocumentSnapshot> sList;
    private FolderAdapter fAdapter;
    Hero curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_home);
        addAlbum = (FloatingActionButton) findViewById(R.id.addBtn);
        dialog = new Dialog(GalleryHome.this);
        dialog.setContentView(R.layout.album_name);
        dialog.setTitle("Societino");

        recyclerView = findViewById(R.id.viewFolder);
        Intent intent =getIntent();
        curUser=intent.getParcelableExtra("userDoc");

        sList = new ArrayList<>();

        //Progress Dialog
        progressDailog = new ProgressDialog(GalleryHome.this);
        progressDailog.setMessage("Creating Album...");
        progressDailog.setTitle("Societino");
        progressDailog.setCancelable(false);

        /*Intent intent = getIntent();
          assume socName is retrived here*/
        // socName = "mysoc";
        final String mysociety  = curUser.getSocName();
        db = FirebaseFirestore.getInstance();
        db.collection("gallery")
                .whereEqualTo("socName", mysociety)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                sList.add(document);
                            }
                            Log.d("sList", sList.size() + "");
                            if (sList.size() > 0) {
                                recyclerView.setLayoutManager(new GridLayoutManager(GalleryHome.this, 3));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                //    recyclerView.addItemDecoration(new DividerItemDecoration(GalleryHome.this,DividerItem));
                                fAdapter = new FolderAdapter(sList, GalleryHome.this);
                                fAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(fAdapter);
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                findViewById(R.id.tv).setVisibility(View.VISIBLE);
                            }

                        } else {
                        }
                    }
                });


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        uploadDialog = new ProgressDialog(GalleryHome.this);
        uploadDialog.setMessage("Uploading...");
        uploadDialog.setTitle("Societino");
        uploadDialog.setCancelable(false);
        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //Toast.makeText(GalleryHome.this,"Create your damn album girl!",Toast.LENGTH_SHORT).show();
            }
        });
        albumName = dialog.findViewById(R.id.albumName);


        dialog.findViewById(R.id.createAlbumbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDailog.show();
                //Toast.makeText(GalleryHome.this, "Album created!", Toast.LENGTH_SHORT).show();
                final String aName = albumName.getText().toString().toLowerCase();

                if (aName.equals("")) {
                    Toast.makeText(GalleryHome.this, "Fill in all the blank", Toast.LENGTH_SHORT).show();
                    progressDailog.dismiss();
                } else {
                    db.collection("gallery")
                            .whereEqualTo("albumName", aName)
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
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("socName", mysociety);
                                            user.put("albumName", aName.toUpperCase());
                                            db.collection("gallery")
                                                    .add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(final DocumentReference documentReference) {
                                                            Toast.makeText(GalleryHome.this, "Album created!", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                            progressDailog.dismiss();
                                                            Intent intent = new Intent(GalleryHome.this, InsideAlbum.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.putExtra("socName", mysociety);
                                                            intent.putExtra("albumName", aName.toUpperCase());
                                                            intent.putExtra("docId", documentReference.getId());
                                                            intent.putExtra("images", new ArrayList<String>());
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDailog.dismiss();
                                                            Toast.makeText(GalleryHome.this, "Album Cannot be created!", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    });

                                        } else {
                                            dialog.dismiss();
                                            progressDailog.dismiss();
                                            Toast.makeText(GalleryHome.this, "Already Existing!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        dialog.dismiss();
                                        progressDailog.dismiss();
                                        Log.e("error", "error");
                                        Toast.makeText(GalleryHome.this, "Failed to create album!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });
    }
}
