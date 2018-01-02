package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardActivity extends AppCompatActivity {
    int ids[][];
    String email = HomeActivity.username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ids = new int[10][3];
        ids[0][0] = R.id.name1;        ids[0][1] = R.id.points1;        ids[0][2] = R.id.dp1;
        ids[1][0] = R.id.name2;        ids[1][1] = R.id.points2;        ids[1][2] = R.id.dp2;
        ids[2][0] = R.id.name3;        ids[2][1] = R.id.points3;        ids[2][2] = R.id.dp3;
        ids[3][0] = R.id.name4;        ids[3][1] = R.id.points4;        ids[3][2] = R.id.dp4;
        ids[4][0] = R.id.name5;        ids[4][1] = R.id.points5;        ids[4][2] = R.id.dp5;
        ids[5][0] = R.id.name6;        ids[5][1] = R.id.points6;        ids[5][2] = R.id.dp6;
        ids[6][0] = R.id.name7;        ids[6][1] = R.id.points7;        ids[6][2] = R.id.dp7;
        ids[7][0] = R.id.name8;        ids[7][1] = R.id.points8;        ids[7][2] = R.id.dp8;
        ids[8][0] = R.id.name9;        ids[8][1] = R.id.points9;        ids[8][2] = R.id.dp9;
        ids[9][0] = R.id.name10;        ids[9][1] = R.id.points10;        ids[9][2] = R.id.dp10;
        new FetchLeaderboard().execute();
        setTitle("Leaderboard");
    }
    private class FetchLeaderboard extends AsyncTask<Void, Void, Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(LeaderboardActivity.this, "Please Wait!","Fetching Leaderboard!");
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
            int i = 0;
            while(webPage.contains("<br>"))
            {
                int index = webPage.indexOf("<br>");
                String username = webPage.substring(0, index);
                webPage = webPage.substring(index + 4);
                index = webPage.indexOf("<br>");
                String name = webPage.substring(0, index);
                webPage = webPage.substring(index + 4);
                index = webPage.indexOf("<br>");
                String userpoints = webPage.substring(0, index);
                webPage = webPage.substring(index + 4);
                index = webPage.indexOf("<br>");
                String image = webPage.substring(0, index);
                webPage = webPage.substring(index + 4);
                if (i < 10)
                {
                    TextView nametv = findViewById(ids[i][0]);
                    nametv.setText(name);
                    TextView pointstv = findViewById(ids[i][1]);
                    if (userpoints.equals("0"))
                    {
                        View v = (View)pointstv.getParent().getParent();
                        v.setVisibility(View.GONE);
                    }
                    else
                        pointstv.setText("Points : " + userpoints);

                    if (!Objects.equals(image, "")) {
                        CircleImageView civ = findViewById(ids[i][2]);
                        Picasso.with(LeaderboardActivity.this).load(image).into(civ);
                    }
                }
                else
                {
                    if (name.isEmpty())
                        continue;
                    Context context = LeaderboardActivity.this;
                    LinearLayout rankL = findViewById(R.id.rank);
                    LinearLayout nameL = findViewById(R.id.name);
                    LinearLayout pointsL = findViewById(R.id.points);
                    TextView ranktv = new TextView(context);
                    ranktv.setTextColor(Color.WHITE);
                    ranktv.setText((i+1) + "");
                    ranktv.setGravity(Gravity.CENTER_HORIZONTAL);
                    ranktv.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
                    rankL.addView(ranktv);
                    TextView nametv = new TextView(context);
                    nametv.setTextColor(Color.WHITE);
                    nametv.setText(name);
                    nametv.setGravity(Gravity.CENTER_HORIZONTAL);
                    nametv.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
                    nameL.addView(nametv);
                    TextView pointstv = new TextView(context);
                    pointstv.setTextColor(Color.WHITE);
                    pointstv.setText(userpoints);
                    pointstv.setGravity(Gravity.CENTER_HORIZONTAL);
                    pointstv.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
                    pointsL.addView(pointstv);
                    if (username.equals(email))
                    {
                        ranktv.setTextColor(getResources().getColor(R.color.colorAccent));
                        nametv.setTextColor(getResources().getColor(R.color.colorAccent));
                        pointstv.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                }
                i++;
            }
            progressDialog.dismiss();
        }
    }
}