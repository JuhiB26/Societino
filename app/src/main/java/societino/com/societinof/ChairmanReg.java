package societino.com.societinof;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
public class ChairmanReg extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    FirebaseAuth mAuth;
    private TextInputEditText t1, t2, t3;
    private String downloadUrl;
    private RadioGroup r1;
    private Button b1, b2, b3;
    private boolean mVerificationInProgress = false, ifVerified = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Dialog dialog;
    private String name, number, email, socId;
    private FirebaseFirestore db;
    private byte[] barry;
    private boolean ifUploaded = false;
    private FirebaseStorage storage;
    private StorageReference imagesRef, storageRef;
    private ProgressDialog verifyDailog, regDialog,uploadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_chairman_reg);
        t1 = (TextInputEditText) findViewById(R.id.chairman_name);
        t2 = (TextInputEditText) findViewById(R.id.chairman_number);
        t3 = (TextInputEditText) findViewById(R.id.chairman_email);
        b1 = (Button) findViewById(R.id.chairman_regbtn);
        b2 = (Button) findViewById(R.id.uploadbtn);
        b3 = (Button) findViewById(R.id.verifybtn);

        r1 = (RadioGroup) findViewById(R.id.genderRadio);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Intent i1 = getIntent();
        String sec = i1.getStringExtra("secret");
        socId = i1.getStringExtra("socId");
        dialog = new Dialog(ChairmanReg.this);
        dialog.setContentView(R.layout.activity_verifyphn);
        dialog.setTitle("Verification");
        db = FirebaseFirestore.getInstance();
        //Verify dialog
        verifyDailog = new ProgressDialog(ChairmanReg.this);
        verifyDailog.setMessage("Verifying...");
        verifyDailog.setTitle("Societino");

        //Upload Dialog
        uploadDialog = new ProgressDialog(ChairmanReg.this);
        uploadDialog.setMessage("Uploading...");
        uploadDialog.setTitle("Societino");

        //Reg Dialog
        regDialog = new ProgressDialog(ChairmanReg.this);
        regDialog.setMessage("Registering...");
        regDialog.setTitle("Societino");


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = t1.getText().toString();
                email = t3.getText().toString();
                if (name.equals("") || number.equals("") || email.equals("") || r1.getCheckedRadioButtonId() == -1 || ifUploaded == false || ifVerified == false) {
                    Toast.makeText(ChairmanReg.this, "Fill in all the blanks.", Toast.LENGTH_SHORT).show();
                } else {
                    regDialog.show();

                    int selId = r1.getCheckedRadioButtonId();
                    RadioButton rButton = findViewById(selId);
                    String gender = rButton.getText().toString();
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("number", number);
                    user.put("socId", socId);
                    user.put("isVerified", false);
                    user.put("gender", gender);
                    user.put("imageUrl", downloadUrl);
                    db.collection("chairman")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(final DocumentReference documentReference) {
                                    Toast.makeText(ChairmanReg.this, "Registeration successful", Toast.LENGTH_SHORT).show();
                                    regDialog.dismiss();
                                    Intent intent = new Intent(ChairmanReg.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChairmanReg.this, "Registeration failed", Toast.LENGTH_SHORT).show();
                                    regDialog.dismiss();
                                }
                            });
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
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


        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = t2.getText().toString();
                if (number.trim().length() != 10) {
                    Toast.makeText(ChairmanReg.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    startPhoneNumberVerification(number);
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                }
            }
        });


        dialog.findViewById(R.id.verifybtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDailog.show();
                TextInputEditText edtcode = dialog.findViewById(R.id.code);
                String code = edtcode.getText().toString().trim();
                if (!code.equals("")) {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("verify 1", "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("verify error 1", "onVerificationFailed");
                Log.e("verification failed",e.toString());
                verifyDailog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(ChairmanReg.this, "Invalid Request.", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(ChairmanReg.this, "Too many requests.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                Log.d("code sent", "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

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
                    imagesRef = storageRef.child("Chariman Address Proof/" + socId);
                    final UploadTask uploadTask = imagesRef.putBytes(barry);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ChairmanReg.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                            uploadDialog.dismiss();
                            b2.setText("Upload Successful");
                            b2.setEnabled(false);
                            Toast.makeText(ChairmanReg.this, "Upload Succesful!", Toast.LENGTH_SHORT).show();
                            ifUploaded = true;
                           /* t4.setText("Upload Successful! Click here.");
                            t4.setTextColor(Color.rgb(51, 51, 255));
                            t4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = downloadUrl;
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });*/

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    uploadDialog.dismiss();
                                    Toast.makeText(ChairmanReg.this, "Failed Uploading image", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Phone Verification functions
    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        String pno = "+91" + phoneNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                pno,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            ifVerified = true;
                            FirebaseUser cuser = task.getResult().getUser();
                            cuser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("userDeleted", "TRUE");
                                }
                            });
                            verifyDailog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(ChairmanReg.this, "Verified successfully", Toast.LENGTH_SHORT).show();
                            b3.setEnabled(false);
                            t2.setEnabled(false);
                            b3.setText("Verified");
                        } else {
                            verifyDailog.dismiss();
                            Toast.makeText(ChairmanReg.this, "Not Verified", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
}