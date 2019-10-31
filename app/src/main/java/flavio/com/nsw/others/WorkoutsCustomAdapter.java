package flavio.com.nsw.others;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Workout;

public class WorkoutsCustomAdapter extends ArrayAdapter<Workout> implements View.OnClickListener{

    private List<Workout> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName, txtType, txtSets;
    }

    public WorkoutsCustomAdapter(List<Workout> data, Context context) {
        super(context, R.layout.workouts_list_element, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Workout dataModel=(Workout)object;

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
        Workout dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.workouts_list_element, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.w_name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.w_type);
            viewHolder.txtSets = (TextView) convertView.findViewById(R.id.w_sets);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtType.setText(dataModel.getType());
        viewHolder.txtSets.setText("SETS: "+dataModel.getSets());
        // Return the completed view to render on screen
        return convertView;
    }
}