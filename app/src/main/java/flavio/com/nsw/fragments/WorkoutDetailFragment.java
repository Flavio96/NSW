package flavio.com.nsw.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Exercise;
import flavio.com.nsw.data_models.RepsSets;
import flavio.com.nsw.others.GestioneDB;
import flavio.com.nsw.others.RepsSetsCustomAdapter;
import flavio.com.nsw.others.SpinAdapter;

public class WorkoutDetailFragment extends Fragment {

    GestioneDB db;
    Integer workoutId;

    FloatingActionButton fab, fab1, fab2, fab3;

    boolean isFABOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_workout_detail,container,false);

        fab = view.findViewById(R.id.fab);
        fab1 = view.findViewById(R.id.fab1);
        fab2 = view.findViewById(R.id.fab2);
        fab3 = view.findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        Bundle args = getArguments();
        if(args != null){
            workoutId = args.getInt("workout_id");
        }else{
            WorkoutsFragment fragment = new WorkoutsFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }

        db = new GestioneDB(getActivity().getApplicationContext());
        db.open();

        Cursor c = db.findRepsSetsByWorkoutId(workoutId);
        List<RepsSets> exerciseList = new ArrayList<RepsSets>();
        List<Exercise> eList = new ArrayList<>();

        XmlResourceParser parser = getResources().getXml(R.xml.exercises);
        try {
            eList = ExercisesFragment.processXMLData(parser, eList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        while (c.moveToNext()){
            RepsSets rp = new RepsSets();
            rp.setExercise(findExerciseById(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_fk_exercise)),eList));
            rp.setId(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_ID)));
            rp.setReps(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_reps)));
            rp.setRest(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_rest)));
            rp.setSets(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_sets)));
            exerciseList.add(rp);
        }

        ListView exercises = view.findViewById(R.id.woExercises);
        RepsSetsCustomAdapter aa = new RepsSetsCustomAdapter(exerciseList, view.getContext());
        exercises.setAdapter(aa);

        final List<Exercise> finalEList = eList;
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(view.getContext());
                d.setContentView(R.layout.dialog_add_exercise);

                final Spinner s = d.findViewById(R.id.ex_spinner);
                SpinAdapter aa = new SpinAdapter
                        (view.getContext(), android.R.layout.simple_spinner_dropdown_item, finalEList);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                s.setAdapter(aa);

                Button cancel, add;
                cancel = d.findViewById(R.id.ex_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                add = d.findViewById(R.id.ex_add);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = (int) s.getSelectedItemId();
                        EditText rest = d.findViewById(R.id.ex_rest);
                        EditText reps = d.findViewById(R.id.ex_reps);
                        int repsNum = Integer.parseInt(reps.getText().toString());
                        int restNum = Integer.parseInt(rest.getText().toString());

                        db.insertRepsSets(repsNum, 0, restNum, id, workoutId);
                        d.dismiss();
                    }
                });
                d.show();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
                        // set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Do you want to Delete?")
                        .setIcon(android.R.drawable.ic_menu_delete)

                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.deleteRepsSetsByWorkoutId(workoutId);
                                db.deleteWorkout(workoutId);
                                HomeFragment fragment = new HomeFragment();
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frame, fragment);
                                fragmentTransaction.commitAllowingStateLoss();
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
        });

        return view;
    }

    Exercise findExerciseById(int id, List<Exercise> list) {
        for(Exercise e : list) {
            if(e.getId() == id) {
                return e;
            }
        }
        return null;
    }


    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_180));
        fab.animate().rotation(315);
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        fab.animate().rotation(0);
    }

}
