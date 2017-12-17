package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    boolean savelogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final CheckBox rememberpasswordbox=(CheckBox)findViewById(R.id.rememberme);
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor loginPrefsEditor = loginPreferences.edit();
        savelogin = loginPreferences.getBoolean("savelogin", false);
        if(savelogin)
        {
            username.setText(loginPreferences.getString("id",""));
            password.setText(loginPreferences.getString("password",""));
            rememberpasswordbox.setChecked(true);
        }
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = username.getText().toString();
                String pass = password.getText().toString();
                if (rememberpasswordbox.isChecked()) {
                    loginPrefsEditor.putBoolean("savelogin", true);
                    loginPrefsEditor.putString("id", id);
                    loginPrefsEditor.putString("password", pass);
                    loginPrefsEditor.apply();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
                new Login().execute(id, pass);
            }
        });
    }
    private class Login extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(LoginActivity.this, "Please Wait!","Logging In!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"login.php?username="+strings[0]+"&password="+strings[1];
                myURL = myURL.replaceAll(" ", "%20");
                url = new URL(myURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String data;
                while ((data=br.readLine()) != null)
                    webPage=webPage+data;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(webPage.contains("login successful<br>"))
            {
                int brI = webPage.indexOf("<br>");
                webPage = webPage.substring(brI+4);
                brI = webPage.indexOf("<br>");
                String name = webPage.substring(0, brI);
                webPage = webPage.substring(brI+4);
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.putExtra("Name",name);
                startActivity(i);
            }
            else
                Toast.makeText(LoginActivity.this, "Username or Password incorrect.", Toast.LENGTH_LONG).show();
        }
    }
}