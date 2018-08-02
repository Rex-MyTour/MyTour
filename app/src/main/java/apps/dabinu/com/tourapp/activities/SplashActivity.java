package apps.dabinu.com.tourapp.activities;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import apps.dabinu.com.tourapp.R;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        overridePendingTransition(0, 0);

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        new SplashAsyntask().execute();

    }


    private class SplashAsyntask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String result){
            switch(result){
                case "noNetwork":
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Try again", new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                                }
                            })
                            .setDuration(1200000)
                            .show();
                    break;

                case "notSignedIn":
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), HomeMapActivity.class).putExtra("AUTH_STATE", "null"));
                    break;

                case "signedIn":
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), HomeMapActivity.class).putExtra("AUTH_STATE", "taslim"));
                    break;
            }
        }

        @Override
        protected String doInBackground(Void... voids){

            if(!isNetworkAvailable()){
                return "noNetwork";
            }
            else{
                return "signedIn";
            }
        }
    }


    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}