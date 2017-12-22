package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.pawelkleczkowski.customgauge.CustomGauge;

import static java.security.AccessController.getContext;

public class HomeActivity extends AppCompatActivity {
    CircleImageView profile_image;
    static CustomGauge myGauge;
    static TextView totalpercentage;
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Boolean isFabOpen = false;
    FloatingActionButton fab1, fab2, fab3, fab4, fab5, fab6;
    private Animation fab_open, fab_close, fade_in, fade_out;
    static String username = "", imageuri = "", nameOfUser = "";
    FragmentTasksClass fragmentTasksClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    username = user.getEmail();
                    onSignedInInitialize();
                }
                else
                {
                    onSignedOutCleanup();
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                    );
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .setTheme(R.style.LoginTheme)
                                    .setLogo(R.drawable.applogo)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        myGauge = findViewById(R.id.gauge1);
        totalpercentage = findViewById(R.id.totalpercentage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profile_image = findViewById(R.id.profile_image);
        /*TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));*/
        fab1 = findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });
        fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(HomeActivity.this);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });
        fab3 = findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCftZVB8RYby7bGqLf8BRqlA/videos")));
            }
        });
        fab4 = findViewById(R.id.fab4);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/_u/TechEagle.in/")));
            }
        });
        fab5 = findViewById(R.id.fab5);
        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/116052057022465218921")));
            }
        });
        fab6 = findViewById(R.id.fab6);
        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://techeagle.in")));
            }
        });
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
    }

    public void animateFAB() {
        if(isFabOpen)
        {
            fab1.setImageResource(android.R.drawable.ic_menu_share);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab5.startAnimation(fab_close);
            fab6.startAnimation(fab_close);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            fab5.setClickable(false);
            fab6.setClickable(false);
            isFabOpen = false;

        }
        else
        {
            fab1.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab5.startAnimation(fab_open);
            fab6.startAnimation(fab_open);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            fab5.setClickable(true);
            fab6.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.leaderboard:
                startActivity(new Intent(HomeActivity.this, LeaderboardActivity.class));
                break;
            case R.id.aboutte:
                break;
            case R.id.aboutapp:
                break;
            case R.id.logout:
                new AlertDialog.Builder(this)
                        .setTitle("Really Logout?")
                        .setMessage("Are you sure you want to Logout?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                AuthUI.getInstance().signOut(HomeActivity.this);
                            }
                        }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    fragmentTasksClass = new FragmentTasksClass();
                    return fragmentTasksClass;
            }
            return null;
        }
        @Override
        public int getCount() {
            return 1;
        }
    }

    private void onSignedInInitialize() {
        imageuri = mFirebaseAuth.getCurrentUser().getPhotoUrl().toString();
        nameOfUser = mFirebaseAuth.getCurrentUser().getDisplayName();
        if (!Objects.equals(imageuri, "")) {
            Picasso.with(this).load(imageuri).into(profile_image);
        }
        TextView name = findViewById(R.id.name);
        name.setText(nameOfUser);
        new Login().execute(username);
    }

    private void onSignedOutCleanup() {
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_CANCELED)
                finish();
    }

    private class Login extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(HomeActivity.this, "Please Wait!","Validating Login!");
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"login.php?username="+strings[0];
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
            if(webPage.contains("login successful<br>"))
            {
                int brI = webPage.indexOf("<br>");
                webPage = webPage.substring(brI+4);
                brI = webPage.indexOf("<br>");
                String tcapID = webPage.substring(0, brI);
                webPage = webPage.substring(brI+4);
                brI = webPage.indexOf("<br>");
                String points = webPage.substring(0, brI);
                webPage = webPage.substring(brI+4);
                brI = webPage.indexOf("<br>");
                String rank = webPage.substring(0, brI);
                TextView tcapid = findViewById(R.id.tcapid);
                tcapid.setText(tcapID);
                TextView userpoints = findViewById(R.id.userpoints);
                userpoints.setText("Points\n" + points);
                TextView userrank = findViewById(R.id.userrank);
                userrank.setText("Rank\n" + rank);
                SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                ViewPager mViewPager = findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setOffscreenPageLimit(1);
            }
            else
            {
                Toast.makeText(HomeActivity.this, "Invalid Login.", Toast.LENGTH_LONG).show();
                AuthUI.getInstance().signOut(HomeActivity.this);
            }
        }
    }

    public static String getFacebookPageURL(Context context)
    {
        final String FACEBOOK_PAGE_ID = "1634843790088323";
        final String FACEBOOK_URL = "https://www.facebook.com/TechEagle.in";
        if(appInstalledOrNot(context, "com.facebook.katana"))
        {
            try
            {
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
            catch(Exception e)
            {
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            }
        }
        else
            return FACEBOOK_URL;
    }

    private static boolean appInstalledOrNot(Context context, String uri)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch(PackageManager.NameNotFoundException ignored){}
        return false;
    }

}