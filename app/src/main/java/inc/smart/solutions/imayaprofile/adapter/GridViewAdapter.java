package inc.smart.solutions.imayaprofile.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import inc.smart.solutions.imayaprofile.R;
import inc.smart.solutions.imayaprofile.models.Projects;

public class GridViewAdapter extends BaseAdapter {

    Context context;
    ArrayList<Projects> projects;

    public GridViewAdapter(Context context, ArrayList<Projects> projects) {
        this.projects = projects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public Object getItem(int position) {
        return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            viewHolder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.gridview_item, null);

            viewHolder.ivBanner = convertView.findViewById(R.id.ivBanner);
            viewHolder.tvAppName = convertView.findViewById(R.id.tvAppName);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }


        Projects projects = (Projects) getItem(position);

        Glide.with(context)
                .load(projects.getProjectBanner())
                .placeholder(R.drawable.pre_loader)
                .into(viewHolder.ivBanner);

        viewHolder.tvAppName.setText(projects.getProjectName());
        return convertView;
    }

    private class ViewHolder {
        ImageView ivBanner;
        TextView tvAppName;
    }

}