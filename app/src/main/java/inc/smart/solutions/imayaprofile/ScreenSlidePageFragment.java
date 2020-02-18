package inc.smart.solutions.imayaprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class ScreenSlidePageFragment extends Fragment {
    private static String TAG = ScreenSlidePageFragment.class.getSimpleName();

    static ScreenSlidePageFragment newInstance(int position, String[] images) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putStringArray("images", images);
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String[] images = getArguments().getStringArray("images");
//        int position = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);


        return rootView;
    }
}
