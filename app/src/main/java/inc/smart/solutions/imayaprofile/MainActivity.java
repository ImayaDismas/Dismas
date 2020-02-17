package inc.smart.solutions.imayaprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import inc.smart.solutions.imayaprofile.adapter.GridViewAdapter;
import inc.smart.solutions.imayaprofile.models.Beanclass;
import inc.smart.solutions.imayaprofile.utils.ExpandableHeightGridView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ExpandableHeightGridView gridView;
    private int[] Image = {R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image5,R.drawable.image6};
    private ArrayList<Beanclass> beans;
    private GridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    @Override
    public void onClick(View view) {

    }
}
