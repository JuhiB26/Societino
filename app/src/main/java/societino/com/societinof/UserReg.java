package societino.com.societinof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UserReg extends AppCompatActivity {
    //vehicle information
    int minteger = 0;
    int ninteger = 0;
    TextView TextView1, TextView2;
    private Button b1, b2, b3, b4;//+ and - button(increae and decrease)
    private Button buttonAddress;
    //For image upload
    private String downloadUrl;
    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog, verifyDailog, uploadDialog,vDialog;
    private boolean ifUploaded = false;
    //Admin Radio button
    private RadioButton admin;
    private TextInputEditText sec;//dialog_login
    private Button submit;//dialog_login
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    //phone number verification
    private FirebaseAuth mAuth;
    private TextInputEditText otp;
    private String number, accId;
    private TextInputEditText contact;
    private Button ButtonVerify;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean mVerificationInProgress = false, ifVerified = false;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private Spinner spin;
    //Details:
    private TextInputEditText ename, eaddress, esuc, enofm;
    private Button Register, verifySoc;
    private String name, address, suc, nofm, spi, eint1, eint2, gender;
    private RadioGroup r1;
    private TextView int1, int2;
    private FirebaseFirestore db;

    private DocumentSnapshot socDoc = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reg);
        b1 = (Button) findViewById(R.id.decrease);
        b2 = (Button) findViewById(R.id.increase);
        b3 = (Button) findViewById(R.id.increase1);
        b4 = (Button) findViewById(R.id.decrease1);
        verifySoc = findViewById(R.id.buttonverify1);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        spin = (Spinner) findViewById(R.id.spinprofession);
        Intent gotIntnet = getIntent();
        accId = gotIntnet.getStringExtra("accId");
        //For image upload
        buttonAddress = (Button) findViewById(R.id.buttonaddress);
        mStorage = FirebaseStorage.getInstance().getReference();
        buttonAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
        //Admin details
        final RadioButton owner = (RadioButton) findViewById(R.id.rb_owner);
        final  RadioButton tenant = findViewById(R.id.rb_tenant);
        admin = (RadioButton) findViewById(R.id.rb_admin);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(UserReg.this);
                View v = getLayoutInflater().inflate(R.layout.dialog_login, null);
                sec = (TextInputEditText) v.findViewById(R.id.ed_sec);
                final Button submit = (Button) v.findViewById(R.id.buttonsubmit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sec.getText().toString().equals(socDoc.getString("secret"))) {
                            Toast.makeText(UserReg.this, "Security key entered.", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            owner.setEnabled(false);
                            tenant.setEnabled(false);

                        } else {
                            owner.setChecked(true);
                            admin.setChecked(false);
                            dialog.dismiss();
                            Toast.makeText(UserReg.this, "Please enter correct security key.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setView(v);
                dialog = builder.create();
                dialog.show();
            }
        });
        //phone verification
        verifyDailog = new ProgressDialog(UserReg.this);
        verifyDailog.setMessage("Verifying...");
        verifyDailog.setTitle("Societino");
        //Register
        progressDialog = new ProgressDialog(UserReg.this);
        progressDialog.setMessage("Registering...");
        progressDialog.setTitle("Societino");

        //Image Upload
        uploadDialog = new ProgressDialog(UserReg.this);
        uploadDialog.setMessage("Uploading...");
        uploadDialog.setTitle("Societino");
        //soc unique code verification
        vDialog = new ProgressDialog(UserReg.this);
        vDialog.setMessage("Verifying...");
        vDialog.setTitle("Societino");
        ButtonVerify = (Button) findViewById(R.id.buttonverify);
        contact = (TextInputEditText) findViewById(R.id.edittext_contact);
        builder = new AlertDialog.Builder(UserReg.this);
        final View v = getLayoutInflater().inflate(R.layout.dialog_otp, null);
        otp = (TextInputEditText) v.findViewById(R.id.ed_otp);
        final Button ok = (Button) v.findViewById(R.id.buttonok);
        ButtonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = contact.getText().toString();
                if (number.trim().length() != 10) {
                    Toast.makeText(UserReg.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    startPhoneNumberVerification(number);
                    builder.setView(v);
                    dialog = builder.create();
                    dialog.show();
                }
            }
        });
        //dialog_otp button
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDailog.show();
                String code = otp.getText().toString().trim();
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
                verifyDailog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(UserReg.this, "Invalid Request.", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(UserReg.this, "Too many requests.", Toast.LENGTH_SHORT).show();
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
        //Checking details
        int1 = (TextView) findViewById(R.id.integer_number);
        int2 = (TextView) findViewById(R.id.integer_number1);

        ename = (TextInputEditText) findViewById(R.id.edittext_name);
        eaddress = (TextInputEditText) findViewById(R.id.edittext_address);
        esuc = (TextInputEditText) findViewById(R.id.edittext_socuc);
        enofm = (TextInputEditText) findViewById(R.id.edittext_nofm);
        r1 = (RadioGroup) findViewById(R.id.genderRadio);
        r1.setEnabled(false);
        Register = (Button) findViewById(R.id.buttonreg);
        Register.setEnabled(false);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eint1 = int1.getText().toString();
                eint2 = int2.getText().toString();
                name = ename.getText().toString();
                address = eaddress.getText().toString();
                suc = esuc.getText().toString();
                nofm = enofm.getText().toString();
                spi = spin.getSelectedItem().toString();
                if (name.equals("") || number.equals("") || address.equals("") || number.equals("") || suc.equals("") || nofm.equals("") || r1.getCheckedRadioButtonId() == -1 || ifUploaded == false || ifVerified == false || spin.getSelectedItemPosition() < 1) {
                    Toast.makeText(UserReg.this, "Fill in all the blanks.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    int selId = r1.getCheckedRadioButtonId();
                    RadioButton rButton = (RadioButton) findViewById(selId);
                    gender = rButton.getText().toString();
                    // Toast.makeText(UserDetails.this,"Your request has been sent",Toast.LENGTH_SHORT).show();
                    Map<String, Object> user = new HashMap<>();
                    user.put("userDetail", gender);
                    if(gender.equals("Admin"))
                    {
                        user.put("isVerified", true);
                    }
                    else
                        {
                            user.put("isVerified", false);
                        }

                    user.put("name", name);
                    //user.put("Name", name);
                    user.put("address", address);
                    user.put("societyUniqueCode", suc);
                    user.put("contact", number);
                    user.put("profession", spi);
                    user.put("numberOfFamilyMembers", nofm);
                    user.put("numberOf2WheelerVehicle", eint1);
                    user.put("numberOf4WheelerVehicle", eint2);
                    user.put("imageUrl", downloadUrl);
                    user.put("userId", accId);
                    user.put("isComplete", true);
                    db.collection("Members")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(final DocumentReference documentReference) {
                                    Toast.makeText(UserReg.this, "Your request has been sent", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(UserReg.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UserReg.this, "Registeration failed", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            }

        });

        //verifySoc
        verifySoc.setOnClickListener(new View.OnClickListener() {
            boolean ifFound = false;

            @Override
            public void onClick(View view) {
                String uname = esuc.getText().toString();
                Log.d("socid",uname);
                if (!uname.isEmpty()) {
                    vDialog.show();
                    db.collection("society")
                            .whereEqualTo("username", uname)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            socDoc = document;
                                            ifFound = true;
                                            Log.d("ifFound", "true");
                                        }
                                        if(ifFound)
                                        {
                                            vDialog.dismiss();
                                            verifySoc.setText("Verified");
                                            verifySoc.setEnabled(false);
                                            esuc.setEnabled(false);
                                            r1.setEnabled(true);
                                            Register.setEnabled(true);
                                        }
                                        else
                                        {
                                            vDialog.dismiss();
                                            Toast.makeText(UserReg.this, "Incorrect Society Unique Code", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    else {
                                        vDialog.dismiss();
                                        Toast.makeText(UserReg.this, "Error Occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(UserReg.this, "Fill in all the blanks", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //For image upload
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadDialog.show();

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("Address proof of users").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                    uploadDialog.dismiss();
                    ifUploaded = true;
                    Toast.makeText(UserReg.this, "Upload Done.", Toast.LENGTH_LONG).show();
                    buttonAddress.setEnabled(false);
                    buttonAddress.setText("Uploaded");
                }
            });

        }
    }

    //Mobile number verification functions.
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

                            FirebaseUser cuser = task.getResult().getUser();
                            cuser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("userDeleted", "TRUE");
                                }
                            });
                            verifyDailog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(UserReg.this, "Verified successfully", Toast.LENGTH_SHORT).show();
                            ButtonVerify.setEnabled(false);
                            contact.setEnabled(false);
                            ButtonVerify.setText("Verified");
                            ifVerified = true;

                        } else {
                            verifyDailog.dismiss();
                            Toast.makeText(UserReg.this, "Not Verified", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    //Vehicle information(2-wheeler)
    public void increaseInteger(View view) {
        minteger = minteger + 1;
        display(minteger);

    }

    public void decreaseInteger(View view) {

        minteger = minteger - 1;
        display(minteger);

    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number);
        displayInteger.setText("" + number);
    }

    //Vehicle info 4-wheeler
    public void increaseIntegerr(View view) {
        ninteger = ninteger + 1;
        display1(ninteger);

    }

    public void decreaseIntegerr(View view) {

        ninteger = ninteger - 1;
        display1(ninteger);

    }

    private void display1(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.integer_number1);
        displayInteger.setText("" + number);
    }
}
