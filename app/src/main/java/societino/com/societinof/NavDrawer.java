package societino.com.societinof;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeTransform;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Hero curUser;
    private FloatingActionButton fab,fab1,fab2,fab3;
    Animation fabOpen,fabClose,rotateForward,rotateBackward;
    boolean isOpen=false;
    private RecyclerView recyclerView;
    List<DocumentSnapshot> sList = new ArrayList<>();
    NoticeAdapter nAdapter;
    FirebaseFirestore db;
    private ProgressDialog uploadDialog;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    StorageReference mStorageReference;
    private FirebaseStorage storage;
    private StorageReference imagesRef,storageRef;
    private String socName;
    private String date, subject;
    private byte[] barry;
    private String imgUrl,pdfUrl;
    private int PICK_PDF_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        curUser=intent.getParcelableExtra("userDoc");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fabOpen= AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose= AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotateForward= AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotateBackward= AnimationUtils.loadAnimation(this,R.anim.rotate_backward);
        recyclerView = findViewById(R.id.noticeList);
        db = FirebaseFirestore.getInstance();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                Intent intent=new Intent(NavDrawer.this,NoticeText.class);

                intent.putExtra("userDoc",curUser);
                startActivity(intent);

                startActivity(intent);

            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

                Dialog dialog;
                dialog = new Dialog(NavDrawer.this);
                dialog.setContentView(R.layout.activity_notice_pdf);
                dialog.setTitle("Verification");
                dialog.show();
                mStorageReference = FirebaseStorage.getInstance().getReference();

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                Button b1=(Button)dialog.findViewById(R.id.uploadPdf);
                final TextInputEditText t1 = (TextInputEditText) dialog.findViewById(R.id.noticeDate);
                final TextInputEditText t2 = (TextInputEditText) dialog.findViewById(R.id.noticeSubject);

                uploadDialog = new ProgressDialog(NavDrawer.this);
                uploadDialog.setMessage("Uploading...");
                uploadDialog.setTitle("Societino");



                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        date = t1.getText().toString();
                        subject = t2.getText().toString();

                        if(date.equals("")||subject.equals("")){
                            Toast.makeText(NavDrawer.this, "Fill in all the blanks.", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            showFileChooser();

                        }

                    }
                });






























            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

                 Dialog dialog;
                dialog = new Dialog(NavDrawer.this);
                dialog.setContentView(R.layout.activity_notice_image);
                dialog.setTitle("Verification");
                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                Button b1;
                storage = FirebaseStorage.getInstance();
                storageRef = storage.getReference();

                b1 = (Button) dialog.findViewById(R.id.uploadImage);
                final TextInputEditText t1 = (TextInputEditText) dialog.findViewById(R.id.noticeDate);
                final TextInputEditText t2 = (TextInputEditText) dialog.findViewById(R.id.noticeSubject);
                uploadDialog = new ProgressDialog(NavDrawer.this);
                uploadDialog.setMessage("Uploading...");
                uploadDialog.setTitle("Societino");
                mStorageReference = FirebaseStorage.getInstance().getReference();


                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        date = t1.getText().toString();
                        subject = t2.getText().toString();

                        if (date.equals("") || subject.equals("")) {
                            Toast.makeText(NavDrawer.this, "Fill in all the blanks.", Toast.LENGTH_SHORT).show();
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
        });

        socName=curUser.getSocName();

        db.collection("notice")
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
                                recyclerView.setLayoutManager(new LinearLayoutManager(NavDrawer.this));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.addItemDecoration(new DividerItemDecoration(NavDrawer.this, DividerItemDecoration.VERTICAL));

                                nAdapter = new NoticeAdapter(sList, NavDrawer.this,curUser);
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





        Log.d("error", "Error getting documents: ");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void animateFab() {
        if (isOpen)
        {
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab3.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isOpen=false;
        }
        else
        {
            fab.startAnimation(rotateBackward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab3.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isOpen=true;
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(NavDrawer.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(NavDrawer.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_verifyMembers) {
            Intent intent = new Intent(NavDrawer.this, VerifyUser.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(NavDrawer.this, GalleryHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);

        } else if (id == R.id.nav_emergency) {
            Intent intent = new Intent(NavDrawer.this, EmergencyHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);

        } else if (id == R.id.nav_complaints) {
            Intent intent = new Intent(NavDrawer.this, ComplainHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);

        }
        else if (id == R.id.nav_phoneDirectory) {
            Intent intent = new Intent(NavDrawer.this,PhoneDirectory.class);
            intent.putExtra("userDoc",curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_nearbyVendors) {
            Intent intent = new Intent(NavDrawer.this,InfoActivity.class);
            intent.putExtra("userDoc",curUser);
            startActivity(intent);
        }

        else if (id == R.id.nav_news) {
            Intent intent = new Intent(NavDrawer.this, NewsHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_schedule) {
            Intent intent = new Intent(NavDrawer.this, ScheduleInfo.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_maps) {
            Intent intent = new Intent(NavDrawer.this, MapsActivity.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_chat) {
            Intent intent = new Intent(NavDrawer.this, ChatHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_poll) {
            Intent intent = new Intent(NavDrawer.this, PollsHome.class);
            intent.putExtra("userDoc", curUser);
            startActivity(intent);
        }
        else if (id == R.id.nav_aboutUs) {
            Intent intent=new Intent(NavDrawer.this,AboutUs.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
    }


    private void uploadFile(Uri data) {
        uploadDialog.show();
        final String curTime = String.valueOf(System.currentTimeMillis());
        StorageReference sRef = mStorageReference.child( "NoticePDF/"+socName+"/"+ curTime + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        pdfUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                        Map<String, Object> data = new HashMap<>();
                        data.put("subject", subject);
                        data.put("pdf",pdfUrl);
                        data.put("date",date);
                        data.put("type","pdf");
                        data.put("socName",socName);
                        data.put("path",curTime);
                        db.collection("notice")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(final DocumentReference documentReference) {
                                        Toast.makeText(NavDrawer.this, "Notice upload successful!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(NavDrawer.this, NavDrawer.class);
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
                                        Toast.makeText(NavDrawer.this, "Registeration failed", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(NavDrawer.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            uploadDialog.dismiss();

                            imgUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                            Map<String, Object> data = new HashMap<>();
                            data.put("subject", subject);
                            data.put("image", imgUrl);
                            data.put("date", date);
                            data.put("type", "image");
                            data.put("socName", socName);
                            data.put("path", curTime);
                            db.collection("notice")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(final DocumentReference documentReference) {
                                            Toast.makeText(NavDrawer.this, "Notice upload successful!", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(NavDrawer.this, NavDrawer.class);
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
                                            Toast.makeText(NavDrawer.this, "Image Upload Failed!", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    uploadDialog.dismiss();
                                    Toast.makeText(NavDrawer.this, "Failed Uploading image", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
             else if (requestCode == PICK_PDF_REQUEST && data != null && data.getData() != null) {
                filePath = data.getData();
                uploadFile(filePath);

            }



        }
    }




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
