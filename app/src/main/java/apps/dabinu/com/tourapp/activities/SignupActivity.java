package apps.dabinu.com.tourapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import apps.dabinu.com.tourapp.R;
import apps.dabinu.com.tourapp.utils.GmailSender;
import apps.dabinu.com.tourapp.utils.TourConstants;

public class SignupActivity extends AppCompatActivity {

    private EditText username, email, number, password;
    private String username_str, email_str, number_str, password_str;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int REQUEST_CODE_SIGN_IN = 0;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private FirebaseAuthException e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.usernameText);
        email = findViewById(R.id.emailText);
        number = findViewById(R.id.phoneNumberText);
        password = findViewById(R.id.passwordText);
        TextView signup = findViewById(R.id.signUpButton);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
            }
        });

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child(TourConstants.USERS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            //Google sign in complete and request for email and profile can be gotten.
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            email_str = account.getEmail();
            Toast.makeText(this, email_str, Toast.LENGTH_SHORT).show();
            username_str = account.getDisplayName();
            password_str = "123456";
            number_str = "null";
            register();

            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Toast.makeText(this, e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void verify(){
        username_str = username.getText().toString().trim();
        email_str = email.getText().toString().trim();
        number_str = number.getText().toString().trim();
        password_str = password.getText().toString().trim();

        if(username_str.equals("")){
            username.setError("Username cannot be empty");
        }
        else if(email_str.equals("")){
            email.setError("Email cannot be empty");
        }
        else if(number_str.equals("")){
            number.setError("Phone number cannot be empty");
        }
        else if(password_str.length() < 6){
            username.setError("Password must be at least 6 characters");
        }else
            register();
    }

    private void register(){
            mAuth.createUserWithEmailAndPassword(email_str, password_str).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mAuth.signInWithEmailAndPassword(email_str, password_str).
                                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            generateActivationCode();
                                        }else{
                                            e = (FirebaseAuthException)task.getException();
                                            assert e != null;
                                            String error = e.getMessage();
                                            Toast.makeText(SignupActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                e = (FirebaseAuthException)task.getException();
                                assert e != null;
                                String error = e.getMessage();
                                Toast.makeText(SignupActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    private void generateActivationCode(){
        Random random = new Random();
        int activation_code = 10000 + random.nextInt(89999);

        store_data(activation_code);
    }

    private void store_data(int activation_code){

        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference user_db = dbRef.child(user_id);
        user_db.child(TourConstants.USERNAME).setValue(username_str);
        user_db.child(TourConstants.EMAIL).setValue(email_str);
        user_db.child(TourConstants.PHONE_NUMBER).setValue(number_str);
        user_db.child(TourConstants.ACTIVATION_CODE).setValue(Integer.toString(activation_code));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(TourConstants.EMAIL, email_str);
        editor.putString(TourConstants.USERNAME, username_str);
        editor.putString(TourConstants.PHONE_NUMBER, number_str);
        editor.putString(TourConstants.PASSWORD, password_str);
        editor.putBoolean(TourConstants.LOGGED_IN, false);
        editor.putBoolean(TourConstants.IS_ACTIVATED, false);

        editor.apply();

        mail_activation_code(activation_code);
    }

    private void mail_activation_code(int activation_code){
        String subject = "Activation Key";
        String content = "Your activation key for MyTouris : "+activation_code+".";

        new GmailSender(email_str, subject, content);

        Toast.makeText(this, "An email has been sent you with you activation key to MyTour." +
                " Please check your email and enter the key in the box provided in the next screen.", Toast.LENGTH_LONG).show();

        startActivity(new Intent(SignupActivity.this, ActivationActivity.class));
        this.finish();
    }

}