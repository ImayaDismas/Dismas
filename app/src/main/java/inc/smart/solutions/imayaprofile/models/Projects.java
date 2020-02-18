package inc.smart.solutions.imayaprofile.models;

public class Projects {

    private String projectName;
    private String projectBanner;
    private String[] projectScreenshots;

    public Projects(String projectName, String projectBanner, String[] projectScreenshots) {
        this.projectName = projectName;
        this.projectBanner = projectBanner;
        this.projectScreenshots = projectScreenshots;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectBanner() {
        return projectBanner;
    }

    public void setProjectBanner(String projectBanner) {
        this.projectBanner = projectBanner;
    }

    public String[] getProjectScreenshots() {
        return projectScreenshots;
    }

    public void setProjectScreenshots(String[] projectScreenshots) {
        this.projectScreenshots = projectScreenshots;
    }
}

