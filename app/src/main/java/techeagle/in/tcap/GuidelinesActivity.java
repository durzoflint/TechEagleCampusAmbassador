package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GuidelinesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidelines);
        setTitle("Guidelines");
        new FetchGuidelines().execute();
    }

    private class FetchGuidelines extends AsyncTask<Void,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(GuidelinesActivity.this, "Please Wait!","Fetching Guidelines!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchguidelines.php";
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
            while (webPage.contains("</p>"))
            {
                LinearLayout data = findViewById(R.id.data);
                Context context = GuidelinesActivity.this;
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setPadding(16,16,16,16);
                TextView textView = new TextView(context);
                int h1I = webPage.indexOf("</p>");
                textView.setText(Html.fromHtml(webPage.substring(0,h1I)));
                textView.setPadding(32,32,32,-48);
                textView.setBackgroundColor(Color.WHITE);
                webPage = webPage.substring(h1I + 4);
                linearLayout.addView(textView);
                data.addView(linearLayout);
            }
        }
    }
}