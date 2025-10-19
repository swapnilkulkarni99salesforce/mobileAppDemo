package com.example.perfectfit.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.perfectfit.models.Measurement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MeasurementDao_Impl implements MeasurementDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Measurement> __insertionAdapterOfMeasurement;

  private final EntityDeletionOrUpdateAdapter<Measurement> __updateAdapterOfMeasurement;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMeasurementsByCustomerId;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateServerInfo;

  public MeasurementDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMeasurement = new EntityInsertionAdapter<Measurement>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `measurements` (`id`,`customerId`,`kurtiLength`,`fullShoulder`,`upperChestRound`,`chestRound`,`waistRound`,`shoulderToApex`,`apexToApex`,`shoulderToLowChestLength`,`skapLength`,`skapLengthRound`,`hipRound`,`frontNeckDeep`,`frontNeckWidth`,`backNeckDeep`,`readyShoulder`,`sleevesHeightShort`,`sleevesHeightElbow`,`sleevesHeightThreeQuarter`,`sleevesRound`,`pantWaist`,`pantLength`,`pantHip`,`pantBottom`,`blouseLength`,`blouseFullShoulder`,`blouseChest`,`blouseWaist`,`blouseShoulderToApex`,`blouseApexToApex`,`blouseBackLength`,`blouseFrontNeckDeep`,`blouseFrontNeckWidth`,`blouseBackNeckDeep`,`blouseReadyShoulder`,`blouseSleevesHeightShort`,`blouseSleevesHeightElbow`,`blouseSleevesHeightThreeQuarter`,`blouseSleevesRound`,`blouseHookOn`,`lastUpdated`,`serverId`,`lastModified`,`syncStatus`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Measurement entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        statement.bindString(3, entity.getKurtiLength());
        statement.bindString(4, entity.getFullShoulder());
        statement.bindString(5, entity.getUpperChestRound());
        statement.bindString(6, entity.getChestRound());
        statement.bindString(7, entity.getWaistRound());
        statement.bindString(8, entity.getShoulderToApex());
        statement.bindString(9, entity.getApexToApex());
        statement.bindString(10, entity.getShoulderToLowChestLength());
        statement.bindString(11, entity.getSkapLength());
        statement.bindString(12, entity.getSkapLengthRound());
        statement.bindString(13, entity.getHipRound());
        statement.bindString(14, entity.getFrontNeckDeep());
        statement.bindString(15, entity.getFrontNeckWidth());
        statement.bindString(16, entity.getBackNeckDeep());
        statement.bindString(17, entity.getReadyShoulder());
        statement.bindString(18, entity.getSleevesHeightShort());
        statement.bindString(19, entity.getSleevesHeightElbow());
        statement.bindString(20, entity.getSleevesHeightThreeQuarter());
        statement.bindString(21, entity.getSleevesRound());
        statement.bindString(22, entity.getPantWaist());
        statement.bindString(23, entity.getPantLength());
        statement.bindString(24, entity.getPantHip());
        statement.bindString(25, entity.getPantBottom());
        statement.bindString(26, entity.getBlouseLength());
        statement.bindString(27, entity.getBlouseFullShoulder());
        statement.bindString(28, entity.getBlouseChest());
        statement.bindString(29, entity.getBlouseWaist());
        statement.bindString(30, entity.getBlouseShoulderToApex());
        statement.bindString(31, entity.getBlouseApexToApex());
        statement.bindString(32, entity.getBlouseBackLength());
        statement.bindString(33, entity.getBlouseFrontNeckDeep());
        statement.bindString(34, entity.getBlouseFrontNeckWidth());
        statement.bindString(35, entity.getBlouseBackNeckDeep());
        statement.bindString(36, entity.getBlouseReadyShoulder());
        statement.bindString(37, entity.getBlouseSleevesHeightShort());
        statement.bindString(38, entity.getBlouseSleevesHeightElbow());
        statement.bindString(39, entity.getBlouseSleevesHeightThreeQuarter());
        statement.bindString(40, entity.getBlouseSleevesRound());
        statement.bindString(41, entity.getBlouseHookOn());
        statement.bindLong(42, entity.getLastUpdated());
        if (entity.getServerId() == null) {
          statement.bindNull(43);
        } else {
          statement.bindString(43, entity.getServerId());
        }
        statement.bindLong(44, entity.getLastModified());
        statement.bindString(45, entity.getSyncStatus());
      }
    };
    this.__updateAdapterOfMeasurement = new EntityDeletionOrUpdateAdapter<Measurement>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `measurements` SET `id` = ?,`customerId` = ?,`kurtiLength` = ?,`fullShoulder` = ?,`upperChestRound` = ?,`chestRound` = ?,`waistRound` = ?,`shoulderToApex` = ?,`apexToApex` = ?,`shoulderToLowChestLength` = ?,`skapLength` = ?,`skapLengthRound` = ?,`hipRound` = ?,`frontNeckDeep` = ?,`frontNeckWidth` = ?,`backNeckDeep` = ?,`readyShoulder` = ?,`sleevesHeightShort` = ?,`sleevesHeightElbow` = ?,`sleevesHeightThreeQuarter` = ?,`sleevesRound` = ?,`pantWaist` = ?,`pantLength` = ?,`pantHip` = ?,`pantBottom` = ?,`blouseLength` = ?,`blouseFullShoulder` = ?,`blouseChest` = ?,`blouseWaist` = ?,`blouseShoulderToApex` = ?,`blouseApexToApex` = ?,`blouseBackLength` = ?,`blouseFrontNeckDeep` = ?,`blouseFrontNeckWidth` = ?,`blouseBackNeckDeep` = ?,`blouseReadyShoulder` = ?,`blouseSleevesHeightShort` = ?,`blouseSleevesHeightElbow` = ?,`blouseSleevesHeightThreeQuarter` = ?,`blouseSleevesRound` = ?,`blouseHookOn` = ?,`lastUpdated` = ?,`serverId` = ?,`lastModified` = ?,`syncStatus` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Measurement entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        statement.bindString(3, entity.getKurtiLength());
        statement.bindString(4, entity.getFullShoulder());
        statement.bindString(5, entity.getUpperChestRound());
        statement.bindString(6, entity.getChestRound());
        statement.bindString(7, entity.getWaistRound());
        statement.bindString(8, entity.getShoulderToApex());
        statement.bindString(9, entity.getApexToApex());
        statement.bindString(10, entity.getShoulderToLowChestLength());
        statement.bindString(11, entity.getSkapLength());
        statement.bindString(12, entity.getSkapLengthRound());
        statement.bindString(13, entity.getHipRound());
        statement.bindString(14, entity.getFrontNeckDeep());
        statement.bindString(15, entity.getFrontNeckWidth());
        statement.bindString(16, entity.getBackNeckDeep());
        statement.bindString(17, entity.getReadyShoulder());
        statement.bindString(18, entity.getSleevesHeightShort());
        statement.bindString(19, entity.getSleevesHeightElbow());
        statement.bindString(20, entity.getSleevesHeightThreeQuarter());
        statement.bindString(21, entity.getSleevesRound());
        statement.bindString(22, entity.getPantWaist());
        statement.bindString(23, entity.getPantLength());
        statement.bindString(24, entity.getPantHip());
        statement.bindString(25, entity.getPantBottom());
        statement.bindString(26, entity.getBlouseLength());
        statement.bindString(27, entity.getBlouseFullShoulder());
        statement.bindString(28, entity.getBlouseChest());
        statement.bindString(29, entity.getBlouseWaist());
        statement.bindString(30, entity.getBlouseShoulderToApex());
        statement.bindString(31, entity.getBlouseApexToApex());
        statement.bindString(32, entity.getBlouseBackLength());
        statement.bindString(33, entity.getBlouseFrontNeckDeep());
        statement.bindString(34, entity.getBlouseFrontNeckWidth());
        statement.bindString(35, entity.getBlouseBackNeckDeep());
        statement.bindString(36, entity.getBlouseReadyShoulder());
        statement.bindString(37, entity.getBlouseSleevesHeightShort());
        statement.bindString(38, entity.getBlouseSleevesHeightElbow());
        statement.bindString(39, entity.getBlouseSleevesHeightThreeQuarter());
        statement.bindString(40, entity.getBlouseSleevesRound());
        statement.bindString(41, entity.getBlouseHookOn());
        statement.bindLong(42, entity.getLastUpdated());
        if (entity.getServerId() == null) {
          statement.bindNull(43);
        } else {
          statement.bindString(43, entity.getServerId());
        }
        statement.bindLong(44, entity.getLastModified());
        statement.bindString(45, entity.getSyncStatus());
        statement.bindLong(46, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteMeasurementsByCustomerId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM measurements WHERE customerId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE measurements SET syncStatus = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateServerInfo = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE measurements SET serverId = ?, syncStatus = ?, lastModified = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMeasurement(final Measurement measurement,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMeasurement.insertAndReturnId(measurement);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMeasurement(final Measurement measurement,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMeasurement.handle(measurement);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMeasurementsByCustomerId(final int customerId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMeasurementsByCustomerId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, customerId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteMeasurementsByCustomerId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final int measurementId, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, measurementId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateServerInfo(final int localId, final String serverId, final String status,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateServerInfo.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, serverId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, status);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, localId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateServerInfo.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<Measurement> getMeasurementByCustomerId(final int customerId) {
    final String _sql = "SELECT * FROM measurements WHERE customerId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, customerId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"measurements"}, false, new Callable<Measurement>() {
      @Override
      @Nullable
      public Measurement call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfKurtiLength = CursorUtil.getColumnIndexOrThrow(_cursor, "kurtiLength");
          final int _cursorIndexOfFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "fullShoulder");
          final int _cursorIndexOfUpperChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "upperChestRound");
          final int _cursorIndexOfChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "chestRound");
          final int _cursorIndexOfWaistRound = CursorUtil.getColumnIndexOrThrow(_cursor, "waistRound");
          final int _cursorIndexOfShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToApex");
          final int _cursorIndexOfApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "apexToApex");
          final int _cursorIndexOfShoulderToLowChestLength = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToLowChestLength");
          final int _cursorIndexOfSkapLength = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLength");
          final int _cursorIndexOfSkapLengthRound = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLengthRound");
          final int _cursorIndexOfHipRound = CursorUtil.getColumnIndexOrThrow(_cursor, "hipRound");
          final int _cursorIndexOfFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckDeep");
          final int _cursorIndexOfFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckWidth");
          final int _cursorIndexOfBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "backNeckDeep");
          final int _cursorIndexOfReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "readyShoulder");
          final int _cursorIndexOfSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightShort");
          final int _cursorIndexOfSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightElbow");
          final int _cursorIndexOfSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightThreeQuarter");
          final int _cursorIndexOfSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesRound");
          final int _cursorIndexOfPantWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "pantWaist");
          final int _cursorIndexOfPantLength = CursorUtil.getColumnIndexOrThrow(_cursor, "pantLength");
          final int _cursorIndexOfPantHip = CursorUtil.getColumnIndexOrThrow(_cursor, "pantHip");
          final int _cursorIndexOfPantBottom = CursorUtil.getColumnIndexOrThrow(_cursor, "pantBottom");
          final int _cursorIndexOfBlouseLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseLength");
          final int _cursorIndexOfBlouseFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFullShoulder");
          final int _cursorIndexOfBlouseChest = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseChest");
          final int _cursorIndexOfBlouseWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseWaist");
          final int _cursorIndexOfBlouseShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseShoulderToApex");
          final int _cursorIndexOfBlouseApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseApexToApex");
          final int _cursorIndexOfBlouseBackLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackLength");
          final int _cursorIndexOfBlouseFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckDeep");
          final int _cursorIndexOfBlouseFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckWidth");
          final int _cursorIndexOfBlouseBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackNeckDeep");
          final int _cursorIndexOfBlouseReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseReadyShoulder");
          final int _cursorIndexOfBlouseSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightShort");
          final int _cursorIndexOfBlouseSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightElbow");
          final int _cursorIndexOfBlouseSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightThreeQuarter");
          final int _cursorIndexOfBlouseSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesRound");
          final int _cursorIndexOfBlouseHookOn = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseHookOn");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final Measurement _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpKurtiLength;
            _tmpKurtiLength = _cursor.getString(_cursorIndexOfKurtiLength);
            final String _tmpFullShoulder;
            _tmpFullShoulder = _cursor.getString(_cursorIndexOfFullShoulder);
            final String _tmpUpperChestRound;
            _tmpUpperChestRound = _cursor.getString(_cursorIndexOfUpperChestRound);
            final String _tmpChestRound;
            _tmpChestRound = _cursor.getString(_cursorIndexOfChestRound);
            final String _tmpWaistRound;
            _tmpWaistRound = _cursor.getString(_cursorIndexOfWaistRound);
            final String _tmpShoulderToApex;
            _tmpShoulderToApex = _cursor.getString(_cursorIndexOfShoulderToApex);
            final String _tmpApexToApex;
            _tmpApexToApex = _cursor.getString(_cursorIndexOfApexToApex);
            final String _tmpShoulderToLowChestLength;
            _tmpShoulderToLowChestLength = _cursor.getString(_cursorIndexOfShoulderToLowChestLength);
            final String _tmpSkapLength;
            _tmpSkapLength = _cursor.getString(_cursorIndexOfSkapLength);
            final String _tmpSkapLengthRound;
            _tmpSkapLengthRound = _cursor.getString(_cursorIndexOfSkapLengthRound);
            final String _tmpHipRound;
            _tmpHipRound = _cursor.getString(_cursorIndexOfHipRound);
            final String _tmpFrontNeckDeep;
            _tmpFrontNeckDeep = _cursor.getString(_cursorIndexOfFrontNeckDeep);
            final String _tmpFrontNeckWidth;
            _tmpFrontNeckWidth = _cursor.getString(_cursorIndexOfFrontNeckWidth);
            final String _tmpBackNeckDeep;
            _tmpBackNeckDeep = _cursor.getString(_cursorIndexOfBackNeckDeep);
            final String _tmpReadyShoulder;
            _tmpReadyShoulder = _cursor.getString(_cursorIndexOfReadyShoulder);
            final String _tmpSleevesHeightShort;
            _tmpSleevesHeightShort = _cursor.getString(_cursorIndexOfSleevesHeightShort);
            final String _tmpSleevesHeightElbow;
            _tmpSleevesHeightElbow = _cursor.getString(_cursorIndexOfSleevesHeightElbow);
            final String _tmpSleevesHeightThreeQuarter;
            _tmpSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfSleevesHeightThreeQuarter);
            final String _tmpSleevesRound;
            _tmpSleevesRound = _cursor.getString(_cursorIndexOfSleevesRound);
            final String _tmpPantWaist;
            _tmpPantWaist = _cursor.getString(_cursorIndexOfPantWaist);
            final String _tmpPantLength;
            _tmpPantLength = _cursor.getString(_cursorIndexOfPantLength);
            final String _tmpPantHip;
            _tmpPantHip = _cursor.getString(_cursorIndexOfPantHip);
            final String _tmpPantBottom;
            _tmpPantBottom = _cursor.getString(_cursorIndexOfPantBottom);
            final String _tmpBlouseLength;
            _tmpBlouseLength = _cursor.getString(_cursorIndexOfBlouseLength);
            final String _tmpBlouseFullShoulder;
            _tmpBlouseFullShoulder = _cursor.getString(_cursorIndexOfBlouseFullShoulder);
            final String _tmpBlouseChest;
            _tmpBlouseChest = _cursor.getString(_cursorIndexOfBlouseChest);
            final String _tmpBlouseWaist;
            _tmpBlouseWaist = _cursor.getString(_cursorIndexOfBlouseWaist);
            final String _tmpBlouseShoulderToApex;
            _tmpBlouseShoulderToApex = _cursor.getString(_cursorIndexOfBlouseShoulderToApex);
            final String _tmpBlouseApexToApex;
            _tmpBlouseApexToApex = _cursor.getString(_cursorIndexOfBlouseApexToApex);
            final String _tmpBlouseBackLength;
            _tmpBlouseBackLength = _cursor.getString(_cursorIndexOfBlouseBackLength);
            final String _tmpBlouseFrontNeckDeep;
            _tmpBlouseFrontNeckDeep = _cursor.getString(_cursorIndexOfBlouseFrontNeckDeep);
            final String _tmpBlouseFrontNeckWidth;
            _tmpBlouseFrontNeckWidth = _cursor.getString(_cursorIndexOfBlouseFrontNeckWidth);
            final String _tmpBlouseBackNeckDeep;
            _tmpBlouseBackNeckDeep = _cursor.getString(_cursorIndexOfBlouseBackNeckDeep);
            final String _tmpBlouseReadyShoulder;
            _tmpBlouseReadyShoulder = _cursor.getString(_cursorIndexOfBlouseReadyShoulder);
            final String _tmpBlouseSleevesHeightShort;
            _tmpBlouseSleevesHeightShort = _cursor.getString(_cursorIndexOfBlouseSleevesHeightShort);
            final String _tmpBlouseSleevesHeightElbow;
            _tmpBlouseSleevesHeightElbow = _cursor.getString(_cursorIndexOfBlouseSleevesHeightElbow);
            final String _tmpBlouseSleevesHeightThreeQuarter;
            _tmpBlouseSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfBlouseSleevesHeightThreeQuarter);
            final String _tmpBlouseSleevesRound;
            _tmpBlouseSleevesRound = _cursor.getString(_cursorIndexOfBlouseSleevesRound);
            final String _tmpBlouseHookOn;
            _tmpBlouseHookOn = _cursor.getString(_cursorIndexOfBlouseHookOn);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final String _tmpServerId;
            if (_cursor.isNull(_cursorIndexOfServerId)) {
              _tmpServerId = null;
            } else {
              _tmpServerId = _cursor.getString(_cursorIndexOfServerId);
            }
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _result = new Measurement(_tmpId,_tmpCustomerId,_tmpKurtiLength,_tmpFullShoulder,_tmpUpperChestRound,_tmpChestRound,_tmpWaistRound,_tmpShoulderToApex,_tmpApexToApex,_tmpShoulderToLowChestLength,_tmpSkapLength,_tmpSkapLengthRound,_tmpHipRound,_tmpFrontNeckDeep,_tmpFrontNeckWidth,_tmpBackNeckDeep,_tmpReadyShoulder,_tmpSleevesHeightShort,_tmpSleevesHeightElbow,_tmpSleevesHeightThreeQuarter,_tmpSleevesRound,_tmpPantWaist,_tmpPantLength,_tmpPantHip,_tmpPantBottom,_tmpBlouseLength,_tmpBlouseFullShoulder,_tmpBlouseChest,_tmpBlouseWaist,_tmpBlouseShoulderToApex,_tmpBlouseApexToApex,_tmpBlouseBackLength,_tmpBlouseFrontNeckDeep,_tmpBlouseFrontNeckWidth,_tmpBlouseBackNeckDeep,_tmpBlouseReadyShoulder,_tmpBlouseSleevesHeightShort,_tmpBlouseSleevesHeightElbow,_tmpBlouseSleevesHeightThreeQuarter,_tmpBlouseSleevesRound,_tmpBlouseHookOn,_tmpLastUpdated,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMeasurementByCustomerIdSync(final int customerId,
      final Continuation<? super Measurement> $completion) {
    final String _sql = "SELECT * FROM measurements WHERE customerId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, customerId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Measurement>() {
      @Override
      @Nullable
      public Measurement call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfKurtiLength = CursorUtil.getColumnIndexOrThrow(_cursor, "kurtiLength");
          final int _cursorIndexOfFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "fullShoulder");
          final int _cursorIndexOfUpperChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "upperChestRound");
          final int _cursorIndexOfChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "chestRound");
          final int _cursorIndexOfWaistRound = CursorUtil.getColumnIndexOrThrow(_cursor, "waistRound");
          final int _cursorIndexOfShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToApex");
          final int _cursorIndexOfApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "apexToApex");
          final int _cursorIndexOfShoulderToLowChestLength = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToLowChestLength");
          final int _cursorIndexOfSkapLength = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLength");
          final int _cursorIndexOfSkapLengthRound = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLengthRound");
          final int _cursorIndexOfHipRound = CursorUtil.getColumnIndexOrThrow(_cursor, "hipRound");
          final int _cursorIndexOfFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckDeep");
          final int _cursorIndexOfFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckWidth");
          final int _cursorIndexOfBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "backNeckDeep");
          final int _cursorIndexOfReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "readyShoulder");
          final int _cursorIndexOfSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightShort");
          final int _cursorIndexOfSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightElbow");
          final int _cursorIndexOfSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightThreeQuarter");
          final int _cursorIndexOfSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesRound");
          final int _cursorIndexOfPantWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "pantWaist");
          final int _cursorIndexOfPantLength = CursorUtil.getColumnIndexOrThrow(_cursor, "pantLength");
          final int _cursorIndexOfPantHip = CursorUtil.getColumnIndexOrThrow(_cursor, "pantHip");
          final int _cursorIndexOfPantBottom = CursorUtil.getColumnIndexOrThrow(_cursor, "pantBottom");
          final int _cursorIndexOfBlouseLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseLength");
          final int _cursorIndexOfBlouseFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFullShoulder");
          final int _cursorIndexOfBlouseChest = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseChest");
          final int _cursorIndexOfBlouseWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseWaist");
          final int _cursorIndexOfBlouseShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseShoulderToApex");
          final int _cursorIndexOfBlouseApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseApexToApex");
          final int _cursorIndexOfBlouseBackLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackLength");
          final int _cursorIndexOfBlouseFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckDeep");
          final int _cursorIndexOfBlouseFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckWidth");
          final int _cursorIndexOfBlouseBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackNeckDeep");
          final int _cursorIndexOfBlouseReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseReadyShoulder");
          final int _cursorIndexOfBlouseSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightShort");
          final int _cursorIndexOfBlouseSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightElbow");
          final int _cursorIndexOfBlouseSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightThreeQuarter");
          final int _cursorIndexOfBlouseSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesRound");
          final int _cursorIndexOfBlouseHookOn = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseHookOn");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final Measurement _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpKurtiLength;
            _tmpKurtiLength = _cursor.getString(_cursorIndexOfKurtiLength);
            final String _tmpFullShoulder;
            _tmpFullShoulder = _cursor.getString(_cursorIndexOfFullShoulder);
            final String _tmpUpperChestRound;
            _tmpUpperChestRound = _cursor.getString(_cursorIndexOfUpperChestRound);
            final String _tmpChestRound;
            _tmpChestRound = _cursor.getString(_cursorIndexOfChestRound);
            final String _tmpWaistRound;
            _tmpWaistRound = _cursor.getString(_cursorIndexOfWaistRound);
            final String _tmpShoulderToApex;
            _tmpShoulderToApex = _cursor.getString(_cursorIndexOfShoulderToApex);
            final String _tmpApexToApex;
            _tmpApexToApex = _cursor.getString(_cursorIndexOfApexToApex);
            final String _tmpShoulderToLowChestLength;
            _tmpShoulderToLowChestLength = _cursor.getString(_cursorIndexOfShoulderToLowChestLength);
            final String _tmpSkapLength;
            _tmpSkapLength = _cursor.getString(_cursorIndexOfSkapLength);
            final String _tmpSkapLengthRound;
            _tmpSkapLengthRound = _cursor.getString(_cursorIndexOfSkapLengthRound);
            final String _tmpHipRound;
            _tmpHipRound = _cursor.getString(_cursorIndexOfHipRound);
            final String _tmpFrontNeckDeep;
            _tmpFrontNeckDeep = _cursor.getString(_cursorIndexOfFrontNeckDeep);
            final String _tmpFrontNeckWidth;
            _tmpFrontNeckWidth = _cursor.getString(_cursorIndexOfFrontNeckWidth);
            final String _tmpBackNeckDeep;
            _tmpBackNeckDeep = _cursor.getString(_cursorIndexOfBackNeckDeep);
            final String _tmpReadyShoulder;
            _tmpReadyShoulder = _cursor.getString(_cursorIndexOfReadyShoulder);
            final String _tmpSleevesHeightShort;
            _tmpSleevesHeightShort = _cursor.getString(_cursorIndexOfSleevesHeightShort);
            final String _tmpSleevesHeightElbow;
            _tmpSleevesHeightElbow = _cursor.getString(_cursorIndexOfSleevesHeightElbow);
            final String _tmpSleevesHeightThreeQuarter;
            _tmpSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfSleevesHeightThreeQuarter);
            final String _tmpSleevesRound;
            _tmpSleevesRound = _cursor.getString(_cursorIndexOfSleevesRound);
            final String _tmpPantWaist;
            _tmpPantWaist = _cursor.getString(_cursorIndexOfPantWaist);
            final String _tmpPantLength;
            _tmpPantLength = _cursor.getString(_cursorIndexOfPantLength);
            final String _tmpPantHip;
            _tmpPantHip = _cursor.getString(_cursorIndexOfPantHip);
            final String _tmpPantBottom;
            _tmpPantBottom = _cursor.getString(_cursorIndexOfPantBottom);
            final String _tmpBlouseLength;
            _tmpBlouseLength = _cursor.getString(_cursorIndexOfBlouseLength);
            final String _tmpBlouseFullShoulder;
            _tmpBlouseFullShoulder = _cursor.getString(_cursorIndexOfBlouseFullShoulder);
            final String _tmpBlouseChest;
            _tmpBlouseChest = _cursor.getString(_cursorIndexOfBlouseChest);
            final String _tmpBlouseWaist;
            _tmpBlouseWaist = _cursor.getString(_cursorIndexOfBlouseWaist);
            final String _tmpBlouseShoulderToApex;
            _tmpBlouseShoulderToApex = _cursor.getString(_cursorIndexOfBlouseShoulderToApex);
            final String _tmpBlouseApexToApex;
            _tmpBlouseApexToApex = _cursor.getString(_cursorIndexOfBlouseApexToApex);
            final String _tmpBlouseBackLength;
            _tmpBlouseBackLength = _cursor.getString(_cursorIndexOfBlouseBackLength);
            final String _tmpBlouseFrontNeckDeep;
            _tmpBlouseFrontNeckDeep = _cursor.getString(_cursorIndexOfBlouseFrontNeckDeep);
            final String _tmpBlouseFrontNeckWidth;
            _tmpBlouseFrontNeckWidth = _cursor.getString(_cursorIndexOfBlouseFrontNeckWidth);
            final String _tmpBlouseBackNeckDeep;
            _tmpBlouseBackNeckDeep = _cursor.getString(_cursorIndexOfBlouseBackNeckDeep);
            final String _tmpBlouseReadyShoulder;
            _tmpBlouseReadyShoulder = _cursor.getString(_cursorIndexOfBlouseReadyShoulder);
            final String _tmpBlouseSleevesHeightShort;
            _tmpBlouseSleevesHeightShort = _cursor.getString(_cursorIndexOfBlouseSleevesHeightShort);
            final String _tmpBlouseSleevesHeightElbow;
            _tmpBlouseSleevesHeightElbow = _cursor.getString(_cursorIndexOfBlouseSleevesHeightElbow);
            final String _tmpBlouseSleevesHeightThreeQuarter;
            _tmpBlouseSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfBlouseSleevesHeightThreeQuarter);
            final String _tmpBlouseSleevesRound;
            _tmpBlouseSleevesRound = _cursor.getString(_cursorIndexOfBlouseSleevesRound);
            final String _tmpBlouseHookOn;
            _tmpBlouseHookOn = _cursor.getString(_cursorIndexOfBlouseHookOn);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final String _tmpServerId;
            if (_cursor.isNull(_cursorIndexOfServerId)) {
              _tmpServerId = null;
            } else {
              _tmpServerId = _cursor.getString(_cursorIndexOfServerId);
            }
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _result = new Measurement(_tmpId,_tmpCustomerId,_tmpKurtiLength,_tmpFullShoulder,_tmpUpperChestRound,_tmpChestRound,_tmpWaistRound,_tmpShoulderToApex,_tmpApexToApex,_tmpShoulderToLowChestLength,_tmpSkapLength,_tmpSkapLengthRound,_tmpHipRound,_tmpFrontNeckDeep,_tmpFrontNeckWidth,_tmpBackNeckDeep,_tmpReadyShoulder,_tmpSleevesHeightShort,_tmpSleevesHeightElbow,_tmpSleevesHeightThreeQuarter,_tmpSleevesRound,_tmpPantWaist,_tmpPantLength,_tmpPantHip,_tmpPantBottom,_tmpBlouseLength,_tmpBlouseFullShoulder,_tmpBlouseChest,_tmpBlouseWaist,_tmpBlouseShoulderToApex,_tmpBlouseApexToApex,_tmpBlouseBackLength,_tmpBlouseFrontNeckDeep,_tmpBlouseFrontNeckWidth,_tmpBlouseBackNeckDeep,_tmpBlouseReadyShoulder,_tmpBlouseSleevesHeightShort,_tmpBlouseSleevesHeightElbow,_tmpBlouseSleevesHeightThreeQuarter,_tmpBlouseSleevesRound,_tmpBlouseHookOn,_tmpLastUpdated,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMeasurementsBySyncStatus(final String status,
      final Continuation<? super List<Measurement>> $completion) {
    final String _sql = "SELECT * FROM measurements WHERE syncStatus = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Measurement>>() {
      @Override
      @NonNull
      public List<Measurement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfKurtiLength = CursorUtil.getColumnIndexOrThrow(_cursor, "kurtiLength");
          final int _cursorIndexOfFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "fullShoulder");
          final int _cursorIndexOfUpperChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "upperChestRound");
          final int _cursorIndexOfChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "chestRound");
          final int _cursorIndexOfWaistRound = CursorUtil.getColumnIndexOrThrow(_cursor, "waistRound");
          final int _cursorIndexOfShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToApex");
          final int _cursorIndexOfApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "apexToApex");
          final int _cursorIndexOfShoulderToLowChestLength = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToLowChestLength");
          final int _cursorIndexOfSkapLength = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLength");
          final int _cursorIndexOfSkapLengthRound = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLengthRound");
          final int _cursorIndexOfHipRound = CursorUtil.getColumnIndexOrThrow(_cursor, "hipRound");
          final int _cursorIndexOfFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckDeep");
          final int _cursorIndexOfFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckWidth");
          final int _cursorIndexOfBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "backNeckDeep");
          final int _cursorIndexOfReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "readyShoulder");
          final int _cursorIndexOfSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightShort");
          final int _cursorIndexOfSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightElbow");
          final int _cursorIndexOfSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightThreeQuarter");
          final int _cursorIndexOfSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesRound");
          final int _cursorIndexOfPantWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "pantWaist");
          final int _cursorIndexOfPantLength = CursorUtil.getColumnIndexOrThrow(_cursor, "pantLength");
          final int _cursorIndexOfPantHip = CursorUtil.getColumnIndexOrThrow(_cursor, "pantHip");
          final int _cursorIndexOfPantBottom = CursorUtil.getColumnIndexOrThrow(_cursor, "pantBottom");
          final int _cursorIndexOfBlouseLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseLength");
          final int _cursorIndexOfBlouseFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFullShoulder");
          final int _cursorIndexOfBlouseChest = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseChest");
          final int _cursorIndexOfBlouseWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseWaist");
          final int _cursorIndexOfBlouseShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseShoulderToApex");
          final int _cursorIndexOfBlouseApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseApexToApex");
          final int _cursorIndexOfBlouseBackLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackLength");
          final int _cursorIndexOfBlouseFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckDeep");
          final int _cursorIndexOfBlouseFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckWidth");
          final int _cursorIndexOfBlouseBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackNeckDeep");
          final int _cursorIndexOfBlouseReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseReadyShoulder");
          final int _cursorIndexOfBlouseSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightShort");
          final int _cursorIndexOfBlouseSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightElbow");
          final int _cursorIndexOfBlouseSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightThreeQuarter");
          final int _cursorIndexOfBlouseSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesRound");
          final int _cursorIndexOfBlouseHookOn = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseHookOn");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<Measurement> _result = new ArrayList<Measurement>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Measurement _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpKurtiLength;
            _tmpKurtiLength = _cursor.getString(_cursorIndexOfKurtiLength);
            final String _tmpFullShoulder;
            _tmpFullShoulder = _cursor.getString(_cursorIndexOfFullShoulder);
            final String _tmpUpperChestRound;
            _tmpUpperChestRound = _cursor.getString(_cursorIndexOfUpperChestRound);
            final String _tmpChestRound;
            _tmpChestRound = _cursor.getString(_cursorIndexOfChestRound);
            final String _tmpWaistRound;
            _tmpWaistRound = _cursor.getString(_cursorIndexOfWaistRound);
            final String _tmpShoulderToApex;
            _tmpShoulderToApex = _cursor.getString(_cursorIndexOfShoulderToApex);
            final String _tmpApexToApex;
            _tmpApexToApex = _cursor.getString(_cursorIndexOfApexToApex);
            final String _tmpShoulderToLowChestLength;
            _tmpShoulderToLowChestLength = _cursor.getString(_cursorIndexOfShoulderToLowChestLength);
            final String _tmpSkapLength;
            _tmpSkapLength = _cursor.getString(_cursorIndexOfSkapLength);
            final String _tmpSkapLengthRound;
            _tmpSkapLengthRound = _cursor.getString(_cursorIndexOfSkapLengthRound);
            final String _tmpHipRound;
            _tmpHipRound = _cursor.getString(_cursorIndexOfHipRound);
            final String _tmpFrontNeckDeep;
            _tmpFrontNeckDeep = _cursor.getString(_cursorIndexOfFrontNeckDeep);
            final String _tmpFrontNeckWidth;
            _tmpFrontNeckWidth = _cursor.getString(_cursorIndexOfFrontNeckWidth);
            final String _tmpBackNeckDeep;
            _tmpBackNeckDeep = _cursor.getString(_cursorIndexOfBackNeckDeep);
            final String _tmpReadyShoulder;
            _tmpReadyShoulder = _cursor.getString(_cursorIndexOfReadyShoulder);
            final String _tmpSleevesHeightShort;
            _tmpSleevesHeightShort = _cursor.getString(_cursorIndexOfSleevesHeightShort);
            final String _tmpSleevesHeightElbow;
            _tmpSleevesHeightElbow = _cursor.getString(_cursorIndexOfSleevesHeightElbow);
            final String _tmpSleevesHeightThreeQuarter;
            _tmpSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfSleevesHeightThreeQuarter);
            final String _tmpSleevesRound;
            _tmpSleevesRound = _cursor.getString(_cursorIndexOfSleevesRound);
            final String _tmpPantWaist;
            _tmpPantWaist = _cursor.getString(_cursorIndexOfPantWaist);
            final String _tmpPantLength;
            _tmpPantLength = _cursor.getString(_cursorIndexOfPantLength);
            final String _tmpPantHip;
            _tmpPantHip = _cursor.getString(_cursorIndexOfPantHip);
            final String _tmpPantBottom;
            _tmpPantBottom = _cursor.getString(_cursorIndexOfPantBottom);
            final String _tmpBlouseLength;
            _tmpBlouseLength = _cursor.getString(_cursorIndexOfBlouseLength);
            final String _tmpBlouseFullShoulder;
            _tmpBlouseFullShoulder = _cursor.getString(_cursorIndexOfBlouseFullShoulder);
            final String _tmpBlouseChest;
            _tmpBlouseChest = _cursor.getString(_cursorIndexOfBlouseChest);
            final String _tmpBlouseWaist;
            _tmpBlouseWaist = _cursor.getString(_cursorIndexOfBlouseWaist);
            final String _tmpBlouseShoulderToApex;
            _tmpBlouseShoulderToApex = _cursor.getString(_cursorIndexOfBlouseShoulderToApex);
            final String _tmpBlouseApexToApex;
            _tmpBlouseApexToApex = _cursor.getString(_cursorIndexOfBlouseApexToApex);
            final String _tmpBlouseBackLength;
            _tmpBlouseBackLength = _cursor.getString(_cursorIndexOfBlouseBackLength);
            final String _tmpBlouseFrontNeckDeep;
            _tmpBlouseFrontNeckDeep = _cursor.getString(_cursorIndexOfBlouseFrontNeckDeep);
            final String _tmpBlouseFrontNeckWidth;
            _tmpBlouseFrontNeckWidth = _cursor.getString(_cursorIndexOfBlouseFrontNeckWidth);
            final String _tmpBlouseBackNeckDeep;
            _tmpBlouseBackNeckDeep = _cursor.getString(_cursorIndexOfBlouseBackNeckDeep);
            final String _tmpBlouseReadyShoulder;
            _tmpBlouseReadyShoulder = _cursor.getString(_cursorIndexOfBlouseReadyShoulder);
            final String _tmpBlouseSleevesHeightShort;
            _tmpBlouseSleevesHeightShort = _cursor.getString(_cursorIndexOfBlouseSleevesHeightShort);
            final String _tmpBlouseSleevesHeightElbow;
            _tmpBlouseSleevesHeightElbow = _cursor.getString(_cursorIndexOfBlouseSleevesHeightElbow);
            final String _tmpBlouseSleevesHeightThreeQuarter;
            _tmpBlouseSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfBlouseSleevesHeightThreeQuarter);
            final String _tmpBlouseSleevesRound;
            _tmpBlouseSleevesRound = _cursor.getString(_cursorIndexOfBlouseSleevesRound);
            final String _tmpBlouseHookOn;
            _tmpBlouseHookOn = _cursor.getString(_cursorIndexOfBlouseHookOn);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final String _tmpServerId;
            if (_cursor.isNull(_cursorIndexOfServerId)) {
              _tmpServerId = null;
            } else {
              _tmpServerId = _cursor.getString(_cursorIndexOfServerId);
            }
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _item = new Measurement(_tmpId,_tmpCustomerId,_tmpKurtiLength,_tmpFullShoulder,_tmpUpperChestRound,_tmpChestRound,_tmpWaistRound,_tmpShoulderToApex,_tmpApexToApex,_tmpShoulderToLowChestLength,_tmpSkapLength,_tmpSkapLengthRound,_tmpHipRound,_tmpFrontNeckDeep,_tmpFrontNeckWidth,_tmpBackNeckDeep,_tmpReadyShoulder,_tmpSleevesHeightShort,_tmpSleevesHeightElbow,_tmpSleevesHeightThreeQuarter,_tmpSleevesRound,_tmpPantWaist,_tmpPantLength,_tmpPantHip,_tmpPantBottom,_tmpBlouseLength,_tmpBlouseFullShoulder,_tmpBlouseChest,_tmpBlouseWaist,_tmpBlouseShoulderToApex,_tmpBlouseApexToApex,_tmpBlouseBackLength,_tmpBlouseFrontNeckDeep,_tmpBlouseFrontNeckWidth,_tmpBlouseBackNeckDeep,_tmpBlouseReadyShoulder,_tmpBlouseSleevesHeightShort,_tmpBlouseSleevesHeightElbow,_tmpBlouseSleevesHeightThreeQuarter,_tmpBlouseSleevesRound,_tmpBlouseHookOn,_tmpLastUpdated,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUnsyncedMeasurements(final Continuation<? super List<Measurement>> $completion) {
    final String _sql = "SELECT * FROM measurements WHERE syncStatus = 'PENDING' OR syncStatus = 'FAILED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Measurement>>() {
      @Override
      @NonNull
      public List<Measurement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfKurtiLength = CursorUtil.getColumnIndexOrThrow(_cursor, "kurtiLength");
          final int _cursorIndexOfFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "fullShoulder");
          final int _cursorIndexOfUpperChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "upperChestRound");
          final int _cursorIndexOfChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "chestRound");
          final int _cursorIndexOfWaistRound = CursorUtil.getColumnIndexOrThrow(_cursor, "waistRound");
          final int _cursorIndexOfShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToApex");
          final int _cursorIndexOfApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "apexToApex");
          final int _cursorIndexOfShoulderToLowChestLength = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToLowChestLength");
          final int _cursorIndexOfSkapLength = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLength");
          final int _cursorIndexOfSkapLengthRound = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLengthRound");
          final int _cursorIndexOfHipRound = CursorUtil.getColumnIndexOrThrow(_cursor, "hipRound");
          final int _cursorIndexOfFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckDeep");
          final int _cursorIndexOfFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckWidth");
          final int _cursorIndexOfBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "backNeckDeep");
          final int _cursorIndexOfReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "readyShoulder");
          final int _cursorIndexOfSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightShort");
          final int _cursorIndexOfSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightElbow");
          final int _cursorIndexOfSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightThreeQuarter");
          final int _cursorIndexOfSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesRound");
          final int _cursorIndexOfPantWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "pantWaist");
          final int _cursorIndexOfPantLength = CursorUtil.getColumnIndexOrThrow(_cursor, "pantLength");
          final int _cursorIndexOfPantHip = CursorUtil.getColumnIndexOrThrow(_cursor, "pantHip");
          final int _cursorIndexOfPantBottom = CursorUtil.getColumnIndexOrThrow(_cursor, "pantBottom");
          final int _cursorIndexOfBlouseLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseLength");
          final int _cursorIndexOfBlouseFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFullShoulder");
          final int _cursorIndexOfBlouseChest = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseChest");
          final int _cursorIndexOfBlouseWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseWaist");
          final int _cursorIndexOfBlouseShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseShoulderToApex");
          final int _cursorIndexOfBlouseApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseApexToApex");
          final int _cursorIndexOfBlouseBackLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackLength");
          final int _cursorIndexOfBlouseFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckDeep");
          final int _cursorIndexOfBlouseFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckWidth");
          final int _cursorIndexOfBlouseBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackNeckDeep");
          final int _cursorIndexOfBlouseReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseReadyShoulder");
          final int _cursorIndexOfBlouseSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightShort");
          final int _cursorIndexOfBlouseSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightElbow");
          final int _cursorIndexOfBlouseSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightThreeQuarter");
          final int _cursorIndexOfBlouseSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesRound");
          final int _cursorIndexOfBlouseHookOn = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseHookOn");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<Measurement> _result = new ArrayList<Measurement>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Measurement _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpKurtiLength;
            _tmpKurtiLength = _cursor.getString(_cursorIndexOfKurtiLength);
            final String _tmpFullShoulder;
            _tmpFullShoulder = _cursor.getString(_cursorIndexOfFullShoulder);
            final String _tmpUpperChestRound;
            _tmpUpperChestRound = _cursor.getString(_cursorIndexOfUpperChestRound);
            final String _tmpChestRound;
            _tmpChestRound = _cursor.getString(_cursorIndexOfChestRound);
            final String _tmpWaistRound;
            _tmpWaistRound = _cursor.getString(_cursorIndexOfWaistRound);
            final String _tmpShoulderToApex;
            _tmpShoulderToApex = _cursor.getString(_cursorIndexOfShoulderToApex);
            final String _tmpApexToApex;
            _tmpApexToApex = _cursor.getString(_cursorIndexOfApexToApex);
            final String _tmpShoulderToLowChestLength;
            _tmpShoulderToLowChestLength = _cursor.getString(_cursorIndexOfShoulderToLowChestLength);
            final String _tmpSkapLength;
            _tmpSkapLength = _cursor.getString(_cursorIndexOfSkapLength);
            final String _tmpSkapLengthRound;
            _tmpSkapLengthRound = _cursor.getString(_cursorIndexOfSkapLengthRound);
            final String _tmpHipRound;
            _tmpHipRound = _cursor.getString(_cursorIndexOfHipRound);
            final String _tmpFrontNeckDeep;
            _tmpFrontNeckDeep = _cursor.getString(_cursorIndexOfFrontNeckDeep);
            final String _tmpFrontNeckWidth;
            _tmpFrontNeckWidth = _cursor.getString(_cursorIndexOfFrontNeckWidth);
            final String _tmpBackNeckDeep;
            _tmpBackNeckDeep = _cursor.getString(_cursorIndexOfBackNeckDeep);
            final String _tmpReadyShoulder;
            _tmpReadyShoulder = _cursor.getString(_cursorIndexOfReadyShoulder);
            final String _tmpSleevesHeightShort;
            _tmpSleevesHeightShort = _cursor.getString(_cursorIndexOfSleevesHeightShort);
            final String _tmpSleevesHeightElbow;
            _tmpSleevesHeightElbow = _cursor.getString(_cursorIndexOfSleevesHeightElbow);
            final String _tmpSleevesHeightThreeQuarter;
            _tmpSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfSleevesHeightThreeQuarter);
            final String _tmpSleevesRound;
            _tmpSleevesRound = _cursor.getString(_cursorIndexOfSleevesRound);
            final String _tmpPantWaist;
            _tmpPantWaist = _cursor.getString(_cursorIndexOfPantWaist);
            final String _tmpPantLength;
            _tmpPantLength = _cursor.getString(_cursorIndexOfPantLength);
            final String _tmpPantHip;
            _tmpPantHip = _cursor.getString(_cursorIndexOfPantHip);
            final String _tmpPantBottom;
            _tmpPantBottom = _cursor.getString(_cursorIndexOfPantBottom);
            final String _tmpBlouseLength;
            _tmpBlouseLength = _cursor.getString(_cursorIndexOfBlouseLength);
            final String _tmpBlouseFullShoulder;
            _tmpBlouseFullShoulder = _cursor.getString(_cursorIndexOfBlouseFullShoulder);
            final String _tmpBlouseChest;
            _tmpBlouseChest = _cursor.getString(_cursorIndexOfBlouseChest);
            final String _tmpBlouseWaist;
            _tmpBlouseWaist = _cursor.getString(_cursorIndexOfBlouseWaist);
            final String _tmpBlouseShoulderToApex;
            _tmpBlouseShoulderToApex = _cursor.getString(_cursorIndexOfBlouseShoulderToApex);
            final String _tmpBlouseApexToApex;
            _tmpBlouseApexToApex = _cursor.getString(_cursorIndexOfBlouseApexToApex);
            final String _tmpBlouseBackLength;
            _tmpBlouseBackLength = _cursor.getString(_cursorIndexOfBlouseBackLength);
            final String _tmpBlouseFrontNeckDeep;
            _tmpBlouseFrontNeckDeep = _cursor.getString(_cursorIndexOfBlouseFrontNeckDeep);
            final String _tmpBlouseFrontNeckWidth;
            _tmpBlouseFrontNeckWidth = _cursor.getString(_cursorIndexOfBlouseFrontNeckWidth);
            final String _tmpBlouseBackNeckDeep;
            _tmpBlouseBackNeckDeep = _cursor.getString(_cursorIndexOfBlouseBackNeckDeep);
            final String _tmpBlouseReadyShoulder;
            _tmpBlouseReadyShoulder = _cursor.getString(_cursorIndexOfBlouseReadyShoulder);
            final String _tmpBlouseSleevesHeightShort;
            _tmpBlouseSleevesHeightShort = _cursor.getString(_cursorIndexOfBlouseSleevesHeightShort);
            final String _tmpBlouseSleevesHeightElbow;
            _tmpBlouseSleevesHeightElbow = _cursor.getString(_cursorIndexOfBlouseSleevesHeightElbow);
            final String _tmpBlouseSleevesHeightThreeQuarter;
            _tmpBlouseSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfBlouseSleevesHeightThreeQuarter);
            final String _tmpBlouseSleevesRound;
            _tmpBlouseSleevesRound = _cursor.getString(_cursorIndexOfBlouseSleevesRound);
            final String _tmpBlouseHookOn;
            _tmpBlouseHookOn = _cursor.getString(_cursorIndexOfBlouseHookOn);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final String _tmpServerId;
            if (_cursor.isNull(_cursorIndexOfServerId)) {
              _tmpServerId = null;
            } else {
              _tmpServerId = _cursor.getString(_cursorIndexOfServerId);
            }
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _item = new Measurement(_tmpId,_tmpCustomerId,_tmpKurtiLength,_tmpFullShoulder,_tmpUpperChestRound,_tmpChestRound,_tmpWaistRound,_tmpShoulderToApex,_tmpApexToApex,_tmpShoulderToLowChestLength,_tmpSkapLength,_tmpSkapLengthRound,_tmpHipRound,_tmpFrontNeckDeep,_tmpFrontNeckWidth,_tmpBackNeckDeep,_tmpReadyShoulder,_tmpSleevesHeightShort,_tmpSleevesHeightElbow,_tmpSleevesHeightThreeQuarter,_tmpSleevesRound,_tmpPantWaist,_tmpPantLength,_tmpPantHip,_tmpPantBottom,_tmpBlouseLength,_tmpBlouseFullShoulder,_tmpBlouseChest,_tmpBlouseWaist,_tmpBlouseShoulderToApex,_tmpBlouseApexToApex,_tmpBlouseBackLength,_tmpBlouseFrontNeckDeep,_tmpBlouseFrontNeckWidth,_tmpBlouseBackNeckDeep,_tmpBlouseReadyShoulder,_tmpBlouseSleevesHeightShort,_tmpBlouseSleevesHeightElbow,_tmpBlouseSleevesHeightThreeQuarter,_tmpBlouseSleevesRound,_tmpBlouseHookOn,_tmpLastUpdated,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMeasurementsModifiedAfter(final long timestamp,
      final Continuation<? super List<Measurement>> $completion) {
    final String _sql = "SELECT * FROM measurements WHERE lastModified > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Measurement>>() {
      @Override
      @NonNull
      public List<Measurement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfKurtiLength = CursorUtil.getColumnIndexOrThrow(_cursor, "kurtiLength");
          final int _cursorIndexOfFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "fullShoulder");
          final int _cursorIndexOfUpperChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "upperChestRound");
          final int _cursorIndexOfChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "chestRound");
          final int _cursorIndexOfWaistRound = CursorUtil.getColumnIndexOrThrow(_cursor, "waistRound");
          final int _cursorIndexOfShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToApex");
          final int _cursorIndexOfApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "apexToApex");
          final int _cursorIndexOfShoulderToLowChestLength = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToLowChestLength");
          final int _cursorIndexOfSkapLength = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLength");
          final int _cursorIndexOfSkapLengthRound = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLengthRound");
          final int _cursorIndexOfHipRound = CursorUtil.getColumnIndexOrThrow(_cursor, "hipRound");
          final int _cursorIndexOfFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckDeep");
          final int _cursorIndexOfFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckWidth");
          final int _cursorIndexOfBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "backNeckDeep");
          final int _cursorIndexOfReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "readyShoulder");
          final int _cursorIndexOfSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightShort");
          final int _cursorIndexOfSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightElbow");
          final int _cursorIndexOfSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightThreeQuarter");
          final int _cursorIndexOfSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesRound");
          final int _cursorIndexOfPantWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "pantWaist");
          final int _cursorIndexOfPantLength = CursorUtil.getColumnIndexOrThrow(_cursor, "pantLength");
          final int _cursorIndexOfPantHip = CursorUtil.getColumnIndexOrThrow(_cursor, "pantHip");
          final int _cursorIndexOfPantBottom = CursorUtil.getColumnIndexOrThrow(_cursor, "pantBottom");
          final int _cursorIndexOfBlouseLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseLength");
          final int _cursorIndexOfBlouseFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFullShoulder");
          final int _cursorIndexOfBlouseChest = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseChest");
          final int _cursorIndexOfBlouseWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseWaist");
          final int _cursorIndexOfBlouseShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseShoulderToApex");
          final int _cursorIndexOfBlouseApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseApexToApex");
          final int _cursorIndexOfBlouseBackLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackLength");
          final int _cursorIndexOfBlouseFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckDeep");
          final int _cursorIndexOfBlouseFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckWidth");
          final int _cursorIndexOfBlouseBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackNeckDeep");
          final int _cursorIndexOfBlouseReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseReadyShoulder");
          final int _cursorIndexOfBlouseSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightShort");
          final int _cursorIndexOfBlouseSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightElbow");
          final int _cursorIndexOfBlouseSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightThreeQuarter");
          final int _cursorIndexOfBlouseSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesRound");
          final int _cursorIndexOfBlouseHookOn = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseHookOn");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<Measurement> _result = new ArrayList<Measurement>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Measurement _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpKurtiLength;
            _tmpKurtiLength = _cursor.getString(_cursorIndexOfKurtiLength);
            final String _tmpFullShoulder;
            _tmpFullShoulder = _cursor.getString(_cursorIndexOfFullShoulder);
            final String _tmpUpperChestRound;
            _tmpUpperChestRound = _cursor.getString(_cursorIndexOfUpperChestRound);
            final String _tmpChestRound;
            _tmpChestRound = _cursor.getString(_cursorIndexOfChestRound);
            final String _tmpWaistRound;
            _tmpWaistRound = _cursor.getString(_cursorIndexOfWaistRound);
            final String _tmpShoulderToApex;
            _tmpShoulderToApex = _cursor.getString(_cursorIndexOfShoulderToApex);
            final String _tmpApexToApex;
            _tmpApexToApex = _cursor.getString(_cursorIndexOfApexToApex);
            final String _tmpShoulderToLowChestLength;
            _tmpShoulderToLowChestLength = _cursor.getString(_cursorIndexOfShoulderToLowChestLength);
            final String _tmpSkapLength;
            _tmpSkapLength = _cursor.getString(_cursorIndexOfSkapLength);
            final String _tmpSkapLengthRound;
            _tmpSkapLengthRound = _cursor.getString(_cursorIndexOfSkapLengthRound);
            final String _tmpHipRound;
            _tmpHipRound = _cursor.getString(_cursorIndexOfHipRound);
            final String _tmpFrontNeckDeep;
            _tmpFrontNeckDeep = _cursor.getString(_cursorIndexOfFrontNeckDeep);
            final String _tmpFrontNeckWidth;
            _tmpFrontNeckWidth = _cursor.getString(_cursorIndexOfFrontNeckWidth);
            final String _tmpBackNeckDeep;
            _tmpBackNeckDeep = _cursor.getString(_cursorIndexOfBackNeckDeep);
            final String _tmpReadyShoulder;
            _tmpReadyShoulder = _cursor.getString(_cursorIndexOfReadyShoulder);
            final String _tmpSleevesHeightShort;
            _tmpSleevesHeightShort = _cursor.getString(_cursorIndexOfSleevesHeightShort);
            final String _tmpSleevesHeightElbow;
            _tmpSleevesHeightElbow = _cursor.getString(_cursorIndexOfSleevesHeightElbow);
            final String _tmpSleevesHeightThreeQuarter;
            _tmpSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfSleevesHeightThreeQuarter);
            final String _tmpSleevesRound;
            _tmpSleevesRound = _cursor.getString(_cursorIndexOfSleevesRound);
            final String _tmpPantWaist;
            _tmpPantWaist = _cursor.getString(_cursorIndexOfPantWaist);
            final String _tmpPantLength;
            _tmpPantLength = _cursor.getString(_cursorIndexOfPantLength);
            final String _tmpPantHip;
            _tmpPantHip = _cursor.getString(_cursorIndexOfPantHip);
            final String _tmpPantBottom;
            _tmpPantBottom = _cursor.getString(_cursorIndexOfPantBottom);
            final String _tmpBlouseLength;
            _tmpBlouseLength = _cursor.getString(_cursorIndexOfBlouseLength);
            final String _tmpBlouseFullShoulder;
            _tmpBlouseFullShoulder = _cursor.getString(_cursorIndexOfBlouseFullShoulder);
            final String _tmpBlouseChest;
            _tmpBlouseChest = _cursor.getString(_cursorIndexOfBlouseChest);
            final String _tmpBlouseWaist;
            _tmpBlouseWaist = _cursor.getString(_cursorIndexOfBlouseWaist);
            final String _tmpBlouseShoulderToApex;
            _tmpBlouseShoulderToApex = _cursor.getString(_cursorIndexOfBlouseShoulderToApex);
            final String _tmpBlouseApexToApex;
            _tmpBlouseApexToApex = _cursor.getString(_cursorIndexOfBlouseApexToApex);
            final String _tmpBlouseBackLength;
            _tmpBlouseBackLength = _cursor.getString(_cursorIndexOfBlouseBackLength);
            final String _tmpBlouseFrontNeckDeep;
            _tmpBlouseFrontNeckDeep = _cursor.getString(_cursorIndexOfBlouseFrontNeckDeep);
            final String _tmpBlouseFrontNeckWidth;
            _tmpBlouseFrontNeckWidth = _cursor.getString(_cursorIndexOfBlouseFrontNeckWidth);
            final String _tmpBlouseBackNeckDeep;
            _tmpBlouseBackNeckDeep = _cursor.getString(_cursorIndexOfBlouseBackNeckDeep);
            final String _tmpBlouseReadyShoulder;
            _tmpBlouseReadyShoulder = _cursor.getString(_cursorIndexOfBlouseReadyShoulder);
            final String _tmpBlouseSleevesHeightShort;
            _tmpBlouseSleevesHeightShort = _cursor.getString(_cursorIndexOfBlouseSleevesHeightShort);
            final String _tmpBlouseSleevesHeightElbow;
            _tmpBlouseSleevesHeightElbow = _cursor.getString(_cursorIndexOfBlouseSleevesHeightElbow);
            final String _tmpBlouseSleevesHeightThreeQuarter;
            _tmpBlouseSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfBlouseSleevesHeightThreeQuarter);
            final String _tmpBlouseSleevesRound;
            _tmpBlouseSleevesRound = _cursor.getString(_cursorIndexOfBlouseSleevesRound);
            final String _tmpBlouseHookOn;
            _tmpBlouseHookOn = _cursor.getString(_cursorIndexOfBlouseHookOn);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final String _tmpServerId;
            if (_cursor.isNull(_cursorIndexOfServerId)) {
              _tmpServerId = null;
            } else {
              _tmpServerId = _cursor.getString(_cursorIndexOfServerId);
            }
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _item = new Measurement(_tmpId,_tmpCustomerId,_tmpKurtiLength,_tmpFullShoulder,_tmpUpperChestRound,_tmpChestRound,_tmpWaistRound,_tmpShoulderToApex,_tmpApexToApex,_tmpShoulderToLowChestLength,_tmpSkapLength,_tmpSkapLengthRound,_tmpHipRound,_tmpFrontNeckDeep,_tmpFrontNeckWidth,_tmpBackNeckDeep,_tmpReadyShoulder,_tmpSleevesHeightShort,_tmpSleevesHeightElbow,_tmpSleevesHeightThreeQuarter,_tmpSleevesRound,_tmpPantWaist,_tmpPantLength,_tmpPantHip,_tmpPantBottom,_tmpBlouseLength,_tmpBlouseFullShoulder,_tmpBlouseChest,_tmpBlouseWaist,_tmpBlouseShoulderToApex,_tmpBlouseApexToApex,_tmpBlouseBackLength,_tmpBlouseFrontNeckDeep,_tmpBlouseFrontNeckWidth,_tmpBlouseBackNeckDeep,_tmpBlouseReadyShoulder,_tmpBlouseSleevesHeightShort,_tmpBlouseSleevesHeightElbow,_tmpBlouseSleevesHeightThreeQuarter,_tmpBlouseSleevesRound,_tmpBlouseHookOn,_tmpLastUpdated,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMeasurementByServerId(final String serverId,
      final Continuation<? super Measurement> $completion) {
    final String _sql = "SELECT * FROM measurements WHERE serverId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, serverId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Measurement>() {
      @Override
      @Nullable
      public Measurement call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfKurtiLength = CursorUtil.getColumnIndexOrThrow(_cursor, "kurtiLength");
          final int _cursorIndexOfFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "fullShoulder");
          final int _cursorIndexOfUpperChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "upperChestRound");
          final int _cursorIndexOfChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "chestRound");
          final int _cursorIndexOfWaistRound = CursorUtil.getColumnIndexOrThrow(_cursor, "waistRound");
          final int _cursorIndexOfShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToApex");
          final int _cursorIndexOfApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "apexToApex");
          final int _cursorIndexOfShoulderToLowChestLength = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToLowChestLength");
          final int _cursorIndexOfSkapLength = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLength");
          final int _cursorIndexOfSkapLengthRound = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLengthRound");
          final int _cursorIndexOfHipRound = CursorUtil.getColumnIndexOrThrow(_cursor, "hipRound");
          final int _cursorIndexOfFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckDeep");
          final int _cursorIndexOfFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckWidth");
          final int _cursorIndexOfBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "backNeckDeep");
          final int _cursorIndexOfReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "readyShoulder");
          final int _cursorIndexOfSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightShort");
          final int _cursorIndexOfSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightElbow");
          final int _cursorIndexOfSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightThreeQuarter");
          final int _cursorIndexOfSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesRound");
          final int _cursorIndexOfPantWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "pantWaist");
          final int _cursorIndexOfPantLength = CursorUtil.getColumnIndexOrThrow(_cursor, "pantLength");
          final int _cursorIndexOfPantHip = CursorUtil.getColumnIndexOrThrow(_cursor, "pantHip");
          final int _cursorIndexOfPantBottom = CursorUtil.getColumnIndexOrThrow(_cursor, "pantBottom");
          final int _cursorIndexOfBlouseLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseLength");
          final int _cursorIndexOfBlouseFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFullShoulder");
          final int _cursorIndexOfBlouseChest = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseChest");
          final int _cursorIndexOfBlouseWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseWaist");
          final int _cursorIndexOfBlouseShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseShoulderToApex");
          final int _cursorIndexOfBlouseApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseApexToApex");
          final int _cursorIndexOfBlouseBackLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackLength");
          final int _cursorIndexOfBlouseFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckDeep");
          final int _cursorIndexOfBlouseFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckWidth");
          final int _cursorIndexOfBlouseBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackNeckDeep");
          final int _cursorIndexOfBlouseReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseReadyShoulder");
          final int _cursorIndexOfBlouseSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightShort");
          final int _cursorIndexOfBlouseSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightElbow");
          final int _cursorIndexOfBlouseSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightThreeQuarter");
          final int _cursorIndexOfBlouseSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesRound");
          final int _cursorIndexOfBlouseHookOn = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseHookOn");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final Measurement _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpKurtiLength;
            _tmpKurtiLength = _cursor.getString(_cursorIndexOfKurtiLength);
            final String _tmpFullShoulder;
            _tmpFullShoulder = _cursor.getString(_cursorIndexOfFullShoulder);
            final String _tmpUpperChestRound;
            _tmpUpperChestRound = _cursor.getString(_cursorIndexOfUpperChestRound);
            final String _tmpChestRound;
            _tmpChestRound = _cursor.getString(_cursorIndexOfChestRound);
            final String _tmpWaistRound;
            _tmpWaistRound = _cursor.getString(_cursorIndexOfWaistRound);
            final String _tmpShoulderToApex;
            _tmpShoulderToApex = _cursor.getString(_cursorIndexOfShoulderToApex);
            final String _tmpApexToApex;
            _tmpApexToApex = _cursor.getString(_cursorIndexOfApexToApex);
            final String _tmpShoulderToLowChestLength;
            _tmpShoulderToLowChestLength = _cursor.getString(_cursorIndexOfShoulderToLowChestLength);
            final String _tmpSkapLength;
            _tmpSkapLength = _cursor.getString(_cursorIndexOfSkapLength);
            final String _tmpSkapLengthRound;
            _tmpSkapLengthRound = _cursor.getString(_cursorIndexOfSkapLengthRound);
            final String _tmpHipRound;
            _tmpHipRound = _cursor.getString(_cursorIndexOfHipRound);
            final String _tmpFrontNeckDeep;
            _tmpFrontNeckDeep = _cursor.getString(_cursorIndexOfFrontNeckDeep);
            final String _tmpFrontNeckWidth;
            _tmpFrontNeckWidth = _cursor.getString(_cursorIndexOfFrontNeckWidth);
            final String _tmpBackNeckDeep;
            _tmpBackNeckDeep = _cursor.getString(_cursorIndexOfBackNeckDeep);
            final String _tmpReadyShoulder;
            _tmpReadyShoulder = _cursor.getString(_cursorIndexOfReadyShoulder);
            final String _tmpSleevesHeightShort;
            _tmpSleevesHeightShort = _cursor.getString(_cursorIndexOfSleevesHeightShort);
            final String _tmpSleevesHeightElbow;
            _tmpSleevesHeightElbow = _cursor.getString(_cursorIndexOfSleevesHeightElbow);
            final String _tmpSleevesHeightThreeQuarter;
            _tmpSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfSleevesHeightThreeQuarter);
            final String _tmpSleevesRound;
            _tmpSleevesRound = _cursor.getString(_cursorIndexOfSleevesRound);
            final String _tmpPantWaist;
            _tmpPantWaist = _cursor.getString(_cursorIndexOfPantWaist);
            final String _tmpPantLength;
            _tmpPantLength = _cursor.getString(_cursorIndexOfPantLength);
            final String _tmpPantHip;
            _tmpPantHip = _cursor.getString(_cursorIndexOfPantHip);
            final String _tmpPantBottom;
            _tmpPantBottom = _cursor.getString(_cursorIndexOfPantBottom);
            final String _tmpBlouseLength;
            _tmpBlouseLength = _cursor.getString(_cursorIndexOfBlouseLength);
            final String _tmpBlouseFullShoulder;
            _tmpBlouseFullShoulder = _cursor.getString(_cursorIndexOfBlouseFullShoulder);
            final String _tmpBlouseChest;
            _tmpBlouseChest = _cursor.getString(_cursorIndexOfBlouseChest);
            final String _tmpBlouseWaist;
            _tmpBlouseWaist = _cursor.getString(_cursorIndexOfBlouseWaist);
            final String _tmpBlouseShoulderToApex;
            _tmpBlouseShoulderToApex = _cursor.getString(_cursorIndexOfBlouseShoulderToApex);
            final String _tmpBlouseApexToApex;
            _tmpBlouseApexToApex = _cursor.getString(_cursorIndexOfBlouseApexToApex);
            final String _tmpBlouseBackLength;
            _tmpBlouseBackLength = _cursor.getString(_cursorIndexOfBlouseBackLength);
            final String _tmpBlouseFrontNeckDeep;
            _tmpBlouseFrontNeckDeep = _cursor.getString(_cursorIndexOfBlouseFrontNeckDeep);
            final String _tmpBlouseFrontNeckWidth;
            _tmpBlouseFrontNeckWidth = _cursor.getString(_cursorIndexOfBlouseFrontNeckWidth);
            final String _tmpBlouseBackNeckDeep;
            _tmpBlouseBackNeckDeep = _cursor.getString(_cursorIndexOfBlouseBackNeckDeep);
            final String _tmpBlouseReadyShoulder;
            _tmpBlouseReadyShoulder = _cursor.getString(_cursorIndexOfBlouseReadyShoulder);
            final String _tmpBlouseSleevesHeightShort;
            _tmpBlouseSleevesHeightShort = _cursor.getString(_cursorIndexOfBlouseSleevesHeightShort);
            final String _tmpBlouseSleevesHeightElbow;
            _tmpBlouseSleevesHeightElbow = _cursor.getString(_cursorIndexOfBlouseSleevesHeightElbow);
            final String _tmpBlouseSleevesHeightThreeQuarter;
            _tmpBlouseSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfBlouseSleevesHeightThreeQuarter);
            final String _tmpBlouseSleevesRound;
            _tmpBlouseSleevesRound = _cursor.getString(_cursorIndexOfBlouseSleevesRound);
            final String _tmpBlouseHookOn;
            _tmpBlouseHookOn = _cursor.getString(_cursorIndexOfBlouseHookOn);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final String _tmpServerId;
            if (_cursor.isNull(_cursorIndexOfServerId)) {
              _tmpServerId = null;
            } else {
              _tmpServerId = _cursor.getString(_cursorIndexOfServerId);
            }
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _result = new Measurement(_tmpId,_tmpCustomerId,_tmpKurtiLength,_tmpFullShoulder,_tmpUpperChestRound,_tmpChestRound,_tmpWaistRound,_tmpShoulderToApex,_tmpApexToApex,_tmpShoulderToLowChestLength,_tmpSkapLength,_tmpSkapLengthRound,_tmpHipRound,_tmpFrontNeckDeep,_tmpFrontNeckWidth,_tmpBackNeckDeep,_tmpReadyShoulder,_tmpSleevesHeightShort,_tmpSleevesHeightElbow,_tmpSleevesHeightThreeQuarter,_tmpSleevesRound,_tmpPantWaist,_tmpPantLength,_tmpPantHip,_tmpPantBottom,_tmpBlouseLength,_tmpBlouseFullShoulder,_tmpBlouseChest,_tmpBlouseWaist,_tmpBlouseShoulderToApex,_tmpBlouseApexToApex,_tmpBlouseBackLength,_tmpBlouseFrontNeckDeep,_tmpBlouseFrontNeckWidth,_tmpBlouseBackNeckDeep,_tmpBlouseReadyShoulder,_tmpBlouseSleevesHeightShort,_tmpBlouseSleevesHeightElbow,_tmpBlouseSleevesHeightThreeQuarter,_tmpBlouseSleevesRound,_tmpBlouseHookOn,_tmpLastUpdated,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllMeasurements(final Continuation<? super List<Measurement>> $completion) {
    final String _sql = "SELECT * FROM measurements";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Measurement>>() {
      @Override
      @NonNull
      public List<Measurement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfKurtiLength = CursorUtil.getColumnIndexOrThrow(_cursor, "kurtiLength");
          final int _cursorIndexOfFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "fullShoulder");
          final int _cursorIndexOfUpperChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "upperChestRound");
          final int _cursorIndexOfChestRound = CursorUtil.getColumnIndexOrThrow(_cursor, "chestRound");
          final int _cursorIndexOfWaistRound = CursorUtil.getColumnIndexOrThrow(_cursor, "waistRound");
          final int _cursorIndexOfShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToApex");
          final int _cursorIndexOfApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "apexToApex");
          final int _cursorIndexOfShoulderToLowChestLength = CursorUtil.getColumnIndexOrThrow(_cursor, "shoulderToLowChestLength");
          final int _cursorIndexOfSkapLength = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLength");
          final int _cursorIndexOfSkapLengthRound = CursorUtil.getColumnIndexOrThrow(_cursor, "skapLengthRound");
          final int _cursorIndexOfHipRound = CursorUtil.getColumnIndexOrThrow(_cursor, "hipRound");
          final int _cursorIndexOfFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckDeep");
          final int _cursorIndexOfFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "frontNeckWidth");
          final int _cursorIndexOfBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "backNeckDeep");
          final int _cursorIndexOfReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "readyShoulder");
          final int _cursorIndexOfSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightShort");
          final int _cursorIndexOfSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightElbow");
          final int _cursorIndexOfSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesHeightThreeQuarter");
          final int _cursorIndexOfSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "sleevesRound");
          final int _cursorIndexOfPantWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "pantWaist");
          final int _cursorIndexOfPantLength = CursorUtil.getColumnIndexOrThrow(_cursor, "pantLength");
          final int _cursorIndexOfPantHip = CursorUtil.getColumnIndexOrThrow(_cursor, "pantHip");
          final int _cursorIndexOfPantBottom = CursorUtil.getColumnIndexOrThrow(_cursor, "pantBottom");
          final int _cursorIndexOfBlouseLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseLength");
          final int _cursorIndexOfBlouseFullShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFullShoulder");
          final int _cursorIndexOfBlouseChest = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseChest");
          final int _cursorIndexOfBlouseWaist = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseWaist");
          final int _cursorIndexOfBlouseShoulderToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseShoulderToApex");
          final int _cursorIndexOfBlouseApexToApex = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseApexToApex");
          final int _cursorIndexOfBlouseBackLength = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackLength");
          final int _cursorIndexOfBlouseFrontNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckDeep");
          final int _cursorIndexOfBlouseFrontNeckWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseFrontNeckWidth");
          final int _cursorIndexOfBlouseBackNeckDeep = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseBackNeckDeep");
          final int _cursorIndexOfBlouseReadyShoulder = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseReadyShoulder");
          final int _cursorIndexOfBlouseSleevesHeightShort = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightShort");
          final int _cursorIndexOfBlouseSleevesHeightElbow = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightElbow");
          final int _cursorIndexOfBlouseSleevesHeightThreeQuarter = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesHeightThreeQuarter");
          final int _cursorIndexOfBlouseSleevesRound = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseSleevesRound");
          final int _cursorIndexOfBlouseHookOn = CursorUtil.getColumnIndexOrThrow(_cursor, "blouseHookOn");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<Measurement> _result = new ArrayList<Measurement>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Measurement _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpKurtiLength;
            _tmpKurtiLength = _cursor.getString(_cursorIndexOfKurtiLength);
            final String _tmpFullShoulder;
            _tmpFullShoulder = _cursor.getString(_cursorIndexOfFullShoulder);
            final String _tmpUpperChestRound;
            _tmpUpperChestRound = _cursor.getString(_cursorIndexOfUpperChestRound);
            final String _tmpChestRound;
            _tmpChestRound = _cursor.getString(_cursorIndexOfChestRound);
            final String _tmpWaistRound;
            _tmpWaistRound = _cursor.getString(_cursorIndexOfWaistRound);
            final String _tmpShoulderToApex;
            _tmpShoulderToApex = _cursor.getString(_cursorIndexOfShoulderToApex);
            final String _tmpApexToApex;
            _tmpApexToApex = _cursor.getString(_cursorIndexOfApexToApex);
            final String _tmpShoulderToLowChestLength;
            _tmpShoulderToLowChestLength = _cursor.getString(_cursorIndexOfShoulderToLowChestLength);
            final String _tmpSkapLength;
            _tmpSkapLength = _cursor.getString(_cursorIndexOfSkapLength);
            final String _tmpSkapLengthRound;
            _tmpSkapLengthRound = _cursor.getString(_cursorIndexOfSkapLengthRound);
            final String _tmpHipRound;
            _tmpHipRound = _cursor.getString(_cursorIndexOfHipRound);
            final String _tmpFrontNeckDeep;
            _tmpFrontNeckDeep = _cursor.getString(_cursorIndexOfFrontNeckDeep);
            final String _tmpFrontNeckWidth;
            _tmpFrontNeckWidth = _cursor.getString(_cursorIndexOfFrontNeckWidth);
            final String _tmpBackNeckDeep;
            _tmpBackNeckDeep = _cursor.getString(_cursorIndexOfBackNeckDeep);
            final String _tmpReadyShoulder;
            _tmpReadyShoulder = _cursor.getString(_cursorIndexOfReadyShoulder);
            final String _tmpSleevesHeightShort;
            _tmpSleevesHeightShort = _cursor.getString(_cursorIndexOfSleevesHeightShort);
            final String _tmpSleevesHeightElbow;
            _tmpSleevesHeightElbow = _cursor.getString(_cursorIndexOfSleevesHeightElbow);
            final String _tmpSleevesHeightThreeQuarter;
            _tmpSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfSleevesHeightThreeQuarter);
            final String _tmpSleevesRound;
            _tmpSleevesRound = _cursor.getString(_cursorIndexOfSleevesRound);
            final String _tmpPantWaist;
            _tmpPantWaist = _cursor.getString(_cursorIndexOfPantWaist);
            final String _tmpPantLength;
            _tmpPantLength = _cursor.getString(_cursorIndexOfPantLength);
            final String _tmpPantHip;
            _tmpPantHip = _cursor.getString(_cursorIndexOfPantHip);
            final String _tmpPantBottom;
            _tmpPantBottom = _cursor.getString(_cursorIndexOfPantBottom);
            final String _tmpBlouseLength;
            _tmpBlouseLength = _cursor.getString(_cursorIndexOfBlouseLength);
            final String _tmpBlouseFullShoulder;
            _tmpBlouseFullShoulder = _cursor.getString(_cursorIndexOfBlouseFullShoulder);
            final String _tmpBlouseChest;
            _tmpBlouseChest = _cursor.getString(_cursorIndexOfBlouseChest);
            final String _tmpBlouseWaist;
            _tmpBlouseWaist = _cursor.getString(_cursorIndexOfBlouseWaist);
            final String _tmpBlouseShoulderToApex;
            _tmpBlouseShoulderToApex = _cursor.getString(_cursorIndexOfBlouseShoulderToApex);
            final String _tmpBlouseApexToApex;
            _tmpBlouseApexToApex = _cursor.getString(_cursorIndexOfBlouseApexToApex);
            final String _tmpBlouseBackLength;
            _tmpBlouseBackLength = _cursor.getString(_cursorIndexOfBlouseBackLength);
            final String _tmpBlouseFrontNeckDeep;
            _tmpBlouseFrontNeckDeep = _cursor.getString(_cursorIndexOfBlouseFrontNeckDeep);
            final String _tmpBlouseFrontNeckWidth;
            _tmpBlouseFrontNeckWidth = _cursor.getString(_cursorIndexOfBlouseFrontNeckWidth);
            final String _tmpBlouseBackNeckDeep;
            _tmpBlouseBackNeckDeep = _cursor.getString(_cursorIndexOfBlouseBackNeckDeep);
            final String _tmpBlouseReadyShoulder;
            _tmpBlouseReadyShoulder = _cursor.getString(_cursorIndexOfBlouseReadyShoulder);
            final String _tmpBlouseSleevesHeightShort;
            _tmpBlouseSleevesHeightShort = _cursor.getString(_cursorIndexOfBlouseSleevesHeightShort);
            final String _tmpBlouseSleevesHeightElbow;
            _tmpBlouseSleevesHeightElbow = _cursor.getString(_cursorIndexOfBlouseSleevesHeightElbow);
            final String _tmpBlouseSleevesHeightThreeQuarter;
            _tmpBlouseSleevesHeightThreeQuarter = _cursor.getString(_cursorIndexOfBlouseSleevesHeightThreeQuarter);
            final String _tmpBlouseSleevesRound;
            _tmpBlouseSleevesRound = _cursor.getString(_cursorIndexOfBlouseSleevesRound);
            final String _tmpBlouseHookOn;
            _tmpBlouseHookOn = _cursor.getString(_cursorIndexOfBlouseHookOn);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final String _tmpServerId;
            if (_cursor.isNull(_cursorIndexOfServerId)) {
              _tmpServerId = null;
            } else {
              _tmpServerId = _cursor.getString(_cursorIndexOfServerId);
            }
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            _item = new Measurement(_tmpId,_tmpCustomerId,_tmpKurtiLength,_tmpFullShoulder,_tmpUpperChestRound,_tmpChestRound,_tmpWaistRound,_tmpShoulderToApex,_tmpApexToApex,_tmpShoulderToLowChestLength,_tmpSkapLength,_tmpSkapLengthRound,_tmpHipRound,_tmpFrontNeckDeep,_tmpFrontNeckWidth,_tmpBackNeckDeep,_tmpReadyShoulder,_tmpSleevesHeightShort,_tmpSleevesHeightElbow,_tmpSleevesHeightThreeQuarter,_tmpSleevesRound,_tmpPantWaist,_tmpPantLength,_tmpPantHip,_tmpPantBottom,_tmpBlouseLength,_tmpBlouseFullShoulder,_tmpBlouseChest,_tmpBlouseWaist,_tmpBlouseShoulderToApex,_tmpBlouseApexToApex,_tmpBlouseBackLength,_tmpBlouseFrontNeckDeep,_tmpBlouseFrontNeckWidth,_tmpBlouseBackNeckDeep,_tmpBlouseReadyShoulder,_tmpBlouseSleevesHeightShort,_tmpBlouseSleevesHeightElbow,_tmpBlouseSleevesHeightThreeQuarter,_tmpBlouseSleevesRound,_tmpBlouseHookOn,_tmpLastUpdated,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
