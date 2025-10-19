package com.example.perfectfit.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.perfectfit.models.WorkloadConfig;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WorkloadConfigDao_Impl implements WorkloadConfigDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkloadConfig> __insertionAdapterOfWorkloadConfig;

  private final EntityDeletionOrUpdateAdapter<WorkloadConfig> __updateAdapterOfWorkloadConfig;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllConfigs;

  public WorkloadConfigDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkloadConfig = new EntityInsertionAdapter<WorkloadConfig>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `workload_config` (`id`,`timePerOrderHours`,`mondayHours`,`tuesdayHours`,`wednesdayHours`,`thursdayHours`,`fridayHours`,`saturdayHours`,`sundayHours`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkloadConfig entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getTimePerOrderHours());
        statement.bindDouble(3, entity.getMondayHours());
        statement.bindDouble(4, entity.getTuesdayHours());
        statement.bindDouble(5, entity.getWednesdayHours());
        statement.bindDouble(6, entity.getThursdayHours());
        statement.bindDouble(7, entity.getFridayHours());
        statement.bindDouble(8, entity.getSaturdayHours());
        statement.bindDouble(9, entity.getSundayHours());
      }
    };
    this.__updateAdapterOfWorkloadConfig = new EntityDeletionOrUpdateAdapter<WorkloadConfig>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `workload_config` SET `id` = ?,`timePerOrderHours` = ?,`mondayHours` = ?,`tuesdayHours` = ?,`wednesdayHours` = ?,`thursdayHours` = ?,`fridayHours` = ?,`saturdayHours` = ?,`sundayHours` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkloadConfig entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getTimePerOrderHours());
        statement.bindDouble(3, entity.getMondayHours());
        statement.bindDouble(4, entity.getTuesdayHours());
        statement.bindDouble(5, entity.getWednesdayHours());
        statement.bindDouble(6, entity.getThursdayHours());
        statement.bindDouble(7, entity.getFridayHours());
        statement.bindDouble(8, entity.getSaturdayHours());
        statement.bindDouble(9, entity.getSundayHours());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllConfigs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM workload_config";
        return _query;
      }
    };
  }

  @Override
  public Object insertConfig(final WorkloadConfig config,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfWorkloadConfig.insertAndReturnId(config);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateConfig(final WorkloadConfig config,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWorkloadConfig.handle(config);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllConfigs(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllConfigs.acquire();
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
          __preparedStmtOfDeleteAllConfigs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getConfig(final Continuation<? super WorkloadConfig> $completion) {
    final String _sql = "SELECT * FROM workload_config LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WorkloadConfig>() {
      @Override
      @Nullable
      public WorkloadConfig call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimePerOrderHours = CursorUtil.getColumnIndexOrThrow(_cursor, "timePerOrderHours");
          final int _cursorIndexOfMondayHours = CursorUtil.getColumnIndexOrThrow(_cursor, "mondayHours");
          final int _cursorIndexOfTuesdayHours = CursorUtil.getColumnIndexOrThrow(_cursor, "tuesdayHours");
          final int _cursorIndexOfWednesdayHours = CursorUtil.getColumnIndexOrThrow(_cursor, "wednesdayHours");
          final int _cursorIndexOfThursdayHours = CursorUtil.getColumnIndexOrThrow(_cursor, "thursdayHours");
          final int _cursorIndexOfFridayHours = CursorUtil.getColumnIndexOrThrow(_cursor, "fridayHours");
          final int _cursorIndexOfSaturdayHours = CursorUtil.getColumnIndexOrThrow(_cursor, "saturdayHours");
          final int _cursorIndexOfSundayHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sundayHours");
          final WorkloadConfig _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final float _tmpTimePerOrderHours;
            _tmpTimePerOrderHours = _cursor.getFloat(_cursorIndexOfTimePerOrderHours);
            final float _tmpMondayHours;
            _tmpMondayHours = _cursor.getFloat(_cursorIndexOfMondayHours);
            final float _tmpTuesdayHours;
            _tmpTuesdayHours = _cursor.getFloat(_cursorIndexOfTuesdayHours);
            final float _tmpWednesdayHours;
            _tmpWednesdayHours = _cursor.getFloat(_cursorIndexOfWednesdayHours);
            final float _tmpThursdayHours;
            _tmpThursdayHours = _cursor.getFloat(_cursorIndexOfThursdayHours);
            final float _tmpFridayHours;
            _tmpFridayHours = _cursor.getFloat(_cursorIndexOfFridayHours);
            final float _tmpSaturdayHours;
            _tmpSaturdayHours = _cursor.getFloat(_cursorIndexOfSaturdayHours);
            final float _tmpSundayHours;
            _tmpSundayHours = _cursor.getFloat(_cursorIndexOfSundayHours);
            _result = new WorkloadConfig(_tmpId,_tmpTimePerOrderHours,_tmpMondayHours,_tmpTuesdayHours,_tmpWednesdayHours,_tmpThursdayHours,_tmpFridayHours,_tmpSaturdayHours,_tmpSundayHours);
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
  public Object getOrCreateConfig(final Continuation<? super WorkloadConfig> $completion) {
    return WorkloadConfigDao.DefaultImpls.getOrCreateConfig(WorkloadConfigDao_Impl.this, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
