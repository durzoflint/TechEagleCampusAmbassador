package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {
    EditText firstname, lastname, number, dob, address;
    Spinner gender, states, interests;
    String statesArray[] = {"Andhra Pradesh (AP)","Arunachal Pradesh (AR)","Assam (AS)","Bihar (BR)",
            "Chhattisgarh (CG)","Goa (GA)","Gujarat (GJ)","Haryana (HR)","Himachal Pradesh (HP)",
            "Jammu and Kashmir (JK)","Jharkhand (JH)","Karnataka (KA)","Kerala (KL)","Madhya Pradesh (MP)",
            "Maharashtra (MH)","Manipur (MN)","Meghalaya (ML)","Mizoram (MZ)","Nagaland (NL)","Odisha(OR)",
            "Punjab (PB)","Rajasthan (RJ)","Sikkim (SK)","Tamil Nadu (TN)","Telangana (TS)","Tripura (TR)",
            "Uttar Pradesh (UP)","Uttarakhand (UK)","West Bengal (WB)","Andaman and Nicobar Islands(AN)",
            "Chandigarh (CH)","Dadra and Nagar Haveli (DN)","Daman and Diu (DD)",
            "National Capital Territory of Delhi (DL)","Lakshadweep (LD)","Pondicherry (PY)"};
    String interestsArray[] = {"Full stack website developer", "Full Stack App developer", "Software Development",
            "Blog Writer-technical", "Blog Writer-Non-technical", "Graphics/Poster/Creative Designer",
            "VFX/Video Maker & Editor", "Photography/Video Graphy", "Digital Marketing/FB Paid Ads/ Adwords"};
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
        tcapid.setText("TechEagle ID : " + userTCAPID);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        number = findViewById(R.id.number);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.address);
        states = findViewById(R.id.states);
        interests = findViewById(R.id.interests);
        new FetchUserDetails().execute(username);
        Button update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (formIsValid())
                    new UpdateUserDetails().execute(username, firstname.getText().toString().trim(),
                            lastname.getText().toString(), number.getText().toString(), dob.getText().toString(),
                            gender.getSelectedItem().toString(), address.getText().toString(),
                            states.getSelectedItem().toString(), interests.getSelectedItem().toString());
            }
        });
    }

    private boolean formIsValid() {
        if (firstname.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "First Name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (lastname.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Last Name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (number.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Number cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidDate())
        {
            Toast.makeText(this, "Invalid Date. Please use DD/MM/YYYY format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (address.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isValidDate() {
        String date = dob.getText().toString();
        try
        {
            String day = date.substring(0,2);
            if (day.length()!=2)
                throw new Exception();
            String month = date.substring(3,5);
            if (month.length()!=2)
                throw new Exception();
            String year = date.substring(6);
            if (year.length()!=4)
                throw new Exception();
            int dd = Integer.parseInt(day);
            if (dd < 1 || dd > 31)
                throw new Exception();
            int mm = Integer.parseInt(month);
            if (mm < 1 || mm > 12)
                throw new Exception();
            int yyyy = Integer.parseInt(year);
            if (yyyy < 1990 || yyyy > 2100)
                throw new Exception();
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    private class UpdateUserDetails extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(ProfileActivity.this, "Please Wait!","Updating!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"updateuserdetails.php?username="+strings[0]+"&firstname="+strings[1]
                        +"&lastname="+strings[2]+"&number="+strings[3]+"&dob="+strings[4]+"&gender="+strings[5]
                        +"&address="+strings[6]+"&state="+strings[7]+"&interests="+strings[8];
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
            {
                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully. Restarting App.", Toast.LENGTH_SHORT).show();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            else
            {
                Toast.makeText(ProfileActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchUserDetails extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
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
            firstname.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            lastname.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            number.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            dob.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            String sex = webPage.substring(0, brI);
            if (sex.equals("Male"))
                gender.setSelection(0);
            else if (sex.equals("Female"))
                gender.setSelection(1);
            else
                gender.setSelection(2);
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            address.setText(webPage.substring(0, brI));
            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            String state = webPage.substring(0, brI);
            int i;
            for(i=0;i<statesArray.length;i++)
                if (state.equals(statesArray[i]))
                {
                    states.setSelection(i);
                    break;
                }

            webPage = webPage.substring(brI+4);
            brI = webPage.indexOf("<br>");
            String interest = webPage.substring(0, brI);
            for(i=0;i<interestsArray.length;i++)
                if (interest.equals(interestsArray[i]))
                {
                    interests.setSelection(i);
                    break;
                }
            webPage = webPage.substring(brI+4);
        }
    }

    @Override
    public void onBackPressed() {
        if (formIsValid())
            super.onBackPressed();
    }
}