package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FeedbackActivity extends AppCompatActivity {
    Intent intent;
    String taskId = "", myprogress = "", myseekbar = "", completedlabel = "", mybuttons = "", direct="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle("Feedback");
        intent = getIntent();
        direct = intent.getStringExtra("direct");
        taskId = intent.getStringExtra("taskid");
        myprogress = intent.getStringExtra("progress");
        myseekbar = intent.getStringExtra("seekbar");
        completedlabel = intent.getStringExtra("completedLabel");
        mybuttons = intent.getStringExtra("buttons");
        final String username = intent.getStringExtra("username");
        String taskName = intent.getStringExtra("taskname");
        String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        TextView taskname = findViewById(R.id.taskname);
        taskname.setText("Task Name : " + taskName);
        TextView nameofuser = findViewById(R.id.nameofuser);
        nameofuser.setText("~" + displayName);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView feedback = findViewById(R.id.feedback);
                String feed = feedback.getText().toString().trim();
                if (feed.isEmpty())
                    Toast.makeText(FeedbackActivity.this, "Empty Feedback not Allowed.", Toast.LENGTH_SHORT).show();
                else
                    new SubmitFeedback().execute(username, taskId, feed);
            }
        });
    }

    private class SubmitFeedback extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(FeedbackActivity.this, "Please Wait!","Submitting Feedback!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"submitfeedback.php?username="+strings[0]+"&taskid="+strings[1]+"&feedback="+ Uri.encode(strings[2]);
                myURL = myURL.replaceAll("\'", "%27");
                myURL = myURL.replaceAll("\'", "%22");
                myURL = myURL.replaceAll("\\(", "%28");
                myURL = myURL.replaceAll("\\)", "%29");
                myURL = myURL.replaceAll("\\{", "%7B");
                myURL = myURL.replaceAll("\\}", "%7B");
                myURL = myURL.replaceAll("\\]", "%22");
                myURL = myURL.replaceAll("\\[", "%22");
                Log.d("Abhinav", myURL);
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
            if(webPage.equals("success"))
            {
                Toast.makeText(FeedbackActivity.this, "Your response was submitted successfully.", Toast.LENGTH_SHORT).show();
                Intent output = new Intent();
                output.putExtra("completedFeedback", "yes");
                output.putExtra("taskid", taskId);
                output.putExtra("progress", myprogress);
                output.putExtra("seekbar", myseekbar);
                output.putExtra("completedLabel", completedlabel);
                output.putExtra("buttons", mybuttons);
                setResult(RESULT_OK, output);
                finish();
            }
            else
                Toast.makeText(FeedbackActivity.this, "Some error occured!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        try
        {
            if(direct.equals("yes"))
                super.onBackPressed();
        }
        catch (Exception e) {
            Toast.makeText(this, "Feedback required!", Toast.LENGTH_SHORT).show();
        }
    }
}