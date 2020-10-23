package com.app.fueleco;
import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <h1>Database to save and manipulate the CarUser objects.</h1>
 * @author  Sotirios Nikas
 * @version 1.0
 * @since   2020-09-20
 */

public class DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UsersDB";
    private static final String TABLE_NAME = "Users";
    private static final String KEY_ID = "id";
    private static final String KEY_CAR = "car";
    private static final String KEY_MASS = "mass";
    private static final String KEY_C = "c";
    private static final String KEY_A = "a";
    private static final String KEY_EFFICIENCY = "efficiency";


    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Table creation with car properties information of the user.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Users ( "
                + "id INTEGER, " + "car TEXT, " + "mass DOUBLE, "
                + "c DOUBLE, " + "a DOUBLE, " + "efficiency DOUBLE )";
        db.execSQL(CREATION_TABLE);
    }


    /**
     * Update the version of DB when it is changed.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    /**
     * Set all the data from the user for all the cars.
     */
    public List<UserCar> allCars() {
        LinkedList<UserCar> cars = new LinkedList<>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        UserCar car;

        if (cursor.moveToFirst()) {
            do {
                car = new UserCar();
                car.setId(Integer.parseInt(cursor.getString(0)));
                car.setCar(cursor.getString(1));
                car.setMass(Double.parseDouble(cursor.getString(2)));
                car.setC(Double.parseDouble(cursor.getString(3)));
                car.setA(Double.parseDouble(cursor.getString(4)));
                car.set_Efficiency(Double.parseDouble(cursor.getString(5)));
                cars.add(car);
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

        return cars;
    }


    /**
     * Add the selected object (user) to the database.
     * If it returns a negative value, there is an error otherwise it returns true.
     */
    public boolean addUser(UserCar user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getId());
        values.put(KEY_CAR, user.getCar());
        values.put(KEY_MASS, user.getMass());
        values.put(KEY_C, user.getC());
        values.put(KEY_A, user.getA());
        values.put(KEY_EFFICIENCY, user.get_efficiency());

        long result  = db.insert(TABLE_NAME,null, values);
        return result != -1;
    }


    /**
     * Show the data of the selected object
     */
    public Cursor showData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data;
        data = db.rawQuery("SELECT  * FROM " + TABLE_NAME, null);
        return data;
    }


    /**
     * Delete the selected object from the database
     * @param car This is the name of the car.
     * @param id  This is also the corresponding id.
     * @return the deletion of the object with the corresponding name and id.
     * <b>Note:</b> The id is used for distinguishing cars with same name
     */
    public Integer deleteData(String car, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "car = ? AND id = ?", new String[]{car, String.valueOf(id)});
    }
}