package inc.smart.solutions.dismas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.BuildConfig;
import inc.smart.solutions.dismas.adapter.GridViewAdapter;
import inc.smart.solutions.dismas.adapter.ScreenSlidePagerAdapter;
import inc.smart.solutions.dismas.animations.ZoomOutPageTransformer;
import inc.smart.solutions.dismas.constants.Configs;
import inc.smart.solutions.dismas.databinding.ActivityMainBinding;
import inc.smart.solutions.dismas.dialogs.CustomDialog;
import inc.smart.solutions.dismas.models.Projects;
import inc.smart.solutions.dismas.models.GitHub;
import inc.smart.solutions.dismas.retrofit.GitHubApiManager;
import inc.smart.solutions.dismas.utils.ExpandableHeightGridView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 99;
    private ArrayList<Projects> projects;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter screenSlidePagerAdapter;
    private int scrolledPagePosition = -1;
    private int clickedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_certified_32dp);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetchUserGitHub();

        LinearLayout llLinkedIn = findViewById(R.id.llLinkedIn);
        LinearLayout llGitHub = findViewById(R.id.llGitHub);
        LinearLayout llWebsite = findViewById(R.id.llWebsite);
        LinearLayout llBlog = findViewById(R.id.llBlog);
        llLinkedIn.setOnClickListener(this);
        llGitHub.setOnClickListener(this);
        llWebsite.setOnClickListener(this);
        llBlog.setOnClickListener(this);

        ExpandableHeightGridView gridView = findViewById(R.id.gridView);
        projects = new ArrayList<>();

        getNotableProjects();

        viewPager = findViewById(R.id.viewPager);

        GridViewAdapter gridViewAdapter = new GridViewAdapter(MainActivity.this, projects);
        gridView.setExpanded(true);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                clickedPosition = position;
                viewPager.setVisibility(View.VISIBLE);
                setUpPagerAdapter(projects.get(position).getProjectScreenshots());
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int devWidth = displayMetrics.widthPixels;

        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(-devWidth / 2);

        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private void setUpPagerAdapter(String[] images) {
        screenSlidePagerAdapter = new ScreenSlidePagerAdapter(MainActivity.this, images);
        viewPager.setAdapter(screenSlidePagerAdapter);
    }

    public void setVisibilityGone(){
        viewPager.setVisibility(View.GONE);
        scrolledPagePosition = -1;
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (scrolledPagePosition != -1){
                if (scrolledPagePosition != position){
                    if (position > scrolledPagePosition){
                        CardView cardView = screenSlidePagerAdapter.cardViews[position - 1];
                        cardView.setVisibility(View.INVISIBLE);
                    } else {
                        CardView cardView = screenSlidePagerAdapter.cardViews[position + 1];
                        cardView.setVisibility(View.INVISIBLE);
                    }
                    scrolledPagePosition = position;
                }
            } else {
                scrolledPagePosition = position;
                CardView cardView = screenSlidePagerAdapter.cardViews[position];
                cardView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageSelected(int position) {
            CardView cardView = screenSlidePagerAdapter.cardViews[position];
            cardView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            if (viewPager.getVisibility() == View.VISIBLE){
                setVisibilityGone();
            }else{
                super.onBackPressed();
            }
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private void getNotableProjects(){
        Projects project2 = new Projects(
                "Skye",
                "https://lh3.googleusercontent.com/13Hu_EAaR5REMBBikzVC9SjHwuktd0Dx51YCDIvO-0r5vYp-TF7hR_GE4WWAW_rgs-c=s180-rw",
                new String[]{
                        "https://lh3.googleusercontent.com/GmylmXAHMGkCbMZK5jqRoci51YugTGQxjjWaRmmQCqrtjWkDSav03Sq9PmH5Hc-M1EI=w720-h310-rw",
                        "https://lh3.googleusercontent.com/TO_SNb_w2A4nHM0-fWEwjfl_jckebfgCadL8Go1GZ-1winyHtYoBXvsx0F6hTH_x1eL5=w720-h310-rw",
                        "https://lh3.googleusercontent.com/NITDt9iGdYXmCxrvU7Qqzii5MrskYdoZGIshvL1upWB10130OxPjIZihkizdq-BvuEPT=w720-h310-rw",
                        "https://lh3.googleusercontent.com/pzJWuDzE2uOfOMHeGlNKnf9a3zj5HcGnfsFwg7mbbX-t5Y9uKCfiKRIBAuav9LSqhVbI=w720-h310-rw",
                        "https://lh3.googleusercontent.com/OzN2FemEenT7sIXVrCiSU1W-Yyt8GgPAQIwWlKquAF6s_IAY_4g-gLEHaF9bvEERLAoY=w720-h310-rw"},
                "https://play.google.com/store/apps/details?id=com.geeknat.skye"
        );
        projects.add(project2);

        Projects project1 = new Projects(
                "ZingKE",
                "https://lh3.googleusercontent.com/_ixWWKKyxWTT0ayYcKuS6xps4h2dG85vvj3bEEj33Fzs_9egU7bgzDvnUIizBEGTFpc=s180-rw",
                new String[]{
                        "https://lh3.googleusercontent.com/zBeAjRn1VLKXVF89LN2_P6moK6f431btI-cFdnZd4M2i0LzXt6rmzPVEGOlarofiIw=w720-h310-rw",
                        "https://lh3.googleusercontent.com/Bf-hTcWAjtz0rRm3o9b_FTxVkp3KmGEGntCswNd2YDtDMsa9rSIR7xm3HPVkcO_3poL2=w720-h310-rw",
                        "https://lh3.googleusercontent.com/nKgybPnQCvAhFWHop37bIUJiUjP-zWv9pbyDjQAYK3mSE9flR5Bp_jSyl28xZ-W8pFkh=w720-h310-rw",
                        "https://lh3.googleusercontent.com/pPnwqe7nZDWOqpXu2sfPV_4G9srjLu2JJFZehL3eUziIpzYmNM2BBaKUBRtxhEu_XBvw=w720-h310-rw",
                        "https://lh3.googleusercontent.com/pv3LE3Fbt20-b0qNOvtFZD-FlLkcsPFzNzeaSb67vH6DCXbbXTTSDJDT8zcvSWLSwQ=w720-h310-rw",
                        "https://lh3.googleusercontent.com/nvcrQ2G7iG8endNB76mMs6lx1Cd8ybyV6MGXX0FZnp-NQXg_28RPQ9OWxomx5T7haVQ=w720-h310-rw",
                        "https://lh3.googleusercontent.com/26CNZ7OePhsJnhRp3r0fTaO6QZrFPKM0FdxydBj49Hd9x91GYIri2GtKfsUhSqWXDA=w720-h310-rw",
                        "https://lh3.googleusercontent.com/3lcHoEOD9XYlVxergeoBxun2jib2ucz6K-1i_xNWdSzMEIQS3X95r5XqOoueQqK5vAQ=w720-h310-rw"},
                "https://play.google.com/store/apps/details?id=zing.app.co.ke.zing"
        );
        projects.add(project1);

    }

    private void fetchUserGitHub() {
        GitHubApiManager gitHubApiManager = GitHubApiManager.getInstance();
        gitHubApiManager.getGitHubUser(new Callback<GitHub>() {
            @Override
            public void onResponse(@NotNull Call<GitHub> call, @NotNull Response<GitHub> response) {
                if (response.isSuccessful()) {
                    GitHub gitHub = response.body();
                    if (gitHub != null) {
                        binding.tvPublicRepos.setText(String.valueOf(gitHub.getPublic_repos()));
                        binding.tvFollowers.setText(String.valueOf(gitHub.getFollowers()));
                        binding.tvFollowing.setText(String.valueOf(gitHub.getFollowing()));
                        binding.tvLocation.setText(gitHub.getLocation());
                    }
                } else {
                    String message = response.message();
                    try {
                        if (response.errorBody() != null) {
                            message = response.errorBody().string();
                            JSONObject error = new JSONObject(message);
                            Toast.makeText(MainActivity.this, error.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException | JSONException ignored) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<GitHub> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void openURLExternal(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void openPlayStore(){
        openURLExternal(projects.get(clickedPosition).getProjectUrl());
    }

    private void placeCall(String phone){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                Bundle args = new Bundle();
                args.putString(CustomDialog.CUSTOM_DIALOG_TITLE, getResources().getString(R.string.permission_needed));
                args.putString(CustomDialog.CUSTOM_DIALOG_MESSAGE, getResources().getString(R.string.phone_call_permission));

                DialogFragment dialogFragment = new CustomDialog();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), getResources().getString(R.string.request_phone_call_permission));

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,  new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else{
            Uri number = Uri.parse("tel:" + Uri.encode(phone));
            Intent dial = new Intent(Intent.ACTION_CALL, number);
            startActivity(dial);
        }
    }

    private void sendEmail(){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.hello_there));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.hello));
        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_email_using)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_email_clients_installed), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.profile));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.share_app), BuildConfig.APPLICATION_ID));
        startActivityForResult(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)), 202);
    }

    private void rateApp(){
        Context context = MainActivity.this;
        Uri uri = Uri.parse(String.format("%s%s", getResources().getString(R.string.market), context.getPackageName()));
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count on Play market backstack, after pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,  Uri.parse(String.format("%s%s", getResources().getString(R.string.playstore_details), context.getPackageName()))));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.particleView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.particleView.pause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llLinkedIn:
                openURLExternal(Configs.LINKEDIN_URL);
                break;
            case R.id.llGitHub:
                openURLExternal(Configs.GITHUB_URL);
                break;
            case R.id.llWebsite:
                Toast.makeText(MainActivity.this, getResources().getString(R.string.website_under_development), Toast.LENGTH_SHORT).show();
                openURLExternal(Configs.WEBSITE_URL);
                break;
            case R.id.llBlog:
//                openURLExternal(Configs.BLOG_URL);
                Toast.makeText(MainActivity.this, getResources().getString(R.string.blog_under_development), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(MainActivity.this, getResources().getString(R.string.certified_android_software_engineer), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuEmail:
                sendEmail();
                return true;
            case R.id.menuCall:
                placeCall(getResources().getString(R.string.phone));
                return true;
            case R.id.menuShare:
                shareApp();
                return true;
            case R.id.menuRate:
                rateApp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    placeCall(getResources().getString(R.string.phone));
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
