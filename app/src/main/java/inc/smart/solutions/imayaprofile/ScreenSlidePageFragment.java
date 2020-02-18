package inc.smart.solutions.imayaprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

public class ScreenSlidePageFragment extends Fragment {
    private static String TAG = ScreenSlidePageFragment.class.getSimpleName();
    private String image;

    static ScreenSlidePageFragment init(String image) {
        Bundle args = new Bundle();
        args.putString("image", image);
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = getArguments() != null ? getArguments().getString("image") : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        ImageView ivImage = rootView.findViewById(R.id.ivImage);
        Picasso.get()
                .load(image)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.error)
                .into(ivImage);

        return rootView;
    }
}
