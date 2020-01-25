package flavio.com.nsw.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Exercise;
import flavio.com.nsw.data_models.RepsSets;
import flavio.com.nsw.data_models.Workout;
import flavio.com.nsw.others.GestioneDB;

public class ExecutionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private int workoutId;
    private int exNumber;
    private int setNumber;
    private int paramTime;

    String m_Text = "";

    Long startTime;

    Chronometer chrono, totTime;
    Button btnPause, btnDone, btnQuit;
    TextView txtReps, txtExName;

    Workout workout;

    RepsSets exercise;

    GestioneDB db;

    List<Exercise> eList;
    List<RepsSets> exerciseList;

    XmlResourceParser parser;

    public ExecutionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_execution, container, false);

        Bundle args = getArguments();
        if(args != null){
            workoutId = args.getInt("workout_id");
            exNumber = args.getInt("ex_num");
            setNumber = args.getInt("set_num");
            paramTime = args.getInt("time");
        }else{
            WorkoutsFragment fragment = new WorkoutsFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }

        db = new GestioneDB(getActivity().getApplicationContext());
        db.open();

        setupView(view);

        totTime.setBase(SystemClock.elapsedRealtime()-paramTime);

        totTime.start();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTotTimeinMills();
                if(exerciseList.size()-1!=exNumber) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Reps / secs executed");

                    // Set up the input
                    final EditText input = new EditText(view.getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();

                            final AlertDialog.Builder dialogGetReady = new AlertDialog.Builder(view.getContext()).setTitle("REST").setMessage("" + exercise.getRest() + " seconds");

                            dialogGetReady.setPositiveButton("skip", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        ExecutionFragment fragment = new ExecutionFragment();
                                        Bundle arguments = new Bundle();
                                        arguments.putInt("workout_id", workoutId);
                                        arguments.putInt("ex_num", ++exNumber);
                                        arguments.putInt("set_num", setNumber);
                                        String t = totTime.getText().toString();
                                        String[] a = t.split(":");
                                        int min = Integer.parseInt(a[0]);
                                        int sec = Integer.parseInt(a[1]);
                                        arguments.putInt("time", ((min * 60) + (sec))*1000);
                                        fragment.setArguments(arguments);
                                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.frame, fragment);
                                        fragmentTransaction.commitAllowingStateLoss();
                                    }
                            });

                            final AlertDialog alert = dialogGetReady.create();
                            alert.show();

                            // Hide after some seconds
                            final Handler handler  = new Handler();
                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (alert.isShowing()) {
                                        alert.dismiss();

                                        ExecutionFragment fragment = new ExecutionFragment();
                                        Bundle arguments = new Bundle();
                                        arguments.putInt("workout_id", workoutId);
                                        arguments.putInt("ex_num", ++exNumber);
                                        arguments.putInt("set_num", setNumber);
                                        arguments.putInt("time", paramTime+(exercise.getRest()*1000));
                                        fragment.setArguments(arguments);



                                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.frame, fragment);
                                        fragmentTransaction.commitAllowingStateLoss();
                                    }
                                }
                            };

                            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    handler.removeCallbacks(runnable);

                                }
                            });

                            handler.postDelayed(runnable, (exercise.getRest()*1000));

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }else{
                    if(setNumber<workout.getSets()){
                        setNumber = setNumber+1;
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext()).setTitle("SET "+setNumber).setMessage("Starting in 3 seconds");

                        final AlertDialog alert = dialog.create();
                        alert.show();

                        // Hide after some seconds
                        final Handler handler  = new Handler();
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (alert.isShowing()) {
                                    alert.dismiss();
                                    ExecutionFragment fragment = new ExecutionFragment();
                                    Bundle arguments = new Bundle();
                                    arguments.putInt("workout_id", workoutId);
                                    arguments.putInt("ex_num", 0);
                                    arguments.putInt("set_num", setNumber);
                                    arguments.putInt("time", paramTime);
                                    fragment.setArguments(arguments);

                                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frame, fragment);
                                    fragmentTransaction.commitAllowingStateLoss();
                                }
                            }
                        };

                        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                handler.removeCallbacks(runnable);
                            }
                        });

                        handler.postDelayed(runnable, 3000);
                    }else{
                        quitWorkout(view);
                    }

                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnPause.getText().toString().equals("Pause")){
                    chrono.stop();
                    btnPause.setCompoundDrawablesWithIntrinsicBounds(v.getContext().getResources().getDrawable(android.R.drawable.ic_media_play), null, null, null);
                    btnPause.setText("Play");
                }else{
                    chrono.start();
                    btnPause.setCompoundDrawablesWithIntrinsicBounds(v.getContext().getResources().getDrawable(android.R.drawable.ic_media_pause), null, null, null);
                    btnPause.setText("Pause");
                }
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitWorkout(view);
            }
        });

        return view;
    }

    public void getTotTimeinMills(){
        String[] currTime = totTime.getText().toString().split(":");
        switch (currTime.length){
            case 1:
                paramTime = Integer.parseInt(currTime[0])+1000;
                break;
            case 2:
                paramTime = Integer.parseInt(currTime[0])*60*1000 + Integer.parseInt(currTime[1])*1000;
                break;
            case 3:
                paramTime = Integer.parseInt(currTime[0])*60*60*1000 + Integer.parseInt(currTime[1])*60*1000 + Integer.parseInt(currTime[2])+1000;
                break;
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Exercise findExerciseById(int id, List<Exercise> list) {
        for(Exercise e : list) {
            if(e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    protected void setupView (View view){
        workout = new Workout();
        Cursor c1 = db.findWorkoutById(workoutId);

        if(!c1.getString(c1.getColumnIndex(db.WORKOUT_ID)).isEmpty()) {
            workout.setId(c1.getInt(c1.getColumnIndex(GestioneDB.WORKOUT_ID)));
        }
        if(!c1.getString(c1.getColumnIndex(db.WORKOUT_sets)).isEmpty()) {
            workout.setSets(c1.getInt(c1.getColumnIndex(GestioneDB.WORKOUT_sets)));
        }
        if(!c1.getString(c1.getColumnIndex(db.WORKOUT_type)).isEmpty()) {
            workout.setType(c1.getString(c1.getColumnIndex(GestioneDB.WORKOUT_type)));
        }
        if(!c1.getString(c1.getColumnIndex(db.WORKOUT_name)).isEmpty()) {
            workout.setName(c1.getString(c1.getColumnIndex(GestioneDB.WORKOUT_name)));
        }

        Cursor c = db.findRepsSetsByWorkoutId(workoutId);
        exerciseList = new ArrayList<RepsSets>();

        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() ) {
            RepsSets rp = new RepsSets();
            eList = new ArrayList<>();
            parser = getResources().getXml(R.xml.exercises);
            try {
                eList = ExercisesFragment.processXMLData(parser, eList);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            rp.setExercise(findExerciseById(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_fk_exercise)),eList));
            rp.setId(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_ID)));
            rp.setReps(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_reps)));
            rp.setRest(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_rest)));
            rp.setSets(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_sets)));
            exerciseList.add(rp);
        }

        exercise = exerciseList.get(exNumber);

        chrono = view.findViewById(R.id.chronometer);
        txtReps = view.findViewById(R.id.txtReps);
        txtExName = view.findViewById(R.id.txtExName);
        totTime = view.findViewById(R.id.totTime);
        btnDone = view.findViewById(R.id.btnDone);
        btnPause = view.findViewById(R.id.btnPause);
        btnQuit = view.findViewById(R.id.btnQuit);

        if(exercise.getExercise().getType().equals("r")){
            txtReps.setVisibility(View.VISIBLE);
            chrono.setVisibility(View.INVISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
            txtReps.setText("x"+exercise.getReps());
            if(exercise.getReps()==0)
                txtReps.setText("MAX");
        }else{
            txtReps.setVisibility(View.GONE);
            chrono.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.VISIBLE);
            chrono.setBase(SystemClock.elapsedRealtime()+(exercise.getReps()*1000+1000));
            chrono.start();
        }

        txtExName.setText(exercise.getExercise().getName());
    }

    public void quitWorkout(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        alert.setTitle("Finish Workout");
        alert.setMessage("Are you sure you want to quit?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                WorkoutDetailFragment fragment = new WorkoutDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putInt("workout_id" , workoutId);
                fragment.setArguments(arguments);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.addToBackStack("STACK");
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }


}
