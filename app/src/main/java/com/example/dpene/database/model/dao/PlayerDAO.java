package com.example.dpene.database.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dpene.database.model.DatabaseHelper;
import com.example.dpene.database.model.Player;

import java.util.ArrayList;
import java.util.List;


public class PlayerDAO implements IPlayerDAO {

    private DatabaseHelper dh;
    private Context context;
    private static PlayerDAO instance = null;

    private PlayerDAO(Context context){
        this.context = context;
        this.dh = DatabaseHelper.getInstance(context);
    }

    public static PlayerDAO getInstance(Context context){
        if(instance == null){
            return new PlayerDAO(context);
        }
        return instance;
    }

    @Override
    public long addPlayer(Player player) {

        SQLiteDatabase db = dh.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(dh.NAME, player.getName());
        values.put(dh.EMAIL, player.getEmail());
        values.put(dh.PASSWORD, player.getPassword());

        long userId = db.insert(dh.TABLE_PLAYER, null, values);
        db.close();
        return userId;
    }

    @Override
    public Player getPlayer(String username) {
        SQLiteDatabase db = dh.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + dh.TABLE_PLAYER
                + " WHERE " + dh.NAME + " = \"" + username + "\"";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex(dh.NAME));
        String password = cursor.getString(cursor.getColumnIndex(dh.PASSWORD));
        String email = cursor.getString(cursor.getColumnIndex(dh.EMAIL));
        int level = cursor.getInt(cursor.getColumnIndex(dh.PL_LEVEL));
        int question = cursor.getInt(cursor.getColumnIndex(dh.PL_QUESTION));
        int lives = cursor.getInt(cursor.getColumnIndex(dh.LIVES));

        Player player = new Player(email, name, password, level, question, lives);
        db.close();
        return player;
    }


    @Override
    public List<Player> getAllPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        String query = "SELECT * FROM " + dh.TABLE_PLAYER;

        SQLiteDatabase db = dh.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()){
            do{
                String username = c.getString(c.getColumnIndex(dh.NAME));
                String password = c.getString(c.getColumnIndex(dh.PASSWORD));
                String email = c.getString(c.getColumnIndex(dh.EMAIL));
                int level = c.getInt(c.getColumnIndex(dh.PL_LEVEL));
                int question = c.getInt(c.getColumnIndex(dh.PL_QUESTION));
                int lives = c.getInt(c.getColumnIndex(dh.LIVES));

                Player player = new Player(email, username, password, level, question, lives);
                players.add(player);
            } while(c.moveToNext());
        }
        db.close();
        return players;
    }

    @Override
    public boolean checkUsername(String username) {
        SQLiteDatabase db = dh.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + dh.TABLE_PLAYER
                + " WHERE " + dh.NAME + " = \"" + username + "\"";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            db.close();
            return true;
        }
        else{
            db.close();
            return false;
        }
    }

    @Override
    public boolean checkUserEmail(String email) {
        SQLiteDatabase db = dh.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + dh.TABLE_PLAYER
                + " WHERE " + dh.EMAIL + " = \"" + email + "\"";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            db.close();
            return true;
        }
        else{
            db.close();
            return false;
        }
    }

    @Override
    public Player checkLogin(String username, String password) {
        SQLiteDatabase db = dh.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + dh.TABLE_PLAYER
                + " WHERE " + dh.NAME + " = \"" + username
                + "\" AND " + dh.PASSWORD + " = \"" + password + "\"";

        Cursor c = db.rawQuery(selectQuery, null);

        Player player = null;

        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndex(dh.UID_PLAYER));
            String uname = c.getString(c.getColumnIndex(dh.NAME));
            String upassword = c.getString(c.getColumnIndex(dh.PASSWORD));
            String email = c.getString(c.getColumnIndex(dh.EMAIL));
            int level = c.getInt(c.getColumnIndex(dh.PL_LEVEL));
            int question = c.getInt(c.getColumnIndex(dh.PL_QUESTION));
            int lives = c.getInt(c.getColumnIndex(dh.LIVES));

            player = new Player(email, upassword, uname, level, question, lives);
        }

        db.close();
        return player;
    }

    @Override
    public void updatePlayer(Player player) {
        //UPDATE <TABLE_NAME> SET Name = 'newName' where Name=? oldNAme
        SQLiteDatabase db = dh.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dh.PL_QUESTION, player.getReachedQuestionId());
        values.put(dh.PL_LEVEL, player.getIdOfLevel());

        db.update(dh.TABLE_PLAYER, values, dh.NAME + " = ? ", new String[]{player.getName()});
        db.close();

    }
}
