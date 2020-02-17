package inc.smart.solutions.imayaprofile.retrofit.interfaces;

import inc.smart.solutions.imayaprofile.constants.Configs;
import inc.smart.solutions.imayaprofile.models.GitHub;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GetGitHubData {

    @GET(Configs.GitHubEndPoints.USER)
    Call<GitHub> getGitHubUser();
}
