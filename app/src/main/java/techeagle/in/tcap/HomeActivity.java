package techeagle.in.tcap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class HomeActivity extends AppCompatActivity {
    CircleImageView profile_image;
    static CustomGauge myGauge;
    static TextView totalpercentage, userrank, userpoints;
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Boolean isFabOpen = false, loginDialogShow = true;
    FloatingActionButton fab1, fab2, fab3, fab4, fab5, fab6;
    private Animation fab_open, fab_close, fade_in, fade_out;
    static String username = "", imageuri = "", nameOfUser = "", tcapID = "";
    FragmentTasksClass fragmentTasksClass;
    Menu mymenu;
    int notifCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Window window = HomeActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimaryDark));
        }
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    username = user.getEmail();
                    String provider = user.getProviders().get(0);
                    if (provider.equals("password"))
                    {
                        Toast.makeText(HomeActivity.this, "We recommend using your google account to login.", Toast.LENGTH_SHORT).show();
                        AuthUI.getInstance().signOut(HomeActivity.this);
                    }
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
        userrank = findViewById(R.id.userrank);
        userpoints = findViewById(R.id.userpoints);
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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/_u/TechEagle.in/")));
            }
        });
        fab3 = findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/TechEagle")));
            }
        });
        fab4 = findViewById(R.id.fab4);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(HomeActivity.this);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
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
        ImageView edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("tcapid", tcapID);
                intent.putExtra("email", username);
                startActivity(intent);
            }
        });
        final LinearLayout status = findViewById(R.id.status);
        LinearLayout display = findViewById(R.id.display);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibility = status.getVisibility();
                if (visibility == View.VISIBLE)
                    status.setVisibility(View.GONE);
                else
                    status.setVisibility(View.VISIBLE);
            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibility = status.getVisibility();
                if (visibility == View.VISIBLE)
                    status.setVisibility(View.GONE);
                else
                    status.setVisibility(View.VISIBLE);
            }
        });
    }

    public void animateFAB() {
        if(isFabOpen)
        {
            fab1.setImageResource(R.drawable.share);
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
        MenuItem menuItem = menu.findItem(R.id.bell);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
        mymenu = menu;
        new Utils2().setBadgeCount(HomeActivity.this, icon, notifCount);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.bell:
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.sync:
                loginDialogShow = false;
                new Login().execute(username);
                break;
            case R.id.leaderboard:
                startActivity(new Intent(HomeActivity.this, LeaderboardActivity.class));
                break;
            case R.id.aboutte:
                startActivity(new Intent(HomeActivity.this, AboutTEActivity.class));
                break;
            case R.id.aboutapp:
                startActivity(new Intent(HomeActivity.this, AboutAppActivity.class));
                break;
            case R.id.guidlines:
                startActivity(new Intent(HomeActivity.this, GuidelinesActivity.class));
                break;
            case R.id.logout:
                new AlertDialog.Builder(this)
                        .setTitle("Are you sure you want to Logout?")
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
        try
        {imageuri = mFirebaseAuth.getCurrentUser().getPhotoUrl().toString();}catch (Exception ignored){}
        if (!Objects.equals(imageuri, "")) {
            Picasso.with(this).load(imageuri).into(profile_image);
        }
        new Login().execute(username);
    }

    private void onSignedOutCleanup() {
    }

    public class BadgeDrawable extends Drawable {

        private float mTextSize;
        private Paint mBadgePaint;
        private Paint mTextPaint;
        private Rect mTxtRect = new Rect();

        private String mCount = "";
        private boolean mWillDraw = false;

        public BadgeDrawable(Context context) {
            //mTextSize = context.getResources().getDimension(R.dimen.badge_text_size);
            mTextSize = 26F;

            mBadgePaint = new Paint();
            mBadgePaint.setColor(getResources().getColor(R.color.colorAccent));
            mBadgePaint.setAntiAlias(true);
            mBadgePaint.setStyle(Paint.Style.FILL);

            mTextPaint = new Paint();
            mTextPaint.setColor(Color.WHITE);
            mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void draw(Canvas canvas) {
            if (!mWillDraw) {
                return;
            }

            Rect bounds = getBounds();
            float width = bounds.right - bounds.left;
            float height = bounds.bottom - bounds.top;

            // Position the badge in the top-right quadrant of the icon.
            float radius = ((Math.min(width, height) / 2) - 1) / 1.5f;
            //float radius = ((Math.min(width, height) / 2) - 1) / 1.5f;
            float centerX = width - radius/2 - 1;
            //float centerX = width - radius - 1;
            float centerY = radius*0.5f + 1;
            //float centerY = radius + 1;

            // Draw badge circle.
            canvas.drawCircle(centerX, centerY, radius, mBadgePaint);

            // Draw badge count text inside the circle.
            mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
            float textHeight = mTxtRect.bottom - mTxtRect.top;
            float textY = centerY + (textHeight / 2f);
            canvas.drawText(mCount, centerX, textY, mTextPaint);
        }

        /*
        Sets the count (i.e notifications) to display.
         */
        public void setCount(int count) {
            mCount = Integer.toString(count);

            // Only draw a badge if there are notifications.
            mWillDraw = count > 0;
            invalidateSelf();
        }

        @Override
        public void setAlpha(int alpha) {
            // do nothing
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            // do nothing
        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }

    public class Utils2 {
        void setBadgeCount(Context context, LayerDrawable icon, int count) {

            BadgeDrawable badge;

            // Reuse drawable if possible
            Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
            if (reuse != null && reuse instanceof BadgeDrawable) {
                badge = (BadgeDrawable) reuse;
            } else {
                badge = new BadgeDrawable(context);
            }

            badge.setCount(count);
            icon.mutate();
            icon.setDrawableByLayerId(R.id.ic_badge, badge);
        }
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

    private class FetchNotificationCount extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
        @Override
        protected Void doInBackground(String... strings){
            URL url;
            HttpURLConnection urlConnection = null;
            try
            {
                String myURL = baseUrl+"getnotificationcount.php?username="+strings[0];
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
            mymenu.clear();
            notifCount = Integer.parseInt(webPage);
            onCreateOptionsMenu(mymenu);
        }
    }

    private class Login extends AsyncTask<String,Void,Void> {
        String webPage="";
        String baseUrl = "http://www.techeagle.in/tcap/v2/";
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            if (loginDialogShow)
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
            if(webPage.contains("login successful<br>"))
            {
                int brI = webPage.indexOf("<br>");
                webPage = webPage.substring(brI+4);
                brI = webPage.indexOf("<br>");
                nameOfUser = webPage.substring(0, brI);
                webPage = webPage.substring(brI+4);
                brI = webPage.indexOf("<br>");
                tcapID = webPage.substring(0, brI);
                if (nameOfUser.isEmpty())
                {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    intent.putExtra("tcapid", tcapID);
                    intent.putExtra("email", username);
                    startActivity(intent);
                }
                else
                {
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    String points = webPage.substring(0, brI);
                    webPage = webPage.substring(brI+4);
                    brI = webPage.indexOf("<br>");
                    String rank = webPage.substring(0, brI);
                    TextView nameText = findViewById(R.id.name);
                    nameText.setText(nameOfUser);
                    TextView tcapid = findViewById(R.id.tcapid);
                    tcapid.setText(tcapID);
                    userpoints.setText("Points\n" + points);
                    userrank.setText("Rank\n" + rank);
                    SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    ViewPager mViewPager = findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    mViewPager.setOffscreenPageLimit(1);
                    new FetchNotificationCount().execute(username);
                }
            }
            else
            {
                Toast.makeText(HomeActivity.this, "Invalid Login.", Toast.LENGTH_LONG).show();
                AuthUI.getInstance().signOut(HomeActivity.this);
            }
            if (loginDialogShow)
                progressDialog.dismiss();
        }
    }

    public static String getFacebookPageURL(Context context) {
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

    private static boolean appInstalledOrNot(Context context, String uri) {
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