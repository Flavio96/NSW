package flavio.com.nsw.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import flavio.com.nsw.DragNDrop.DragListener;
import flavio.com.nsw.DragNDrop.DragNDropAdapter;
import flavio.com.nsw.DragNDrop.DragNDropListView;
import flavio.com.nsw.DragNDrop.DropListener;
import flavio.com.nsw.DragNDrop.RemoveListener;
import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Exercise;
import flavio.com.nsw.data_models.RepsSets;
import flavio.com.nsw.data_models.Workout;
import flavio.com.nsw.others.GestioneDB;
import flavio.com.nsw.others.RepsSetsCustomAdapter;
import flavio.com.nsw.others.SpinAdapter;

import static android.view.View.GONE;
import static android.widget.AdapterView.INVALID_POSITION;

public class WorkoutDetailFragment extends Fragment {

    GestioneDB db;
    Integer workoutId;
    Workout workout;

    FloatingActionButton fab, fab1, fab2, fab3;

    List<Exercise> eList;

    ListView exercises;

    TextView sets, name;

    SeekBar scrollControl;

    boolean isFABOpen = false;

    boolean mDragMode;

    int mStartPosition;
    int mEndPosition;
    int mDragPointOffset;

    ImageView mDragView;
    List<RepsSets> exerciseList;

    Context ctx;
    AutoCompleteTextView autotxt;

