package com.idigital.asistenciasidigital.dstabase;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.idigital.asistenciasidigital.model.Place;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME    = "data.db";
	private static final int    DATABASE_VERSION = 1;

	private Dao<Place, Integer> mPlaceDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Place.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
						  int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Place.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Dao<Place, Integer> getPlaceDao() throws SQLException {
		if (mPlaceDao == null) {
            mPlaceDao = getDao(Place.class);
        }

        return mPlaceDao;
	}

	@Override
	public void close() {
		mPlaceDao = null;

		super.close();
	}
}
