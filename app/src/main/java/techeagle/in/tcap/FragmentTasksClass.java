package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Abhinav on 16-Dec-17.
 */

public class FragmentTasksClass extends Fragment{
        View rootView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        new FetchTasks().execute();
        FloatingActionButton sync = rootView.findViewById(R.id.sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchTasks().execute();
            }
        });
        return rootView;
    }
    private class FetchTasks extends AsyncTask<Void,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(getActivity(), "Please Wait!","Fetching Tasks!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchtasks.php";
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
                LinearLayout data = rootView.findViewById(R.id.data);
                data.removeAllViews();
                while (webPage.contains("<br>"))
                {
                    int brI = webPage.indexOf("<br>");
                    String name = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    String deadline = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    String stages = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    String completed = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    String rewardPoints = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br><br>");
                    String details = webPage.substring(0, brI);
                    details = details.replaceAll("<br />", "\n");
                    webPage = webPage.substring(brI + 8);
                    Context context = getActivity();
                    LinearLayout.LayoutParams matchParams = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                    LinearLayout outer = new LinearLayout(context);
                    outer.setLayoutParams(matchParams);
                    outer.setOrientation(LinearLayout.VERTICAL);
                    outer.setPadding(0,0,0,30);
                    CardView cardView = new CardView(context);
                    cardView.setLayoutParams(matchParams);
                    LinearLayout mid = new LinearLayout(context);
                    mid.setLayoutParams(matchParams);
                    mid.setOrientation(LinearLayout.VERTICAL);
                    mid.setPadding(0,20,0,20);
                    TextView nameLabel = new TextView(context);
                    nameLabel.setText(name);
                    nameLabel.setTextSize(24);
                    nameLabel.setGravity(Gravity.CENTER);
                    mid.addView(nameLabel);
                    final int id = View.generateViewId();
                    LinearLayout inner = new LinearLayout(context);
                    inner.setLayoutParams(matchParams);
                    inner.setOrientation(LinearLayout.VERTICAL);
                    inner.setVisibility(View.GONE);
                    inner.setId(id);
                    TextView deadlineLabel = new TextView(context);
                    deadlineLabel.setText("Deadline : " + deadline);
                    deadlineLabel.setTextSize(20);
                    deadlineLabel.setGravity(Gravity.CENTER);
                    inner.addView(deadlineLabel);
                    TextView completedLabel = new TextView(context);
                    completedLabel.setText("Completed : " + completed + "/" + stages);
                    completedLabel.setTextSize(20);
                    completedLabel.setGravity(Gravity.CENTER);
                    inner.addView(completedLabel);
                    TextView rewardPointsLabel = new TextView(context);
                    rewardPointsLabel.setText("Reward Points : " + rewardPoints);
                    rewardPointsLabel.setTextSize(20);
                    rewardPointsLabel.setGravity(Gravity.CENTER);
                    inner.addView(rewardPointsLabel);
                    TextView detailsLabel = new TextView(context);
                    detailsLabel.setText("Details : " + details);
                    detailsLabel.setTextSize(20);
                    detailsLabel.setGravity(Gravity.CENTER);
                    inner.addView(detailsLabel);
                    mid.addView(inner);
                    cardView.addView(mid);
                    outer.addView(cardView);
                    data.addView(outer);
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            View v = rootView.findViewById(id);
                            if(v.getVisibility()==View.GONE)
                                v.setVisibility(View.VISIBLE);
                            else
                                v.setVisibility(View.GONE);
                        }
                    });
                }
            }
            else
                Toast.makeText(getActivity(), "Some Error Occurred.", Toast.LENGTH_LONG).show();
        }
    }
}