package lite.organizer.mtg.harlequin.com.hedronalignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationData extends AppCompatActivity {

    private Button completeButton;
    private EditText userName,userSurname,userMail,userPassword;
    private DatabaseReference myReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_data);

        completeButton = (Button)findViewById(R.id.completeRegistration);
        userName = (EditText)findViewById(R.id.userName);
        userSurname = (EditText)findViewById(R.id.userSurname);
        userMail = (EditText)findViewById(R.id.userMail);
        userPassword = (EditText)findViewById(R.id.userPass);

        mProgressDialog = new ProgressDialog(this);

        myReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("///////", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("///////", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkIfRegistrable()){
                    Toast.makeText(RegistrationData.this,"Riempi i campi necessari",Toast.LENGTH_LONG).show();
                }
                else{
                    mProgressDialog.setMessage("Effettuando la registrazione");
                    mProgressDialog.show();

                    final String name = userName.getText().toString().trim();
                    final String surname = userSurname.getText().toString().trim();
                    final String usermail = userMail.getText().toString().trim();
                    final String userpass = userPassword.getText().toString().trim();


                    mAuth.createUserWithEmailAndPassword(usermail, userpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                User newUser = new User(name, surname, usermail, userpass,"non specificato","non specificato");
                                myReference.child(userId).setValue(newUser);
                                mProgressDialog.dismiss();
                                Toast.makeText(RegistrationData.this,"Registrazione effetuata",Toast.LENGTH_SHORT).show();
                                Intent toUserPage = new Intent(RegistrationData.this,UserPage.class);
                                startActivity(toUserPage);

                            }
                            else{
                                mProgressDialog.dismiss();
                                Toast.makeText(RegistrationData.this,"Registrazione fallita",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });
    }

    private boolean checkIfRegistrable (){
        boolean isRegistrable ;
        if(userName.getText().toString().isEmpty() || userSurname.getText().toString().isEmpty()
                || userPassword.getText().toString().isEmpty() || userPassword.getText().toString().isEmpty()){
            isRegistrable = false;
        }
        else{
            isRegistrable = true;
        }
        return isRegistrable;
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
}
