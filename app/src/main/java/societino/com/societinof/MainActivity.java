package societino.com.societinof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {
    private  boolean ifFound=false, isComplete=false;
    private CardView socreg;
    private SignInButton mGooglebutton;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private static final int RC_SIGN_IN=1;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String TAG = "HERE";
    private FirebaseFirestore db;
    private DocumentSnapshot userDoc=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGooglebutton = findViewById(R.id.googlebutton);
        //Register your soc
        socreg=(CardView) findViewById(R.id.regCard);
        socreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1=new Intent(MainActivity.this,SocietyReg.class);
                startActivity(i1);
            }
        });

        db=FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Signing in...");
        progressDialog.setTitle("Societino");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGooglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //Intent signInIntent = Auth.GOOGLE_SIGN_IN_API.
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //   Toast.makeText(MainActivity.this,"HERE1",Toast.LENGTH_SHORT).show();
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                // ...
            }
        }
    }

    private void updateUI(final FirebaseUser currentUser) {
        if(currentUser!=null)
        {
            //signInButton.setEnabled(false);
            Log.d("userId",currentUser.getUid());
            db.collection("Members")
                    .whereEqualTo("userId", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot document : task.getResult()) {
                                    userDoc=document;
                                    ifFound=true;
                                    Log.d("ifFound","true");
                                }

                                if (ifFound)
                                {
                                    isComplete = userDoc.getBoolean("isComplete");
                                    Log.d("ifComplete", String.valueOf(isComplete));
                                }


                                if(isComplete)
                                {

                                    Toast.makeText(MainActivity.this, "Signed IN!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this,Logout.class);
                                    intent.putExtra("accId",currentUser.getUid());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Signed IN!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this,UserReg.class);
                                    intent.putExtra("accId",currentUser.getUid());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }



                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d("2", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("3", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mGoogleSignInClient.revokeAccess();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("4", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Not Signed In!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}