    XmlResourceParser parser;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_workout_detail,container,false);

        ctx = view.getContext();

        mDragView = view.findViewById(R.id.handler);
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

        eList = new ArrayList<>();
        parser = getResources().getXml(R.xml.exercises);
        try {
            eList = ExercisesFragment.processXMLData(parser, eList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        Cursor c = db.findRepsSetsByWorkoutId(workoutId);
        exerciseList = new ArrayList<RepsSets>();
        exercises = view.findViewById(R.id.woExercises);

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

        RepsSetsCustomAdapter aa = new RepsSetsCustomAdapter(view.getContext(),
                exerciseList);
        //exercises.setAdapter(aa);

        ArrayList<String> content = new ArrayList<String>(exerciseList.size());
        for(RepsSets rs : exerciseList){
            content.add(rs.getExercise().getName()+"&&"
            +rs.getReps() + "&&"
            +rs.getId() + "&&"
            +rs.getExercise().getType());

        }
//        for (int i=0; i < mListContent.length; i++) {
//            content.add(mListContent[i]);
//        }

        exercises.setAdapter(new DragNDropAdapter(view.getContext(), new int[]{R.layout.workout_exercise_element}, new int[]{R.id.exName, R.id.exReps, R.id.exId}, content));//new DragNDropAdapter(this,content)
        //exercises.setAdapter(new RepsSetsCustomAdapter(view.getContext(), exerciseList));
        exercises.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchEvent(event);
            }
        });

        final List<RepsSets> finalExList = exerciseList;
       /* exercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
                        // set message, title, and icon
                        .setTitle("Remove")
                        .setMessage("Do you want to remove the Exercise?")
                        .setIcon(android.R.drawable.ic_menu_delete)

                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.deleteRepsSetsById(finalExList.get(position).getId());
                                refreshList(view);
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
        });*/

        sets = view.findViewById(R.id.woSets);
        name = view.findViewById(R.id.woTitle);
        name.setText(workout.getName());
        sets.setText("SETS: "+workout.getSets());
        sets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Sets");

                // Set up the input
                final EditText input = new EditText(view.getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!input.getText().toString().isEmpty()){
                            db.updateWorkoutSets(workoutId, Integer.parseInt(input.getText().toString()));
                            sets.setText("SETS: "+input.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(view.getContext());
                d.setContentView(R.layout.dialog_add_exercise);

                //final Spinner s = d.findViewById(R.id.ex_spinner);
                autotxt = d.findViewById(R.id.autoCompleteTextView);
                SpinAdapter aa = new SpinAdapter
                        (view.getContext(), android.R.layout.simple_spinner_dropdown_item, eList);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                autotxt.setAdapter(aa);

                autotxt.setThreshold(2);

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
                        String input = autotxt.getText().toString();
                        Exercise ex = findExerciseByName(input, eList);
                        if(ex!=null) {
                            EditText rest = d.findViewById(R.id.ex_rest);
                            EditText reps = d.findViewById(R.id.ex_reps);
                            int repsNum = Integer.parseInt(reps.getText().toString());
                            int restNum = Integer.parseInt(rest.getText().toString());
                            int pos = 0;
                            if (exerciseList.size() > 0)
                                pos = exerciseList.size() - 1;
                            db.insertRepsSets(repsNum, pos, restNum, ex.getId(), workoutId);
                            d.dismiss();
                            refreshList(view);
                        }else{
                            autotxt.setError("Invalid input");
                        }
                    }
                });
                d.show();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutionFragment fragment = new ExecutionFragment();
                Bundle arguments = new Bundle();
                arguments.putInt("workout_id" , workoutId);
                arguments.putInt("ex_num", 0);
                fragment.setArguments(arguments);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.addToBackStack("STACK");
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
                        // set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Do you want to Delete the Workout?")
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

        scrollControl = view.findViewById(R.id.scrollControl);
        setScrollControl();

        return view;
    }


    public void setScrollControl (){


        if(exercises.getCount()>0){
            int last = exercises.getLastVisiblePosition();
            if(last == exercises.getCount() - 1) {
                // It fits!
                scrollControl.setVisibility(GONE);
            }
            else {
                // It doesn't fit...
                scrollControl.setVisibility(View.VISIBLE);
                final int pages = exercises.getCount() - 1 / last;
                scrollControl.setMax(pages);
                scrollControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if(b){
                            if(i<pages && i>0)
                                exercises.smoothScrollToPosition((int)exerciseList.size()-1/i);
                            else
                                if(i == 0)
                                    exercises.smoothScrollToPosition(0);
                                else
                                    exercises.smoothScrollToPosition(exerciseList.size()-1);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

            }
        }else{
            scrollControl.setVisibility(GONE);
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN && x < exercises.getWidth()/4) {
            mDragMode = true;
        }
        mDragMode = false;
        if (!mDragMode)

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartPosition = exercises.pointToPosition(x,y);
                if (mStartPosition != INVALID_POSITION) {
                    int mItemPosition = mStartPosition - exercises.getFirstVisiblePosition();
                    mDragPointOffset = y - exercises.getChildAt(mItemPosition).getTop();
                    mDragPointOffset -= ((int)ev.getRawY()) - y;
                    startDrag(mItemPosition,y);
                    drag(0,y);// replace 0 with x if desired
                }
                break;
            case MotionEvent.ACTION_MOVE:
                drag(0,y);// replace 0 with x if desired
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                mDragMode = false;
                mEndPosition = exercises.pointToPosition(x,y);
                stopDrag(mStartPosition - exercises.getFirstVisiblePosition());
                if (mDropListener != null && mStartPosition != INVALID_POSITION && mEndPosition != INVALID_POSITION) {
                    mDropListener.onDrop(mStartPosition, mEndPosition);
                    db.open();
                    RepsSets a = exerciseList.get(mStartPosition);
                    RepsSets b = exerciseList.get(mEndPosition);
                    db.updateRepsSetsOrder(a.getId(), b.getId(), mStartPosition, mEndPosition);
                    a.setSets(mEndPosition);
                    b.setSets(mStartPosition);
                    exerciseList.set(mStartPosition, b);
                    exerciseList.set(mEndPosition, a);
                }
                break;
        }
        return true;
    }

    // move the drag view
    private void drag(int x, int y) {
        if (mDragView != null) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
            layoutParams.x = x;
            layoutParams.y = y - mDragPointOffset;
            WindowManager mWindowManager = (WindowManager) getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.updateViewLayout(mDragView, layoutParams);

            if (mDragListener != null)
                mDragListener.onDrag(x, y, null);// change null to "this" when ready to use
        }
    }

    // enable the drag view for dragging
    private void startDrag(int itemIndex, int y) {
        stopDrag(itemIndex);

        View item = exercises.getChildAt(itemIndex);
        if (item == null) return;
        item.setDrawingCacheEnabled(true);
        if (mDragListener != null)
            mDragListener.onStartDrag(item);

        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = y - mDragPointOffset;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);
        v.setImageBitmap(bitmap);

        WindowManager mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }

    // destroy drag view
    private void stopDrag(int itemIndex) {
        if (mDragView != null) {
            if (mDragListener != null)
                mDragListener.onStopDrag(exercises.getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
    }

    Exercise findExerciseById(int id, List<Exercise> list) {
        for(Exercise e : list) {
            if(e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    Exercise findExerciseByName(String name, List<Exercise> list) {
        for(Exercise e : list) {
            if(e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    private void refreshList(View view){
        /*Cursor c = db.findRepsSetsByWorkoutId(workoutId);
        List<RepsSets> exerciseList = new ArrayList<RepsSets>();

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
            rp.setExercise(findExerciseById(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_fk_exercise)), eList));
            rp.setId(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_ID)));
            rp.setReps(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_reps)));
            rp.setRest(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_rest)));
            rp.setSets(c.getInt(c.getColumnIndex(GestioneDB.REPS_SETS_sets)));
            exerciseList.add(rp);
        }

        RepsSetsCustomAdapter aa = new RepsSetsCustomAdapter(view.getContext(), exerciseList);
        exercises.setAdapter(aa);*/

        WorkoutDetailFragment fragment = new WorkoutDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("workout_id" , workoutId);
        fragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commitAllowingStateLoss();
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


    private DropListener mDropListener =
            new DropListener() {
                public void onDrop(int from, int to) {
                    ListAdapter adapter = exercises.getAdapter();
                    if (adapter instanceof DragNDropAdapter) {
                        ((DragNDropAdapter)adapter).onDrop(from, to);
                        exercises.invalidateViews();
                    }
                }
            };

    private RemoveListener mRemoveListener =
            new RemoveListener() {
                public void onRemove(int which) {
                    ListAdapter adapter = exercises.getAdapter();
                    if (adapter instanceof DragNDropAdapter) {
                        ((DragNDropAdapter)adapter).onRemove(which);
                        exercises.invalidateViews();
                    }
                }
            };


    private DragListener mDragListener =
            new DragListener() {

                int backgroundColor = 0xe0103010;
                int defaultBackgroundColor;

                public void onDrag(int x, int y, ListView listView) {
                    // TODO Auto-generated method stub
                }

                public void onStartDrag(View itemView) {
                    itemView.setVisibility(View.INVISIBLE);
                    defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
                    itemView.setBackgroundColor(backgroundColor);
                }

                public void onStopDrag(View itemView) {
                    if(itemView != null) {
                        itemView.setVisibility(View.VISIBLE);
                        itemView.setBackgroundColor(defaultBackgroundColor);
                    }
                }

            };

    private static String[] mListContent={"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7"};
}
