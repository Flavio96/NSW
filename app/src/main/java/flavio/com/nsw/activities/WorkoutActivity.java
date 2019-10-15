package flavio.com.nsw.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import flavio.com.nsw.R;
import flavio.com.nsw.data_models.Exercise;
import flavio.com.nsw.data_models.RepsSets;
import flavio.com.nsw.data_models.RepsSets_Exercises;
import flavio.com.nsw.data_models.Workout;
import flavio.com.nsw.others.GestioneDB;

public class WorkoutActivity extends AppCompatActivity {

    GestioneDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        db = new GestioneDB(this);
        int counter =0;

        final Intent i = getIntent();
        int idWorkout = i.getIntExtra("idWorkout", -1);
        Cursor c = db.findWorkoutById(idWorkout);
        Workout w = new Workout();
        if(c.moveToFirst()) {
            w.setId(c.getInt(c.getColumnIndex(db.WORKOUT_ID)));
            w.setName(c.getString(c.getColumnIndex(db.WORKOUT_name)));
            w.setType(c.getString(c.getColumnIndex(db.WORKOUT_type)));
            w.setSets(c.getInt(c.getColumnIndex(db.WORKOUT_sets)));

            Cursor c_ex = db.findRepsSetsByWorkoutId(w.getId());
            List<RepsSets_Exercises> exercises = new ArrayList<RepsSets_Exercises>();
            while(c_ex.moveToNext()){
                RepsSets_Exercises rs_ex = new RepsSets_Exercises();
                Exercise ex = new Exercise();
                RepsSets rs = new RepsSets();
                ex.setId(c.getInt(c.getColumnIndex(db.EXERCISE_ID)));
                ex.setName(c.getString(c.getColumnIndex(db.EXERCISE_name)));
                ex.setMuscles(c.getString(c.getColumnIndex(db.EXERCISE_muscles)));
                ex.setImage_name(c.getString(c.getColumnIndex(db.EXERCISE_img)));
                rs.setId(c.getInt(c.getColumnIndex(db.REPS_SETS_ID)));
                rs.setReps(c.getInt(c.getColumnIndex(db.REPS_SETS_reps)));
                rs.setRest(c.getInt(c.getColumnIndex(db.REPS_SETS_rest)));
                rs.setSets(c.getInt(c.getColumnIndex(db.REPS_SETS_sets)));
                rs_ex.setExercise(ex);
                rs_ex.setReps_sets(rs);
                exercises.add(rs_ex);
            }

            counter = 0;
            final TextView current, next, reps, time;
            current = findViewById(R.id.current_ex);
            next = findViewById(R.id.next_ex);
            time = findViewById(R.id.workout_time);
            reps = findViewById(R.id.reps);

            current.setText(exercises.get(counter).getExercise().getName());
            next.setText(exercises.get(counter+1).getExercise().getName());
            reps.setText(exercises.get(counter).getReps_sets().getReps());

            final Intent backToMain = new Intent(this, MainActivity.class);
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Start workout")
                    .setMessage("Let's start")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            new CountDownTimer(30000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    time.setText("seconds remaining: " + millisUntilFinished / 1000);
                                }

                                public void onFinish() {
                                    time.setText("done!");
                                }
                            }.start();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(backToMain);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }else{
            Toast.makeText(this, "No workouts finded with this ID", Toast.LENGTH_LONG).show();
        }


    }
}
