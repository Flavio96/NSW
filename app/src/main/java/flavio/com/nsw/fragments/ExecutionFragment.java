package flavio.com.nsw.fragments;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExecutionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExecutionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExecutionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private int workoutId;
    private int exNumber;
    private int paramTime;

    Long startTime;

    Chronometer chrono, totTime;
    Button btnPause, btnDone;
    TextView txtReps;

    Workout workout;

    RepsSets exercise;

    GestioneDB db;

    List<Exercise> eList;
    List<RepsSets> exerciseList;

    XmlResourceParser parser;

    public ExecutionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExecutionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExecutionFragment newInstance(String param1, String param2) {
        ExecutionFragment fragment = new ExecutionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
            paramTime = args.getInt("time");
        }else{
            WorkoutsFragment fragment = new WorkoutsFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }

        db = new GestioneDB(getActivity().getApplicationContext());
        db.open();
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

        while (c.moveToNext()){
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
        totTime = view.findViewById(R.id.totTime);
        btnDone = view.findViewById(R.id.btnDone);
        btnPause = view.findViewById(R.id.btnPause);

        if(exercise.getExercise().getType().equals("r")){
            txtReps.setVisibility(View.VISIBLE);
            chrono.setVisibility(View.INVISIBLE);
        }else{
            txtReps.setVisibility(View.GONE);
            chrono.setVisibility(View.VISIBLE);
        }

        totTime.setBase(SystemClock.elapsedRealtime()-paramTime);

        totTime.start();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTotTimeinMills();
                ExecutionFragment fragment = new ExecutionFragment();
                Bundle arguments = new Bundle();
                arguments.putInt("workout_id" , workoutId);
                arguments.putInt("time" , paramTime);
                fragment.setArguments(arguments);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.commitAllowingStateLoss();
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

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    Exercise findExerciseById(int id, List<Exercise> list) {
        for(Exercise e : list) {
            if(e.getId() == id) {
                return e;
            }
        }
        return null;
    }

}
