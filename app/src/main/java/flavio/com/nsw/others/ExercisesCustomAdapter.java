package flavio.com.nsw.others;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Exercise;

public class ExercisesCustomAdapter extends ArrayAdapter<Exercise> implements View.OnClickListener{

    private List<Exercise> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        RelativeLayout bg;
        TextView txtName, txtMuscles, txtPath;
    }

    public ExercisesCustomAdapter(List<Exercise> data, Context context) {
        super(context, R.layout.exercises_list_element, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Exercise dataModel=(Exercise)object;

        /*switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }*/
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Exercise dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.exercises_list_element, parent, false);
            viewHolder.bg = convertView.findViewById(R.id.bg);
            viewHolder.txtName = convertView.findViewById(R.id.e_name);
            viewHolder.txtMuscles = convertView.findViewById(R.id.e_muscles);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        lastPosition = position;

        if(position%2 == 0)
            viewHolder.bg.setBackgroundColor(Color.parseColor("#fef2e8"));
        else
            viewHolder.bg.setBackgroundColor(Color.parseColor("#ffffff"));
        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtMuscles.setText(dataModel.getMuscles());


        // Return the completed view to render on screen
        return convertView;
    }
}