package flavio.com.nsw.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Workout;
import flavio.com.nsw.others.GestioneDB;
import flavio.com.nsw.others.WorkoutsCustomAdapter;

public class WorkoutsFragment extends Fragment {

    GestioneDB db;
    private WorkoutsCustomAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workouts,container,false);
        ListView list = view.findViewById(R.id.workouts_list);

        final List<Workout> workouts = new ArrayList<>();

        db = new GestioneDB(getActivity().getApplicationContext());
        db.open();
        Cursor c = db.getAllWorkouts();
        while (c.moveToNext()) {
            Workout workout = new Workout();
            if(!c.getString(c.getColumnIndex(db.WORKOUT_name)).isEmpty()) {
                workout.setName(c.getString(c.getColumnIndex(db.WORKOUT_name)));
            }
            if(!c.getString(c.getColumnIndex(db.WORKOUT_type)).isEmpty()) {
                workout.setType(c.getString(c.getColumnIndex(db.WORKOUT_type)));
            }
            workout.setSets(c.getInt(c.getColumnIndex(db.WORKOUT_sets)));
            workouts.add(workout);
        }

        adapter = new WorkoutsCustomAdapter(workouts, getActivity().getApplicationContext());
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WorkoutDetailFragment fragment = new WorkoutDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putInt("workout_id" , workouts.get(position).getId());
                fragment.setArguments(arguments);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

/*
        final TextView txt = view.findViewById(R.id.textView);
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                txt.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                txt.setText("done!");
            }
        }.start();
*/
        return view;
    }
}
