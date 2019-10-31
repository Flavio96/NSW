package flavio.com.nsw.others;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Exercise;

public class SpinAdapter extends ArrayAdapter<Exercise> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private List<Exercise> values;


    private ArrayList<Exercise> items;
    private ArrayList<Exercise> itemsAll;
    private ArrayList<Exercise> suggestions;

    public SpinAdapter(Context context, int textViewResourceId,
                       List<Exercise> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
        this.items = new ArrayList<>();
        items.addAll(values);
        this.itemsAll = (ArrayList<Exercise>) items.clone();
        this.suggestions = new ArrayList<Exercise>();
    }

    @Override
    public int getCount(){
        return suggestions.size();
    }

    @Override
    public Exercise getItem(int position){
        return suggestions.get(position);
    }

    @Override
    public long getItemId(int position){
        return suggestions.get(position).getId();
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values.get(position).getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
*/

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.autocomplete_exercise_element, parent, false);
        }
        Exercise exercise = suggestions.get(position);
        if (exercise != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.exName);
            if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView Customer Name:"+customer.getName());
                customerNameLabel.setText(exercise.getName());
            }
        }
        return  v;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position).getName());

        return label;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Exercise)(resultValue)).getName();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Exercise exercise : itemsAll) {
                    if(exercise.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        suggestions.add(exercise);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Exercise> filteredList = (ArrayList<Exercise>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Exercise c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}