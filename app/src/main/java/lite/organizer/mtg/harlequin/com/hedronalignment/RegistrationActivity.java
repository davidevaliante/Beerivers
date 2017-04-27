package lite.organizer.mtg.harlequin.com.hedronalignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegistrationActivity extends AppCompatActivity {

    private LoginButton facebookLoginButton;
    private Button registrationButton,loginButton;
    private EditText mailField,passwordField;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private MaterialRippleLayout accessRipple, registerRipple;
    private CallbackManager callbackManager;
    private String TAG = "**FACEBOOK_LOGIN_INFO: ";
    private DatabaseReference mDatabaseReference;
    private LoginResult facebookLoginResult ;
    private String userName ="";
    private String userSurname = "";
    private String userProfile = "";
    private String userLink = "";
    private String userGender = "";
    private ValueEventListener userIdListener;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FacebookSdk.sdkInitialize(getApplicationContext());

        /* HASH KEY DEVELOPMENT
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.finder.harlequinapp.valiante.harlequin",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("******FACEBOOOOK***","*********************"+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Toast.makeText(RegistrationActivity.this,""+Base64.encodeToString(md.digest(), Base64.DEFAULT),Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        */

        accessRipple = (MaterialRippleLayout)findViewById(R.id.rippleAccedi);
        registerRipple = (MaterialRippleLayout)findViewById(R.id.rippleRegistrati);
        registrationButton = (Button)findViewById(R.id.registration);
        loginButton = (Button)findViewById(R.id.login);
        mailField = (EditText)findViewById(R.id.mail);
        passwordField = (EditText)findViewById(R.id.password);

        //registrazione con facebook
        facebookLoginButton = (LoginButton)findViewById(R.id.login_button);
        facebookLoginButton.setReadPermissions(
                "email", "public_profile","user_birthday","user_location");
        callbackManager = CallbackManager.Factory.create();

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(RegistrationActivity.this,"Eseguendo l'accesso con Facebook",Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken(),loginResult);

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });


        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {



                        userIdListener = mDatabaseReference.addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(user.getUid())){
                                Intent intent = new Intent(RegistrationActivity.this,UserPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }else {
                                if (facebookLoginResult != null) {

                                    getUserFromFacebook(facebookLoginResult, user);
                                    Intent intentFacebook = new Intent(RegistrationActivity.this, UserPage.class);
                                    intentFacebook.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intentFacebook);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                        });



                } else {
                    // User is signed out
                    Log.d("///////", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };





        accessRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isAbleToLogin()){
                    mProgressDialog.setMessage("Login...");
                    mProgressDialog.show();
                    String email = mailField.getText().toString().trim();
                    String password = passwordField.getText().toString().trim();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                mProgressDialog.dismiss();
                                Intent toUserPage = new Intent (RegistrationActivity.this,UserPage.class);
                                startActivity(toUserPage);
                            }
                            if(!task.isSuccessful()){
                                mProgressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this, "Login fallito",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(RegistrationActivity.this,"Riempi i campi necessari",Toast.LENGTH_SHORT).show();
                }

            }
        });


        registerRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, RegistrationData.class);
                startActivity(intent);
            }
        });

    }//FINE DI ONCREATE

    private boolean isAbleToLogin(){
        boolean canLogin;
        if(mailField.getText().toString().isEmpty() || passwordField.getText().toString().isEmpty()){
            canLogin = false;
        }
        else{
            canLogin = true;
        }
        return canLogin;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void getUserFromFacebook (final LoginResult loginResult, final FirebaseUser user){
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,gender,link");
        final User newUser= new User();

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("***LoginResponse :",response.toString());

                        try {

                            userGender = userGender + response.getJSONObject().getString("gender");
                            userName= userName + response.getJSONObject().getString("first_name");
                            userSurname = userSurname + response.getJSONObject().getString("last_name");


                            User facebookUser = new User (userName,userSurname,"null","null",genderFixer(userGender),"non specificato");
                            mDatabaseReference.child(user.getUid()).setValue(facebookUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(RegistrationActivity.this, "Registrazione completata",Toast.LENGTH_SHORT).show();
                                }
                            });

                            Log.i("****Login"+ "FirstName", userName);
                            Log.i("****Login" + "LastName", userSurname);
                            Log.i("****Login" + "Gender", userGender);
                            Log.i("***Login"+"ProfilePic",userProfile );



                        }catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
        );
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void handleFacebookAccessToken(AccessToken token, final LoginResult loginResult) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]
        //TODO creare l'utente
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "*********signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (task.isSuccessful()){

                            facebookLoginResult = loginResult;


                        }

                    }
                });
    }
    // [END auth_with_facebook]


    public String genderFixer (String gender){
        String fixedGender ="";
        if (gender.equalsIgnoreCase("female")){
            fixedGender = fixedGender+"Donna";
        }
        if (gender.equals("male")){
            fixedGender = fixedGender+"Uomo";
        }
        if (gender.isEmpty()){
            fixedGender = fixedGender+"Non specificato";
        }
        return fixedGender;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        mDatabaseReference.removeEventListener(userIdListener);
        super.onDestroy();
    }
}
