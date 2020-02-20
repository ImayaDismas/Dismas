package inc.smart.solutions.dismas.retrofit.interfaces;

import inc.smart.solutions.dismas.constants.Configs;
import inc.smart.solutions.dismas.models.GitHub;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GetGitHubData {

    @GET(Configs.GitHubEndPoints.USER)
    Call<GitHub> getGitHubUser();
}
