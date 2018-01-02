package techeagle.in.tcap;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import pl.pawelkleczkowski.customgauge.CustomGauge;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Abhinav on 16-Dec-17.
 */

public class FragmentTasksClass extends Fragment{
    View rootView;
    CustomGauge myGauge;
    TextView totalpercentage, userPoints;
    String username="", imageuri="", name="";
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        myGauge = HomeActivity.myGauge;
        username = HomeActivity.username;
        totalpercentage = HomeActivity.totalpercentage;
        imageuri = HomeActivity.imageuri;
        name = HomeActivity.nameOfUser;
        userPoints = HomeActivity.userpoints;
        new FetchTasks().execute(username);
        new FetchPointsAndProgress().execute(username);
        return rootView;
    }

    private class FetchTasks extends AsyncTask<String,Void,Void> {
        Context context = getActivity();
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
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
                LinearLayout onGoing = rootView.findViewById(R.id.data);
                onGoing.removeAllViews();
                LinearLayout expiredData = rootView.findViewById(R.id.dataexpired);
                expiredData.removeAllViews();
                final LinearLayout completedData = rootView.findViewById(R.id.datacompleted);
                completedData.removeAllViews();
                LinearLayout data;
                while (webPage.contains("<br>"))
                {
                    int brI = webPage.indexOf("<br>");
                    String tasknumber = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    final String taskname = webPage.substring(0, brI);
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
                    brI = webPage.indexOf("<br>");
                    String file = webPage.substring(0, brI);
                    final String fileURI = file.replaceAll(" ", "%20");
                    webPage = webPage.substring(brI + 4);
                    brI = webPage.indexOf("<br>");
                    String expired = webPage.substring(0, brI);
                    webPage = webPage.substring(brI + 4);
                    double com = Integer.parseInt(completed);
                    final double stage = Integer.parseInt(stages);
                    if (expired.equals("yes"))
                    {
                        TextView expiredLabel = rootView.findViewById(R.id.expiredlabel);
                        expiredLabel.setVisibility(View.VISIBLE);
                        data = expiredData;
                    }
                    else if (com >= stage)
                    {
                        TextView completedLabelTop = rootView.findViewById(R.id.completedlabeltop);
                        completedLabelTop.setVisibility(View.VISIBLE);
                        data = completedData;
                    }
                    else
                    {
                        data = onGoing;
                        try
                        {
                            Date curDate = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            Date date = format.parse(deadline);
                            long diffInMillisec =date.getTime()- curDate.getTime();
                            long daysDiff = TimeUnit.MILLISECONDS.toDays(diffInMillisec);
                            long hoursDiff = TimeUnit.MILLISECONDS.toHours(diffInMillisec);
                            long minsDiff = TimeUnit.MILLISECONDS.toMinutes(diffInMillisec);
                            if (daysDiff >= 2)
                                deadline = daysDiff + " Days Left";
                            else if (daysDiff < 2 && hoursDiff >= 1)
                                deadline = hoursDiff + " Hours Left";
                            else if (daysDiff < 2 && hoursDiff < 1)
                                deadline = minsDiff + " Minutes left";
                        }catch (ParseException ignored){
                        }
                    }
                    LinearLayout.LayoutParams matchParams = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                    LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    final LinearLayout outer = new LinearLayout(context);
                    outer.setLayoutParams(matchParams);
                    outer.setOrientation(LinearLayout.VERTICAL);
                    outer.setPadding(0,0,0,30);
                    final CardView cardView = new CardView(context);
                    cardView.setLayoutParams(matchParams);
                    LinearLayout mid = new LinearLayout(context);
                    mid.setLayoutParams(matchParams);
                    mid.setOrientation(LinearLayout.VERTICAL);
                    mid.setPadding(20,20,20,20);
                    LinearLayout title = new LinearLayout(context);
                    title.setLayoutParams(matchParams);
                    title.setOrientation(LinearLayout.HORIZONTAL);
                    TextView tasknumberLabel = new TextView(context);
                    tasknumberLabel.setLayoutParams(wrapParams);
                    tasknumberLabel.setTextColor(getResources().getColor(R.color.colorBackground));
                    tasknumberLabel.setText(tasknumber+") ");
                    tasknumberLabel.setTextSize(20);
                    title.addView(tasknumberLabel);
                    TextView nameLabel = new TextView(context);
                    nameLabel.setLayoutParams(wrapParams);
                    nameLabel.setText(taskname);
                    nameLabel.setTextColor(getResources().getColor(R.color.colorBackground));
                    nameLabel.setTextSize(20);
                    title.addView(nameLabel);
                    mid.addView(title);
                    TextView deadlineLabel = new TextView(context);
                    deadlineLabel.setText("Deadline : " + deadline);
                    deadlineLabel.setTextSize(18);
                    mid.addView(deadlineLabel);
                    LayoutInflater progressInflater = LayoutInflater.from(context);
                    final SeekBar seekBar = (SeekBar) progressInflater.inflate(R.layout.seekbar, null);
                    seekBar.setOnTouchListener(new View.OnTouchListener()
                    {@Override public boolean onTouch(View view, MotionEvent motionEvent) {return true;}});
                    seekBar.setMax((int)stage);
                    seekBar.setProgress((int)com);
                    mid.addView(seekBar);
                    final int id = View.generateViewId();
                    LinearLayout inner = new LinearLayout(context);
                    inner.setLayoutParams(matchParams);
                    inner.setOrientation(LinearLayout.VERTICAL);
                    inner.setVisibility(View.GONE);
                    inner.setId(id);
                    final TextView completedLabel = new TextView(context);
                    completedLabel.setText("Completed : " + completed + "/" + stages);
                    completedLabel.setTextSize(18);
                    completedLabel.setGravity(Gravity.CENTER);
                    inner.addView(completedLabel);
                    TextView rewardPointsLabel = new TextView(context);
                    rewardPointsLabel.setLayoutParams(matchParams);
                    rewardPointsLabel.setText("Reward Points : " + rewardPoints+"\n");
                    rewardPointsLabel.setTextSize(18);
                    inner.addView(rewardPointsLabel);
                    TextView detailsLabel = new TextView(context);
                    detailsLabel.setText("Details : " + details);
                    detailsLabel.setPadding(8,0,8,8);
                    detailsLabel.setTextSize(18);
                    inner.addView(detailsLabel);
                    if (fileURI.length() > 0)
                    {
                        TextView attachedFile = new TextView(context);
                        attachedFile.setText("Download attached file");
                        attachedFile.setTextColor(getResources().getColor(R.color.colorAccent));
                        detailsLabel.setTextSize(16);
                        attachedFile.setPadding(16,16,16,16);
                        attachedFile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkPermissions(fileURI);
                            }
                        });
                        inner.addView(attachedFile);
                    }
                    if(com < stage && expired.equals("no"))
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
                                        .setMessage("Are you sure you have completed one more stage?")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                int com = seekBar.getProgress() + 1;
                                                int stages = seekBar.getMax();
                                                if (com == stages)
                                                {
                                                    buttons.setVisibility(View.GONE);
                                                    TextView completedLabelTop = rootView.findViewById(R.id.completedlabeltop);
                                                    completedLabelTop.setVisibility(View.VISIBLE);
                                                    LinearLayout parent = (LinearLayout) cardView.getParent().getParent();
                                                    parent.removeView(outer);
                                                    completedData.addView(outer);
                                                    getFeedBack(taskname, taskid, 1, seekBar, completedLabel, buttons);
                                                }
                                                else
                                                {
                                                    new AddProgress().execute(username, taskid, 1+"");
                                                    seekBar.setProgress(com);
                                                    completedLabel.setText("Completed : " + com + "/" + stages);
                                                }
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
                                                TextView completedLabelTop = rootView.findViewById(R.id.completedlabeltop);
                                                completedLabelTop.setVisibility(View.VISIBLE);
                                                LinearLayout parent = (LinearLayout) cardView.getParent().getParent();
                                                parent.removeView(outer);
                                                completedData.addView(outer);
                                                getFeedBack(taskname, taskid, (int)(stage - seekBar.getProgress()), seekBar, completedLabel, buttons);
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .create().show();
                            }
                        });
                        buttons.addView(completedAll);
                        inner.addView(buttons);
                    }
                    LinearLayout feedbackL = new LinearLayout(context);
                    feedbackL.setLayoutParams(matchParams);
                    TextView submitDetails = new TextView(context);
                    submitDetails.setText("Submit Details");
                    submitDetails.setLayoutParams(matchParams);
                    submitDetails.setPadding(16,16,16,16);
                    submitDetails.setTextSize(16);
                    submitDetails.setGravity(Gravity.START);
                    submitDetails.setTextColor(getResources().getColor(R.color.colorAccent));
                    submitDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                            intent.putExtra("taskname", taskname);
                            intent.putExtra("username", username);
                            intent.putExtra("taskid", taskid);
                            intent.putExtra("direct","yes");
                            intent.putExtra("query","no");
                            startActivity(intent);
                        }
                    });
                    feedbackL.addView(submitDetails);
                    TextView feedback = new TextView(context);
                    feedback.setLayoutParams(matchParams);
                    feedback.setText("Ask a question");
                    feedback.setGravity(Gravity.END);
                    feedback.setPadding(16,16,16,16);
                    feedback.setTextSize(16);
                    feedback.setTextColor(Color.RED);
                    feedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                            intent.putExtra("taskname", taskname);
                            intent.putExtra("username", username);
                            intent.putExtra("taskid", taskid);
                            intent.putExtra("direct","yes");
                            intent.putExtra("query","yes");
                            startActivity(intent);
                        }
                    });
                    feedbackL.addView(feedback);
                    inner.addView(feedbackL);
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
                Toast.makeText(getActivity(), "Some Error Occurred while Fetching Tasks", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        void getFeedBack(String taskname, String taskid, int progress, SeekBar seekBar,
                         TextView completedLabel, LinearLayout buttons){
            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
            intent.putExtra("taskname", taskname);
            intent.putExtra("username", username);
            intent.putExtra("taskid", taskid);
            intent.putExtra("progress", progress+"");
            intent.putExtra("query","no");
            int seekbarId = View.generateViewId();
            seekBar.setId(seekbarId);
            intent.putExtra("seekbar", seekbarId + "");
            int textviewId = View.generateViewId();
            completedLabel.setId(textviewId);
            intent.putExtra("completedLabel", textviewId + "");
            int linearlayoutId = View.generateViewId();
            buttons.setId(linearlayoutId);
            intent.putExtra("buttons", linearlayoutId + "");
            startActivityForResult(intent, 1);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String completedFeedback = data.getStringExtra("completedFeedback");
            if (completedFeedback.equals("yes"))
            {
                String myProgress = data.getStringExtra("progress");
                String myTaskId = data.getStringExtra("taskid");
                String seekbarId = data.getStringExtra("seekbar");
                String textviewId = data.getStringExtra("completedLabel");
                String linearLayoutId = data.getStringExtra("buttons");
                new AddProgress().execute(username, myTaskId, myProgress);
                SeekBar mySeekbar = rootView.findViewById(Integer.parseInt(seekbarId));
                TextView completedLabel = rootView.findViewById(Integer.parseInt(textviewId));
                LinearLayout buttons = rootView.findViewById(Integer.parseInt(linearLayoutId));
                mySeekbar.setProgress(mySeekbar.getProgress() + Integer.parseInt(myProgress));
                int com = mySeekbar.getProgress();
                int stages = mySeekbar.getMax();
                completedLabel.setText("Completed : " + com + "/" + stages);
                if (com == stages)
                    buttons.setVisibility(View.GONE);
            }
        }
    }

    private class AddProgress extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
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
                        +"&userprogress="+strings[2]+"&imageuri="+imageuri;
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
            if(webPage.equals("success"))
                Toast.makeText(getActivity(), "Progress Updated Successfully.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "Some error occurred.", Toast.LENGTH_LONG).show();
            new FetchPointsAndProgress().execute(username);
        }
    }

    private class FetchPointsAndProgress extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"fetchstatus.php?username="+strings[0];
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
                int totalprogress = Integer.parseInt(webPage.substring(0, brI));
                webPage = webPage.substring(brI+4);
                brI = webPage.indexOf("<br>");
                String rank = webPage.substring(0, brI);
                webPage = webPage.substring(brI+4);
                myGauge.setValue(totalprogress);
                totalpercentage.setText(myGauge.getValue()+"%");
                userPoints.setText("Points\n" + points);
            }
            else
                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_LONG).show();
        }
    }

    public void checkPermissions(String fileURI) {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
                download(fileURI);
        }
        else
            download(fileURI);
    }

    private void download(String fileURI) {
        Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileURI));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String filename = URLUtil.guessFileName(fileURI, null, MimeTypeMap.getFileExtensionFromUrl(fileURI));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}