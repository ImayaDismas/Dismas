package inc.smart.solutions.imayaprofile.retrofit;

import inc.smart.solutions.imayaprofile.constants.Configs;
import inc.smart.solutions.imayaprofile.models.GitHub;
import inc.smart.solutions.imayaprofile.retrofit.interfaces.GetGitHubData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubApiManager {
    private static GitHubApiManager instance;
    private GetGitHubData getGitHubData;

    private GitHubApiManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Configs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getGitHubData = retrofit.create(GetGitHubData.class);
    }

    public static GitHubApiManager getInstance() {
        if (instance == null) {
            instance = new GitHubApiManager();
        }
        return instance;
    }

    public void getGitHubUser(Callback<GitHub> callback) {
        Call<GitHub> call = getGitHubData.getGitHubUser();
        call.enqueue(callback);
    }
}
