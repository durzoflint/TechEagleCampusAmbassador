package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationActivity extends AppCompatActivity {
    String username = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        new FetchNotifications().execute(username);
        setTitle("Notifications");
    }

    private class FetchNotifications extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(NotificationActivity.this, "Please Wait!","Fetching Notifications!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"getnotifications.php?username="+strings[0];
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
            while (webPage.contains("<br>"))
            {
                int brI = webPage.indexOf("<br>");
                String notif = webPage.substring(0, brI);
                webPage = webPage.substring(brI + 4);
                LinearLayout data = findViewById(R.id.data);
                Context context = NotificationActivity.this;
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
                TextView textView = new TextView(context);
                textView.setPadding(32,32,32,32);
                textView.setTextSize(18);
                textView.setText(notif);
                linearLayout.addView(textView);
                data.addView(linearLayout);
            }
            progressDialog.dismiss();
        }
    }
}