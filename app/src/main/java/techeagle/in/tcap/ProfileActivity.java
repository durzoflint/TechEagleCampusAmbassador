package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    EditText name, number, dob, gender, address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Edit Profile");
        Intent intent = getIntent();
        String userTCAPID = intent.getStringExtra("tcapid");
        final String username = intent.getStringExtra("email");
        TextView email = findViewById(R.id.email);
        email.setText(username);
        TextView tcapid = findViewById(R.id.tcapid);
        tcapid.setText(userTCAPID);
        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.address);
        new FetchUserDetails().execute(username);
        Button update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = dob.getText().toString();
                if (isValidDate(date))
                    new UpdateUserDetails().execute(username, name.getText().toString(), number.getText().toString(),
                        dob.getText().toString(), gender.getText().toString(), address.getText().toString());
                else
                    Toast.makeText(ProfileActivity.this, "Invalid Date. Please use DD/MM/YYYY", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isValidDate(String date) {
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date d=dateFormat.parse(date);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    private class UpdateUserDetails extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ProfileActivity.this, "Please Wait!","Fetching Latest Data!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"updateuserdetails.php?username="+strings[0]+"&name="+strings[1]
                        +"&number="+strings[2]+"&dob="+strings[3]+"&gender="+strings[4]+"&address="+strings[5];
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
            if (webPage.equals("success"))
                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ProfileActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchUserDetails extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ProfileActivity.this, "Please Wait!","Fetching Latest Data!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"getuserdetails.php?username="+strings[0];
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
            int brI = webPage.indexOf("<br>");
            name.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            number.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            dob.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            gender.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            address.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
        }
    }
}