package flavio.com.nsw.others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class GestioneDB {

    /*
    Definisco una serie di costanti
    */
    public static final String WORKOUT_ID = "workout_id";
    public static final String WORKOUT_name = "name";
    public static final String WORKOUT_type = "type";
    public static final String WORKOUT_sets = "sets";
    public static final String EXERCISE_ID = "exercise_id";
    public static final String EXERCISE_name = "name";
    public static final String EXERCISE_muscles = "muscles";
    public static final String EXERCISE_img = "img";
    public static final String REPS_SETS_ID = "reps_sets_id";
    public static final String REPS_SETS_reps = "reps";
    public static final String REPS_SETS_sets = "sets";
    public static final String REPS_SETS_rest = "rest";
    public static final String REPS_SETS_fk_workout = "fk_workout";
    public static final String REPS_SETS_fk_exercise = "fk_exercise";
    public static final String GOAL_ID = "goal_id";
    public static final String GOAL_reps = "reps";
    public static final String GOAL_fk_exercise = "fk_exercise";
    public static final String TAG = "GestioneDB";
    public static final String DATABASE_NOME = "NSWdb";
    public static final String WORKOUT_TABLE = "workout";
    public static final String EXERCISE_TABLE = "exercise";
    public static final String REPS_SETS_TABLE = "reps_sets";
    public static final String GOAL_TABLE = "goal";
    static final int DATABASE_VERSIONE = 3;

    /*
    Creo una costante contenente la query per la creazione del database
    */
    static final String CREATE_TABLE_WORKOUT = "create table workout (workout_id INTEGER primary key, name text, type text, sets INTEGER);";
    static final String CREATE_TABLE_EXERCISE = "create table exercise (exercise_id integer primary key, name text not null, muscles text, img text);";
    static final String CREATE_TABLE_REPS_SETS = "create table reps_sets (reps_sets_id INTEGER primary key, reps INTEGER, sets INTEGER, rest INTEGER, fk_workout INTEGER, fk_exercise INTEGER);";
    static final String CREATE_TABLE_GOAL = "create table goal (goal_id INTEGER primary key, reps INTEGER, fk_exercise INTEGER);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    /*
    Costruttore
    */
    public GestioneDB(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    /*
    Estendo la classe SQLiteOpenHelper che si occupa
    della gestione delle connessioni e della creazione del DB
    */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            // invoco il costruttore della classe base  Environment.getExternalStorageDirectory().getPath()+
            //"/mnt/sdcard/Apps_database/nomeDB.db"
            super(context, "/mnt/sdcard/Apps_database/NSWDB.db", null, DATABASE_VERSIONE);
            //super(context,"NSWDB", null, DATABASE_VERSIONE);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_WORKOUT);
                //db.execSQL(CREATE_TABLE_EXERCISE);
                db.execSQL(CREATE_TABLE_REPS_SETS);
                db.execSQL(CREATE_TABLE_GOAL);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        }
    }

    /*
    Apro la connessione al DB
    */
    public GestioneDB open() throws SQLException {
        // ottengo accesso al DB anche in scrittura
        db = DBHelper.getWritableDatabase();
        return this;
    }

    /*
    Chiudo la connessione al DB
    */
    public void close() {
        // chiudo la connessione al DB
        DBHelper.close();
    }

    /*
    Estraggo elenco di tutti i workout
    */
    public Cursor getAllWorkouts() {
        // applico il metodo query senza applicare nessuna clausola WHERE
        return db.query(WORKOUT_TABLE, new String[]{WORKOUT_ID, WORKOUT_name, WORKOUT_type, WORKOUT_sets}, null, null, null, null, WORKOUT_name + " COLLATE NOCASE");
    }

    /*
    Estraggo elenco di tutti gli esercizi
    */
    public Cursor getAllExercises() {
        // applico il metodo query senza applicare nessuna clausola WHERE
        return db.query(EXERCISE_TABLE, new String[]{EXERCISE_ID, EXERCISE_name, EXERCISE_muscles, EXERCISE_img}, null, null, null, null, EXERCISE_name + " COLLATE NOCASE");
    }

    /*
    Estraggo elenco di tutte le relazioni workout-exercise
    */
    public Cursor getAllRepsSets() {
        // applico il metodo query senza applicare nessuna clausola WHERE
        return db.query(REPS_SETS_TABLE, new String[]{REPS_SETS_ID, REPS_SETS_reps, REPS_SETS_sets, REPS_SETS_rest, REPS_SETS_fk_exercise, REPS_SETS_fk_workout}, null, null, null, null, REPS_SETS_ID + " COLLATE NOCASE");
    }

    /*
    Estraggo elenco di tutti i goal
    */
    public Cursor getAllGoals() {
        // applico il metodo query senza applicare nessuna clausola WHERE
        return db.query(GOAL_TABLE, new String[]{GOAL_ID, GOAL_reps, GOAL_fk_exercise}, null, null, null, null, GOAL_ID + " COLLATE NOCASE");
    }

   /* public boolean cancellaTutto() {
        return db.delete(DATABASE_TABELLA, null, null) > 0;
    }*/


    public Cursor findWorkoutByName(String name) throws SQLException {
        // applico il metodo query filtrando per ID
        Cursor mCursore = db.query(true, WORKOUT_TABLE, new String[]{WORKOUT_ID, WORKOUT_name, WORKOUT_sets}, WORKOUT_name + "='" + name + "'", null, null, null, null, null);
        if (mCursore != null) {
            mCursore.moveToFirst();
        }
        return mCursore;
    }


    public Cursor findWorkoutById(int id) throws SQLException {
        // applico il metodo query filtrando per ID
        Cursor mCursore = db.query(true, WORKOUT_TABLE, new String[]{WORKOUT_ID, WORKOUT_name, WORKOUT_sets}, WORKOUT_ID + "=" + id, null, null, null, null, null);
        if (mCursore != null) {
            mCursore.moveToFirst();
        }
        return mCursore;
    }

    public Cursor findExerciseByName(String name) throws SQLException {
        // applico il metodo query filtrando per nome
        Cursor mCursore = db.query(true, EXERCISE_TABLE, new String[]{EXERCISE_ID, EXERCISE_name, EXERCISE_muscles, EXERCISE_img}, EXERCISE_name + "='" + name + "'", null, null, null, null, null);
        if (mCursore != null) {
            mCursore.moveToFirst();
        }
        return mCursore;
    }


    public Cursor findExerciseById(int id) throws SQLException {
        // applico il metodo query filtrando per ID
        Cursor mCursore = db.query(true, EXERCISE_TABLE, new String[]{EXERCISE_ID, EXERCISE_name, EXERCISE_muscles, EXERCISE_img}, EXERCISE_ID + "=" + id, null, null, null, null, null);
        if (mCursore != null) {
            mCursore.moveToFirst();
        }
        return mCursore;
    }

    /*
    Inserimento di un nuovo workout nella tabella
    */
    public long insertWorkout(String name, String type, int sets) {
        // creo una mappa di valori
        ContentValues initialValues = new ContentValues();
        initialValues.put(WORKOUT_name, name);
        initialValues.put(WORKOUT_type, type);
        initialValues.put(WORKOUT_sets, sets);
        // applico il metodo insert
        return db.insert(WORKOUT_TABLE, null, initialValues);
    }

    /*
    Inserimento di un nuovo exercise nella tabella
    */
    public long insertExercise(String name, String muscles, String image_name) {
        // creo una mappa di valori
        ContentValues initialValues = new ContentValues();
        initialValues.put(EXERCISE_name, name);
        initialValues.put(EXERCISE_muscles, muscles);
        initialValues.put(EXERCISE_img  , image_name);
        // applico il metodo insert
        return db.insert(EXERCISE_TABLE, null, initialValues);
    }

    /*
    Inserimento di un nuovo reps_sets nella tabella
    */
    public long insertRepsSets(int reps, int sets, int rest, int exercise_id, int workout_id) {
        // creo una mappa di valori
        ContentValues initialValues = new ContentValues();
        initialValues.put(REPS_SETS_reps, reps);
        initialValues.put(REPS_SETS_sets, sets);
        initialValues.put(REPS_SETS_rest, rest);
        initialValues.put(REPS_SETS_fk_exercise, exercise_id);
        initialValues.put(REPS_SETS_fk_workout, workout_id);
        // applico il metodo insert
        return db.insert(REPS_SETS_TABLE, null, initialValues);
    }

    /*
    Inserimento di un nuovo goal nella tabella
    */
    public long insertGoal(int reps, int exercise_id) {
        // creo una mappa di valori
        ContentValues initialValues = new ContentValues();
        initialValues.put(GOAL_reps, reps);
        initialValues.put(GOAL_fk_exercise, exercise_id);
        // applico il metodo insert
        return db.insert(GOAL_TABLE, null, initialValues);
    }

    /*
    Cancellazione di un workout
    */
    public boolean deleteRepsSetsById(int id) {
        // applico il metodo delete
        return db.delete(REPS_SETS_TABLE, REPS_SETS_ID + "='" + id + "'", null) > 0;
    }

    /*
    Cancellazione di un workout
    */
    public boolean deleteWorkout(String name) {
        // applico il metodo delete
        return db.delete(WORKOUT_TABLE, WORKOUT_name + "='" + name + "'", null) > 0;
    }


    public boolean deleteWorkout(int id) {
        // applico il metodo delete
        return db.delete(WORKOUT_TABLE, WORKOUT_ID + "=" + id, null) > 0;
    }


    public boolean deleteRepsSetsByWorkoutId(int id) {
        // applico il metodo delete
        return db.delete(REPS_SETS_TABLE, REPS_SETS_fk_workout + "=" + id, null) > 0;
    }

    /*
    Aggiorno dati di un workout
    */
    public boolean updateWorkout(String name) {
        // creo una mappa di valori
        ContentValues args = new ContentValues();
        args.put(WORKOUT_name, name);
        // applico il metodo update
        return db.update(WORKOUT_TABLE, args, WORKOUT_name + "='" + name + "'", null) > 0;
    }

    /*
    Aggiorno dati di un exercise
    */
    public boolean updateExercise(String name) {
        // creo una mappa di valori
        ContentValues args = new ContentValues();
        args.put(EXERCISE_name, name);
        // applico il metodo update
        return db.update(EXERCISE_TABLE, args, EXERCISE_name + "='" + name + "'", null) > 0;
    }

    public Cursor findRepsSetsByWorkoutId(int workoutId){
        Cursor mCursore = db.query(true, REPS_SETS_TABLE, new String[]{REPS_SETS_ID, REPS_SETS_reps, REPS_SETS_rest, REPS_SETS_sets, REPS_SETS_fk_exercise, REPS_SETS_fk_workout}, REPS_SETS_fk_workout + "=" + workoutId, null, null, null, null, null);
        if (mCursore != null) {
            mCursore.moveToFirst();
        }
        return mCursore;
    }

}
