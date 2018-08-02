package apps.dabinu.com.tourapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import apps.dabinu.com.tourapp.R;
import apps.dabinu.com.tourapp.utils.TourConstants;


public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    TextView login, signup;
    private FirebaseAuth mAuth;
    private FirebaseAuthException e;
    private DatabaseReference dbRef;
    private String user_id, email_text, password_text;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getSupportActionBar().hide();
        overridePendingTransition(0, 0);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child(TourConstants.USERS);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                email_text = email.getText().toString().trim();
                password_text = password.getText().toString().trim();

                if(password.getText().toString().length() < 6){
                    password.setError("Invalid password");
                }
                else if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())){
                    email.setError("Invalid email");
                }
                else{
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                user_id = mAuth.getCurrentUser().getUid();
                                fetchData();
                            } else{
                                e = (FirebaseAuthException)task.getException();
                                assert e != null;
                                String error = e.getMessage();
                                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                LoginActivity.this.finish();
            }
        });

    }

    private void fetchData(){
        dbRef = dbRef.child(user_id);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                storeData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void storeData(DataSnapshot dataSnapshot){
        String username = (String)dataSnapshot.child(TourConstants.USERNAME).getValue();
        String mobile = (String)dataSnapshot.child(TourConstants.PHONE_NUMBER).getValue();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(TourConstants.EMAIL, email_text);
        editor.putString(TourConstants.USERNAME, username);
        editor.putString(TourConstants.PHONE_NUMBER, mobile);
        editor.putString(TourConstants.PASSWORD, password_text);
        editor.putBoolean(TourConstants.LOGGED_IN, true);
        editor.putBoolean(TourConstants.IS_ACTIVATED, true);

        editor.apply();

        startActivity(new Intent(LoginActivity.this, HomeMapActivity.class));
        LoginActivity.this.finish();
    }
}
