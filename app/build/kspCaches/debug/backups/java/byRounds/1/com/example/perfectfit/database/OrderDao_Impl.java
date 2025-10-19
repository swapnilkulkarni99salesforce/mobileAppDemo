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
import com.example.perfectfit.models.Order;
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
public final class OrderDao_Impl implements OrderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Order> __insertionAdapterOfOrder;

  private final EntityDeletionOrUpdateAdapter<Order> __updateAdapterOfOrder;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOrder;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateServerInfo;

  public OrderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOrder = new EntityInsertionAdapter<Order>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `orders` (`id`,`customerId`,`customerName`,`orderDate`,`orderType`,`estimatedDeliveryDate`,`instructions`,`amount`,`status`,`advancePayment`,`balancePayment`,`paymentStatus`,`paymentDate`,`serverId`,`lastModified`,`syncStatus`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Order entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        statement.bindString(3, entity.getCustomerName());
        statement.bindString(4, entity.getOrderDate());
        statement.bindString(5, entity.getOrderType());
        statement.bindString(6, entity.getEstimatedDeliveryDate());
        statement.bindString(7, entity.getInstructions());
        statement.bindDouble(8, entity.getAmount());
        statement.bindString(9, entity.getStatus());
        statement.bindDouble(10, entity.getAdvancePayment());
        statement.bindDouble(11, entity.getBalancePayment());
        statement.bindString(12, entity.getPaymentStatus());
        if (entity.getPaymentDate() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getPaymentDate());
        }
        if (entity.getServerId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getServerId());
        }
        statement.bindLong(15, entity.getLastModified());
        statement.bindString(16, entity.getSyncStatus());
      }
    };
    this.__updateAdapterOfOrder = new EntityDeletionOrUpdateAdapter<Order>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `orders` SET `id` = ?,`customerId` = ?,`customerName` = ?,`orderDate` = ?,`orderType` = ?,`estimatedDeliveryDate` = ?,`instructions` = ?,`amount` = ?,`status` = ?,`advancePayment` = ?,`balancePayment` = ?,`paymentStatus` = ?,`paymentDate` = ?,`serverId` = ?,`lastModified` = ?,`syncStatus` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Order entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        statement.bindString(3, entity.getCustomerName());
        statement.bindString(4, entity.getOrderDate());
        statement.bindString(5, entity.getOrderType());
        statement.bindString(6, entity.getEstimatedDeliveryDate());
        statement.bindString(7, entity.getInstructions());
        statement.bindDouble(8, entity.getAmount());
        statement.bindString(9, entity.getStatus());
        statement.bindDouble(10, entity.getAdvancePayment());
        statement.bindDouble(11, entity.getBalancePayment());
        statement.bindString(12, entity.getPaymentStatus());
        if (entity.getPaymentDate() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getPaymentDate());
        }
        if (entity.getServerId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getServerId());
        }
        statement.bindLong(15, entity.getLastModified());
        statement.bindString(16, entity.getSyncStatus());
        statement.bindLong(17, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteOrder = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM orders WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE orders SET syncStatus = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateServerInfo = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE orders SET serverId = ?, syncStatus = ?, lastModified = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Order order, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfOrder.insertAndReturnId(order);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Order order, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfOrder.handle(order);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOrder(final int orderId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOrder.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, orderId);
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
          __preparedStmtOfDeleteOrder.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final int orderId, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, orderId);
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
  public Object getAllOrders(final Continuation<? super List<Order>> $completion) {
    final String _sql = "SELECT * FROM orders ORDER BY orderDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Order>>() {
      @Override
      @NonNull
      public List<Order> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfOrderDate = CursorUtil.getColumnIndexOrThrow(_cursor, "orderDate");
          final int _cursorIndexOfOrderType = CursorUtil.getColumnIndexOrThrow(_cursor, "orderType");
          final int _cursorIndexOfEstimatedDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryDate");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAdvancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "advancePayment");
          final int _cursorIndexOfBalancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "balancePayment");
          final int _cursorIndexOfPaymentStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentStatus");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<Order> _result = new ArrayList<Order>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Order _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpOrderDate;
            _tmpOrderDate = _cursor.getString(_cursorIndexOfOrderDate);
            final String _tmpOrderType;
            _tmpOrderType = _cursor.getString(_cursorIndexOfOrderType);
            final String _tmpEstimatedDeliveryDate;
            _tmpEstimatedDeliveryDate = _cursor.getString(_cursorIndexOfEstimatedDeliveryDate);
            final String _tmpInstructions;
            _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final double _tmpAdvancePayment;
            _tmpAdvancePayment = _cursor.getDouble(_cursorIndexOfAdvancePayment);
            final double _tmpBalancePayment;
            _tmpBalancePayment = _cursor.getDouble(_cursorIndexOfBalancePayment);
            final String _tmpPaymentStatus;
            _tmpPaymentStatus = _cursor.getString(_cursorIndexOfPaymentStatus);
            final String _tmpPaymentDate;
            if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
              _tmpPaymentDate = null;
            } else {
              _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
            }
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
            _item = new Order(_tmpId,_tmpCustomerId,_tmpCustomerName,_tmpOrderDate,_tmpOrderType,_tmpEstimatedDeliveryDate,_tmpInstructions,_tmpAmount,_tmpStatus,_tmpAdvancePayment,_tmpBalancePayment,_tmpPaymentStatus,_tmpPaymentDate,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
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
  public Object getOrdersByCustomerId(final int customerId,
      final Continuation<? super List<Order>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE customerId = ? ORDER BY orderDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, customerId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Order>>() {
      @Override
      @NonNull
      public List<Order> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfOrderDate = CursorUtil.getColumnIndexOrThrow(_cursor, "orderDate");
          final int _cursorIndexOfOrderType = CursorUtil.getColumnIndexOrThrow(_cursor, "orderType");
          final int _cursorIndexOfEstimatedDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryDate");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAdvancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "advancePayment");
          final int _cursorIndexOfBalancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "balancePayment");
          final int _cursorIndexOfPaymentStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentStatus");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<Order> _result = new ArrayList<Order>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Order _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpOrderDate;
            _tmpOrderDate = _cursor.getString(_cursorIndexOfOrderDate);
            final String _tmpOrderType;
            _tmpOrderType = _cursor.getString(_cursorIndexOfOrderType);
            final String _tmpEstimatedDeliveryDate;
            _tmpEstimatedDeliveryDate = _cursor.getString(_cursorIndexOfEstimatedDeliveryDate);
            final String _tmpInstructions;
            _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final double _tmpAdvancePayment;
            _tmpAdvancePayment = _cursor.getDouble(_cursorIndexOfAdvancePayment);
            final double _tmpBalancePayment;
            _tmpBalancePayment = _cursor.getDouble(_cursorIndexOfBalancePayment);
            final String _tmpPaymentStatus;
            _tmpPaymentStatus = _cursor.getString(_cursorIndexOfPaymentStatus);
            final String _tmpPaymentDate;
            if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
              _tmpPaymentDate = null;
            } else {
              _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
            }
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
            _item = new Order(_tmpId,_tmpCustomerId,_tmpCustomerName,_tmpOrderDate,_tmpOrderType,_tmpEstimatedDeliveryDate,_tmpInstructions,_tmpAmount,_tmpStatus,_tmpAdvancePayment,_tmpBalancePayment,_tmpPaymentStatus,_tmpPaymentDate,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
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
  public Object getOrderById(final int orderId, final Continuation<? super Order> $completion) {
    final String _sql = "SELECT * FROM orders WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, orderId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Order>() {
      @Override
      @Nullable
      public Order call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfOrderDate = CursorUtil.getColumnIndexOrThrow(_cursor, "orderDate");
          final int _cursorIndexOfOrderType = CursorUtil.getColumnIndexOrThrow(_cursor, "orderType");
          final int _cursorIndexOfEstimatedDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryDate");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAdvancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "advancePayment");
          final int _cursorIndexOfBalancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "balancePayment");
          final int _cursorIndexOfPaymentStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentStatus");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final Order _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpOrderDate;
            _tmpOrderDate = _cursor.getString(_cursorIndexOfOrderDate);
            final String _tmpOrderType;
            _tmpOrderType = _cursor.getString(_cursorIndexOfOrderType);
            final String _tmpEstimatedDeliveryDate;
            _tmpEstimatedDeliveryDate = _cursor.getString(_cursorIndexOfEstimatedDeliveryDate);
            final String _tmpInstructions;
            _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final double _tmpAdvancePayment;
            _tmpAdvancePayment = _cursor.getDouble(_cursorIndexOfAdvancePayment);
            final double _tmpBalancePayment;
            _tmpBalancePayment = _cursor.getDouble(_cursorIndexOfBalancePayment);
            final String _tmpPaymentStatus;
            _tmpPaymentStatus = _cursor.getString(_cursorIndexOfPaymentStatus);
            final String _tmpPaymentDate;
            if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
              _tmpPaymentDate = null;
            } else {
              _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
            }
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
            _result = new Order(_tmpId,_tmpCustomerId,_tmpCustomerName,_tmpOrderDate,_tmpOrderType,_tmpEstimatedDeliveryDate,_tmpInstructions,_tmpAmount,_tmpStatus,_tmpAdvancePayment,_tmpBalancePayment,_tmpPaymentStatus,_tmpPaymentDate,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
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
  public Object getOrdersBySyncStatus(final String status,
      final Continuation<? super List<Order>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE syncStatus = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Order>>() {
      @Override
      @NonNull
      public List<Order> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfOrderDate = CursorUtil.getColumnIndexOrThrow(_cursor, "orderDate");
          final int _cursorIndexOfOrderType = CursorUtil.getColumnIndexOrThrow(_cursor, "orderType");
          final int _cursorIndexOfEstimatedDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryDate");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAdvancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "advancePayment");
          final int _cursorIndexOfBalancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "balancePayment");
          final int _cursorIndexOfPaymentStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentStatus");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<Order> _result = new ArrayList<Order>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Order _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpOrderDate;
            _tmpOrderDate = _cursor.getString(_cursorIndexOfOrderDate);
            final String _tmpOrderType;
            _tmpOrderType = _cursor.getString(_cursorIndexOfOrderType);
            final String _tmpEstimatedDeliveryDate;
            _tmpEstimatedDeliveryDate = _cursor.getString(_cursorIndexOfEstimatedDeliveryDate);
            final String _tmpInstructions;
            _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final double _tmpAdvancePayment;
            _tmpAdvancePayment = _cursor.getDouble(_cursorIndexOfAdvancePayment);
            final double _tmpBalancePayment;
            _tmpBalancePayment = _cursor.getDouble(_cursorIndexOfBalancePayment);
            final String _tmpPaymentStatus;
            _tmpPaymentStatus = _cursor.getString(_cursorIndexOfPaymentStatus);
            final String _tmpPaymentDate;
            if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
              _tmpPaymentDate = null;
            } else {
              _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
            }
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
            _item = new Order(_tmpId,_tmpCustomerId,_tmpCustomerName,_tmpOrderDate,_tmpOrderType,_tmpEstimatedDeliveryDate,_tmpInstructions,_tmpAmount,_tmpStatus,_tmpAdvancePayment,_tmpBalancePayment,_tmpPaymentStatus,_tmpPaymentDate,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
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
  public Object getUnsyncedOrders(final Continuation<? super List<Order>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE syncStatus = 'PENDING' OR syncStatus = 'FAILED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Order>>() {
      @Override
      @NonNull
      public List<Order> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfOrderDate = CursorUtil.getColumnIndexOrThrow(_cursor, "orderDate");
          final int _cursorIndexOfOrderType = CursorUtil.getColumnIndexOrThrow(_cursor, "orderType");
          final int _cursorIndexOfEstimatedDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryDate");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAdvancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "advancePayment");
          final int _cursorIndexOfBalancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "balancePayment");
          final int _cursorIndexOfPaymentStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentStatus");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<Order> _result = new ArrayList<Order>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Order _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpOrderDate;
            _tmpOrderDate = _cursor.getString(_cursorIndexOfOrderDate);
            final String _tmpOrderType;
            _tmpOrderType = _cursor.getString(_cursorIndexOfOrderType);
            final String _tmpEstimatedDeliveryDate;
            _tmpEstimatedDeliveryDate = _cursor.getString(_cursorIndexOfEstimatedDeliveryDate);
            final String _tmpInstructions;
            _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final double _tmpAdvancePayment;
            _tmpAdvancePayment = _cursor.getDouble(_cursorIndexOfAdvancePayment);
            final double _tmpBalancePayment;
            _tmpBalancePayment = _cursor.getDouble(_cursorIndexOfBalancePayment);
            final String _tmpPaymentStatus;
            _tmpPaymentStatus = _cursor.getString(_cursorIndexOfPaymentStatus);
            final String _tmpPaymentDate;
            if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
              _tmpPaymentDate = null;
            } else {
              _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
            }
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
            _item = new Order(_tmpId,_tmpCustomerId,_tmpCustomerName,_tmpOrderDate,_tmpOrderType,_tmpEstimatedDeliveryDate,_tmpInstructions,_tmpAmount,_tmpStatus,_tmpAdvancePayment,_tmpBalancePayment,_tmpPaymentStatus,_tmpPaymentDate,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
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
  public Object getOrdersModifiedAfter(final long timestamp,
      final Continuation<? super List<Order>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE lastModified > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Order>>() {
      @Override
      @NonNull
      public List<Order> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfOrderDate = CursorUtil.getColumnIndexOrThrow(_cursor, "orderDate");
          final int _cursorIndexOfOrderType = CursorUtil.getColumnIndexOrThrow(_cursor, "orderType");
          final int _cursorIndexOfEstimatedDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryDate");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAdvancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "advancePayment");
          final int _cursorIndexOfBalancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "balancePayment");
          final int _cursorIndexOfPaymentStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentStatus");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final List<Order> _result = new ArrayList<Order>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Order _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpOrderDate;
            _tmpOrderDate = _cursor.getString(_cursorIndexOfOrderDate);
            final String _tmpOrderType;
            _tmpOrderType = _cursor.getString(_cursorIndexOfOrderType);
            final String _tmpEstimatedDeliveryDate;
            _tmpEstimatedDeliveryDate = _cursor.getString(_cursorIndexOfEstimatedDeliveryDate);
            final String _tmpInstructions;
            _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final double _tmpAdvancePayment;
            _tmpAdvancePayment = _cursor.getDouble(_cursorIndexOfAdvancePayment);
            final double _tmpBalancePayment;
            _tmpBalancePayment = _cursor.getDouble(_cursorIndexOfBalancePayment);
            final String _tmpPaymentStatus;
            _tmpPaymentStatus = _cursor.getString(_cursorIndexOfPaymentStatus);
            final String _tmpPaymentDate;
            if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
              _tmpPaymentDate = null;
            } else {
              _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
            }
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
            _item = new Order(_tmpId,_tmpCustomerId,_tmpCustomerName,_tmpOrderDate,_tmpOrderType,_tmpEstimatedDeliveryDate,_tmpInstructions,_tmpAmount,_tmpStatus,_tmpAdvancePayment,_tmpBalancePayment,_tmpPaymentStatus,_tmpPaymentDate,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
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
  public Object getOrderByServerId(final String serverId,
      final Continuation<? super Order> $completion) {
    final String _sql = "SELECT * FROM orders WHERE serverId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, serverId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Order>() {
      @Override
      @Nullable
      public Order call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfOrderDate = CursorUtil.getColumnIndexOrThrow(_cursor, "orderDate");
          final int _cursorIndexOfOrderType = CursorUtil.getColumnIndexOrThrow(_cursor, "orderType");
          final int _cursorIndexOfEstimatedDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedDeliveryDate");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAdvancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "advancePayment");
          final int _cursorIndexOfBalancePayment = CursorUtil.getColumnIndexOrThrow(_cursor, "balancePayment");
          final int _cursorIndexOfPaymentStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentStatus");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final Order _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpOrderDate;
            _tmpOrderDate = _cursor.getString(_cursorIndexOfOrderDate);
            final String _tmpOrderType;
            _tmpOrderType = _cursor.getString(_cursorIndexOfOrderType);
            final String _tmpEstimatedDeliveryDate;
            _tmpEstimatedDeliveryDate = _cursor.getString(_cursorIndexOfEstimatedDeliveryDate);
            final String _tmpInstructions;
            _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final double _tmpAdvancePayment;
            _tmpAdvancePayment = _cursor.getDouble(_cursorIndexOfAdvancePayment);
            final double _tmpBalancePayment;
            _tmpBalancePayment = _cursor.getDouble(_cursorIndexOfBalancePayment);
            final String _tmpPaymentStatus;
            _tmpPaymentStatus = _cursor.getString(_cursorIndexOfPaymentStatus);
            final String _tmpPaymentDate;
            if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
              _tmpPaymentDate = null;
            } else {
              _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
            }
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
            _result = new Order(_tmpId,_tmpCustomerId,_tmpCustomerName,_tmpOrderDate,_tmpOrderType,_tmpEstimatedDeliveryDate,_tmpInstructions,_tmpAmount,_tmpStatus,_tmpAdvancePayment,_tmpBalancePayment,_tmpPaymentStatus,_tmpPaymentDate,_tmpServerId,_tmpLastModified,_tmpSyncStatus);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
