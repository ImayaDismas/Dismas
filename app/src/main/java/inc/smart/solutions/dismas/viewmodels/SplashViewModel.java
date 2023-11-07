package inc.smart.solutions.dismas.viewmodels;

import androidx.lifecycle.ViewModel;

public class SplashViewModel extends ViewModel {
    private boolean ready = false;

    public SplashViewModel() {

    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}