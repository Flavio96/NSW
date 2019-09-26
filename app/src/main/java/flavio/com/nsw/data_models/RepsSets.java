package flavio.com.nsw.data_models;

public class RepsSets {
    private int id, reps, sets, rest, fk_workout, fk_exercise;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public int getFk_workout() {
        return fk_workout;
    }

    public void setFk_workout(int fk_workout) {
        this.fk_workout = fk_workout;
    }

    public int getFk_exercise() {
        return fk_exercise;
    }

    public void setFk_exercise(int fk_exercise) {
        this.fk_exercise = fk_exercise;
    }
}
