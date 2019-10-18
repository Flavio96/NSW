package flavio.com.nsw.fragments;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Workout;
import flavio.com.nsw.others.GestioneDB;
import flavio.com.nsw.others.ViewPagerAdapter;
import flavio.com.nsw.others.WorkoutsCustomAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener {

    FloatingActionButton fab, fab1, fab2;

    boolean isFABOpen = false;

    Context context;

    GestioneDB db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewPagerAdapter myPagerAdapter;

    public ViewPager viewPager;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = view.findViewById(R.id.viewpager);
        myPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        myPagerAdapter.addFragment(new WorkoutsFragment(), "Workouts");
        myPagerAdapter.addFragment(new GoalsFragment(), "Goals");
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        fab = view.findViewById(R.id.fab);
        fab1 = view.findViewById(R.id.fab1);
        fab2 = view.findViewById(R.id.fab2);
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

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_add_workout);
                dialog.setTitle("Create a Workout");
                Button cancel, save;
                cancel = dialog.findViewById(R.id.cancel);
                save = dialog.findViewById(R.id.save);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText name, type;
                        name = dialog.findViewById(R.id.txt_name);
                        type = dialog.findViewById(R.id.txt_type);
                        String name_text, type_text;
                        if(isEmpty(name)){
                            name_text="";
                        }else{
                            name_text=name.getText().toString();
                        }
                        if(isEmpty(type)){
                            type_text="";
                        }else{
                            type_text=type.getText().toString();
                        }
                        db = new GestioneDB(context);
                        db.open();
                        db.insertWorkout(name_text, type_text, 1);

                        ListView list = view.findViewById(R.id.workouts_list);
                        List<Workout> workouts = new ArrayList();
                        Cursor c = db.getAllWorkouts();
                        while (c.moveToNext()) {
                            Workout workout = new Workout();
                            if(c.getInt(c.getColumnIndex(db.WORKOUT_ID))>=0) {
                                workout.setId(c.getInt(c.getColumnIndex(db.WORKOUT_ID)));
                            }
                            if(!c.getString(c.getColumnIndex(db.WORKOUT_name)).isEmpty()) {
                                workout.setName(c.getString(c.getColumnIndex(db.WORKOUT_name)));
                            }
                            if(!c.getString(c.getColumnIndex(db.WORKOUT_type)).isEmpty()) {
                                workout.setType(c.getString(c.getColumnIndex(db.WORKOUT_type)));
                            }
                            workout.setSets(c.getInt(c.getColumnIndex(db.WORKOUT_sets)));
                            workouts.add(workout);
                        }
                        WorkoutsCustomAdapter aa = new WorkoutsCustomAdapter(workouts, getActivity().getApplicationContext());
                        list.setAdapter(aa);
                        db.close();
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
        myPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        myPagerAdapter.addFragment(new WorkoutsFragment(), "Workouts");
        myPagerAdapter.addFragment(new GoalsFragment(), "Goals");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

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


    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab.animate().rotation(315);
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab.animate().rotation(0);
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }
}
