package flavio.com.nsw.others;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.RepsSets;
import flavio.com.nsw.data_models.Workout;
import flavio.com.nsw.fragments.HomeFragment;

public class RepsSetsCustomAdapter extends ArrayAdapter<RepsSets> implements View.OnClickListener{

    private List<RepsSets> dataSet;
    Context mContext;

    GestioneDB db;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName, txtSets;
    }

    public RepsSetsCustomAdapter(List<RepsSets> data, Context context) {
        super(context, R.layout.workout_exercise_element, data);
        this.dataSet = data;
        this.mContext=context;
        this.db = new GestioneDB(mContext);

    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        final RepsSets dataModel=(RepsSets)object;
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                // set message, title, and icon
                .setTitle("Remove")
                .setMessage("Do you want to remove the Exercise?")
                .setIcon(android.R.drawable.ic_menu_delete)

                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.deleteRepsSetsById(dataModel.getId());
                        dataSet.remove(dataModel);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .show();
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RepsSets dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.workout_exercise_element, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.exName);
            viewHolder.txtSets = convertView.findViewById(R.id.exReps);

            viewHolder.txtName.setText(dataModel.getExercise().getName());
            viewHolder.txtSets.setText("x"+dataModel.getReps());

            convertView.setTag(viewHolder);
        }
        lastPosition = position;

        // Return the completed view to render on screen
        return convertView;
    }
}