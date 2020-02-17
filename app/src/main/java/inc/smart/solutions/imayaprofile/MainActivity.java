package inc.smart.solutions.imayaprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import inc.smart.solutions.imayaprofile.models.Beanclass;
import inc.smart.solutions.imayaprofile.models.GitHub;
import inc.smart.solutions.imayaprofile.retrofit.GitHubApiManager;
import inc.smart.solutions.imayaprofile.utils.ExpandableHeightGridView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private TextView tvPublicRepos, tvFollowers, tvFollowing, tvLocation;
    private ExpandableHeightGridView gridView;
    private int[] Image = {R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image5,R.drawable.image6};
    private ArrayList<Beanclass> beans;
    private GridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
