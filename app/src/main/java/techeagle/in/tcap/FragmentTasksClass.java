package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * Created by Abhinav on 16-Dec-17.
 */

public class FragmentTasksClass extends Fragment{
    View rootView;
    CustomGauge myGauge;
    TextView totalpercentage, userPoints, userRank;
    String username="", imageuri="", name="";
    int count = 0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        myGauge = HomeActivity.myGauge;
        username = HomeActivity.username;
        totalpercentage = HomeActivity.totalpercentage;
        imageuri = HomeActivity.imageuri;
        name = HomeActivity.nameOfUser;
        userPoints = HomeActivity.userpoints;
        userRank = HomeActivity.userrank;
        new FetchTasks().execute(username);
        return rootView;
    }
    private class FetchTasks extends AsyncTask<String,Void,Void> {
        Context context = getActivity();
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(getActivity(), "Please Wait!","Fetching Tasks!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchtasks.php?username=" + strings[0];
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
            if(webPage.contains("<br>"))
            {
                LinearLayout data = rootView.findViewById(R.id.data);
                data.removeAllViews();
                double totalProgress = 0;
                while (webPage.contains("<br>"))
                {
                    count++;
                    int brI = webPage.indexOf("<br>");
                    String name = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String taskid = webPage.substring(0, brI);
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
                    mid.setPadding(20,20,20,20);
                    LinearLayout title = new LinearLayout(context);
                    title.setLayoutParams(matchParams);
                    title.setOrientation(LinearLayout.HORIZONTAL);
                    TextView nameLabel = new TextView(context);
                    nameLabel.setLayoutParams(matchParams);
                    nameLabel.setText(name);
                    nameLabel.setTextSize(24);
                    nameLabel.setGravity(Gravity.START);
                    title.addView(nameLabel);
                    TextView rewardPointsLabel = new TextView(context);
                    rewardPointsLabel.setLayoutParams(matchParams);
                    rewardPointsLabel.setText("Points : " + rewardPoints);
                    rewardPointsLabel.setTextSize(20);
                    rewardPointsLabel.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);
                    title.addView(rewardPointsLabel);
                    mid.addView(title);
                    TextView deadlineLabel = new TextView(context);
                    deadlineLabel.setText("Deadline : " + deadline);
                    deadlineLabel.setTextSize(20);
                    deadlineLabel.setGravity(Gravity.START);
                    mid.addView(deadlineLabel);
                    LayoutInflater progressInflater = LayoutInflater.from(context);
                    final SeekBar seekBar = (SeekBar) progressInflater.inflate(R.layout.seekbar, null);
                    seekBar.setOnTouchListener(new View.OnTouchListener()
                    {@Override public boolean onTouch(View view, MotionEvent motionEvent) {return true;}});
                    final double com[] = new double[1];
                    com[0] = Integer.parseInt(completed);
                    final double stage = Integer.parseInt(stages);
                    totalProgress += (com[0]/stage);
                    seekBar.setMax((int)stage);
                    seekBar.setProgress((int)com[0]);
                    mid.addView(seekBar);
                    final int id = View.generateViewId();
                    LinearLayout inner = new LinearLayout(context);
                    inner.setLayoutParams(matchParams);
                    inner.setOrientation(LinearLayout.VERTICAL);
                    inner.setVisibility(View.GONE);
                    inner.setId(id);
                    final TextView completedLabel = new TextView(context);
                    completedLabel.setText("Completed : " + completed + "/" + stages);
                    completedLabel.setTextSize(20);
                    completedLabel.setGravity(Gravity.CENTER);
                    inner.addView(completedLabel);
                    TextView detailsLabel = new TextView(context);
                    detailsLabel.setText("Details : " + details);
                    detailsLabel.setPadding(8,0,8,8);
                    detailsLabel.setTextSize(20);
                    detailsLabel.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    inner.addView(detailsLabel);
                    if(com[0] < stage)
                    {
                        final LinearLayout buttons = new LinearLayout(context);
                        buttons.setOrientation(LinearLayout.HORIZONTAL);
                        buttons.setLayoutParams(matchParams);
                        Button completedOne = new Button(context);
                        completedOne.setLayoutParams(matchParams);
                        completedOne.setText("Completed One More");
                        buttons.addView(completedOne);
                        Button completedAll = new Button(context);
                        completedAll.setLayoutParams(matchParams);
                        completedAll.setText("Completed All");
                        completedOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Confirm Progress")
                                        .setMessage("Are you sure you?")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                calculateFinalProgress(1, (int)stage);
                                                new AddProgress().execute(username, taskid, "1");
                                                com[0]++;
                                                seekBar.setProgress((int)com[0]);
                                                completedLabel.setText("Completed : " + com[0] + "/" + stage);
                                                if (com[0] == stage)
                                                    buttons.setVisibility(View.GONE);
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .create().show();
                            }
                        });
                        completedAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Confirm Progress")
                                        .setMessage("Are you sure you have completed the task?")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                calculateFinalProgress((int)(stage-com[0]), (int)stage);
                                                new AddProgress().execute(username, taskid, ""+(int)(stage - com[0]));
                                                seekBar.setProgress((int)stage);
                                                completedLabel.setText("Completed : " + stage + "/" + stage);
                                                buttons.setVisibility(View.GONE);
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .create().show();
                            }
                        });
                        buttons.addView(completedAll);
                        inner.addView(buttons);
                    }
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
                int finalProgress = (int)(totalProgress*100/count);
                myGauge.setValue(finalProgress);
                String progString = finalProgress+"";
                if (progString.length()>6)
                    progString = progString.substring(0, 6);
                totalpercentage.setText(progString + "%");
            }
            else
                Toast.makeText(getActivity(), "Some Error Occurred.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        void calculateFinalProgress(double num,  double deno) {
            double prog = myGauge.getValue();
            prog = ((prog*count)/100+(num/deno))*100/count;
            myGauge.setValue((int)prog);
            String progString = prog+"";
            if (progString.length()>6)
                progString = progString.substring(0, 6);
            totalpercentage.setText(progString + "%");
        }
    }
    private class AddProgress extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(getActivity(), "Please Wait!","Updating!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"addprogress.php?username="+strings[0]+"&taskid="+strings[1]
                        +"&userprogress="+strings[2]+"&totalprogress="+myGauge.getValue()+"&name="+name+"&imageuri="+imageuri;
                myURL = myURL.replaceAll(" ", "%20");
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
                Toast.makeText(getActivity(), "Progress Updated Successfully.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "Some error occurred.", Toast.LENGTH_LONG).show();

            new FetchPointsAndProgress().execute(username);
        }
    }
    private class FetchPointsAndProgress extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchprogressandpoints.php?username="+strings[0];
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
            if(webPage.contains("<br>"))
            {
                int brI = webPage.indexOf("<br>");
                String points = webPage.substring(0, brI);
                webPage = webPage.substring(brI+4);
                brI = webPage.indexOf("<br>");
                String rank = webPage.substring(0, brI);
                userPoints.setText("Points\n" + points);
                userRank.setText("Rank\n" + rank);
            }
            else
                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_LONG).show();
        }
    }
}