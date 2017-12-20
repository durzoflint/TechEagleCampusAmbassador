package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        new FetchLeaderboard().execute();
        setTitle("Leaderboard");
    }
    private class FetchLeaderboard extends AsyncTask<Void, Void, Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(LeaderboardActivity.this, "Please Wait!","Fetching Leaderboard In!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"leaderboard.php";
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
            if(webPage.contains("<br>"))
            {
                int i = 0;
                while (webPage.contains("<br>"))
                {
                    int index = webPage.indexOf("<br>");
                    if (i == 0)
                    {
                        TextView first = findViewById(R.id.first);
                        first.setText("1st\n" + webPage.substring(0, index));
                    }
                    else if (i == 1)
                    {
                        TextView second = findViewById(R.id.second);
                        second.setText("2nd\n" + webPage.substring(0, index));
                    }
                    else if (i == 2)
                    {
                        TextView third = findViewById(R.id.third);
                        third.setText("3rd\n" + webPage.substring(0, index));
                    }
                    else
                    {
                        LinearLayout data = findViewById(R.id.data);
                        Context context = LeaderboardActivity.this;
                        LinearLayout.LayoutParams layoutParamsOne = new LinearLayout.LayoutParams(LinearLayout
                                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                        LinearLayout.LayoutParams layoutParamsTen = new LinearLayout.LayoutParams(LinearLayout
                                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 10f);
                        LinearLayout linearLayout = new LinearLayout(context);
                        linearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
                        linearLayout.setLayoutParams(layoutParamsOne);
                        TextView tv1=new TextView(context);
                        tv1.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
                        tv1.setPadding(8,0,0,0);
                        tv1.setLayoutParams(layoutParamsTen);
                        tv1.setGravity(Gravity.CENTER);
                        tv1.setText("" + (i + 1));
                        linearLayout.addView(tv1);
                        TextView tv2=new TextView(context);
                        tv2.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
                        tv2.setGravity(Gravity.CENTER);
                        tv2.setPadding(8,0,0,0);
                        tv2.setLayoutParams(layoutParamsOne);
                        tv2.setText(webPage.substring(0, index));
                        linearLayout.addView(tv2);
                        data.addView(linearLayout);
                    }
                    webPage = webPage.substring(index + 4);
                    i++;
                }
            }
            else
                Toast.makeText(LeaderboardActivity.this, "No data available.", Toast.LENGTH_LONG).show();
        }
    }
}