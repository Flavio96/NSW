package flavio.com.nsw.fragments;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Exercise;
import flavio.com.nsw.others.ExercisesCustomAdapter;
import flavio.com.nsw.others.GestioneDB;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExercisesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExercisesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExercisesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    Context context;
    GestioneDB db;
    ExercisesCustomAdapter adapter;

    public ExercisesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExercisesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExercisesFragment newInstance(String param1, String param2) {
        ExercisesFragment fragment = new ExercisesFragment();
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
        context = getActivity().getApplicationContext();
        final View view = inflater.inflate(R.layout.fragment_exercises, container, false);
        ListView list = view.findViewById(R.id.exercises_list);
        List<Exercise> exercises = new ArrayList<>();

//        while(c.moveToNext()){
//            Exercise exercise = new Exercise();
//            if(!c.getString(c.getColumnIndex(db.EXERCISE_name)).isEmpty()) {
//                exercise.setName(c.getString(c.getColumnIndex(db.EXERCISE_name)));
//            }
//            exercises.add(exercise);
//        }
        adapter = new ExercisesCustomAdapter(exercises, getActivity().getApplicationContext() );
        list.setAdapter(adapter);

        FloatingActionButton exFab = view.findViewById(R.id.add_exercise_fab);
        exFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_add_exercise);
                dialog.setTitle("");
                Button cancel, save;
                cancel = dialog.findViewById(R.id.ex_cancel);
                save = dialog.findViewById(R.id.ex_save);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText name, muscles, path;
                        name = dialog.findViewById(R.id.txt_ex_name);
                        muscles = dialog.findViewById(R.id.txt_muscles);
                        path = dialog.findViewById(R.id.txt_image_name);
                        String name_text, muscles_text, path_text;
                        if(isEmpty(name)){
                            name_text="";
                        }else{
                            name_text=name.getText().toString();
                        }
                        if(isEmpty(muscles)){
                            muscles_text="";
                        }else{
                            muscles_text=muscles.getText().toString();
                        }
                        if(isEmpty(path)){
                            path_text="";
                        }else{
                            path_text=path.getText().toString();
                        }
                        db.open();
                        db.insertExercise(name_text, muscles_text, path_text);
                        db.close();

                        ListView list = view.findViewById(R.id.exercises_list);
                        List exercises = new ArrayList();
                        db = new GestioneDB(context);
                        db.open();
                        Cursor c = db.getAllExercises();
                        while (c.moveToNext()) {
                            Exercise exercise = new Exercise();
                            if(!c.getString(c.getColumnIndex(db.EXERCISE_name)).isEmpty()) {
                                exercise.setName(c.getString(c.getColumnIndex(db.EXERCISE_name)));
                            }
                            exercises.add(exercise);
                        }
                        adapter = new ExercisesCustomAdapter(exercises, getActivity().getApplicationContext() );
                        list.setAdapter(adapter);

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        return view;
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


    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }
}
