package inc.smart.solutions.imayaprofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import inc.smart.solutions.imayaprofile.adapter.GridViewAdapter;
import inc.smart.solutions.imayaprofile.constants.Configs;
import inc.smart.solutions.imayaprofile.dialogs.CustomDialog;
import inc.smart.solutions.imayaprofile.models.Beanclass;
import inc.smart.solutions.imayaprofile.models.GitHub;
import inc.smart.solutions.imayaprofile.retrofit.GitHubApiManager;
import inc.smart.solutions.imayaprofile.utils.ExpandableHeightGridView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 99;
    private TextView tvPublicRepos, tvFollowers, tvFollowing, tvLocation;
    private ExpandableHeightGridView gridView;
    private int[] Image = {R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image5,R.drawable.image6};
    private ArrayList<Beanclass> beans;
    private GridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.white_dismas_logo);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);

        tvPublicRepos = findViewById(R.id.tvPublicRepos);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);
        tvLocation = findViewById(R.id.tvLocation);
        fetchUserGitHub();

        LinearLayout llLinkedIn = findViewById(R.id.llLinkedIn);
        LinearLayout llGitHub = findViewById(R.id.llGitHub);
        LinearLayout llWebsite = findViewById(R.id.llWebsite);
        LinearLayout llBlog = findViewById(R.id.llBlog);
        llLinkedIn.setOnClickListener(this);
        llGitHub.setOnClickListener(this);
        llWebsite.setOnClickListener(this);
        llBlog.setOnClickListener(this);

        gridView = findViewById(R.id.gridView);
        beans= new ArrayList<>();

        for (int i = 0; i< Image.length; i++) {

            Beanclass beanclass = new Beanclass(Image[i]);
            beans.add(beanclass);

        }
        gridViewAdapter = new GridViewAdapter(MainActivity.this, beans);
        gridView.setExpanded(true);

        gridView.setAdapter(gridViewAdapter);
    }

    private void fetchUserGitHub() {
        GitHubApiManager gitHubApiManager = GitHubApiManager.getInstance();
        gitHubApiManager.getGitHubUser(new Callback<GitHub>() {
            @Override
            public void onResponse(@NotNull Call<GitHub> call, @NotNull Response<GitHub> response) {
                if (response.isSuccessful()) {
                    GitHub gitHub = response.body();
                    if (gitHub != null) {
                        tvPublicRepos.setText(String.valueOf(gitHub.getPublic_repos()));
                        tvFollowers.setText(String.valueOf(gitHub.getFollowers()));
                        tvFollowing.setText(String.valueOf(gitHub.getFollowing()));
                        tvLocation.setText(gitHub.getLocation());
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

    private void placeCall(String phone){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                Bundle args = new Bundle();
                args.putString(CustomDialog.CUSTOM_DIALOG_TITLE, "Call Permission Needed");
                args.putString(CustomDialog.CUSTOM_DIALOG_MESSAGE, "This app needs the Phone Call permission. Please accept to place a call");

                DialogFragment dialogFragment = new CustomDialog();
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "Request Call Phone Permission");

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
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello There");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hello Imaya,\n\n");
        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "No email clients installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp(){
        Context context = MainActivity.this;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Imaya Profile App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("Have a look at Imaya Profile on Google Play: https://play.google.com/store/apps/details?id=" + context.getPackageName() +" without the need to install it"));
        startActivityForResult(Intent.createChooser(sharingIntent, "Invite via"), 202);
    }

    private void rateApp(){
        Context context = MainActivity.this;
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count on Play market backstack, after pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,  Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
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
                openURLExternal(Configs.WEBSITE_URL);
                break;
            case R.id.llBlog:
                openURLExternal(Configs.BLOG_URL);
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
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    placeCall(getResources().getString(R.string.phone));
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied, the app requires your Phone Call permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
