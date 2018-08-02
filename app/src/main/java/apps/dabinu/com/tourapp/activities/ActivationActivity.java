package apps.dabinu.com.tourapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import apps.dabinu.com.tourapp.R;
import apps.dabinu.com.tourapp.utils.TourConstants;

public class ActivationActivity extends AppCompatActivity {

    private EditText key_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        key_text = findViewById(R.id.activation_key);
    }

    public void activate(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String user_id = mAuth.getCurrentUser().getUid();
        final String key = key_text.getText().toString().trim();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(TourConstants.USERS).
                child(user_id).child(TourConstants.ACTIVATION_CODE);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String activation_code = (String)dataSnapshot.getValue();
                if(!TextUtils.isEmpty(key)){
                    if (key.equalsIgnoreCase(activation_code)){
                        Toast.makeText(ActivationActivity.this, "You have been activated!!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ActivationActivity.this, HomeMapActivity.class));
                        ActivationActivity.this.finish();
                    }else Toast.makeText(ActivationActivity.this, "Incorrect Key!!!", Toast.LENGTH_SHORT).show();
                }else Toast.makeText(ActivationActivity.this, "Enter Key!!!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



    }
}