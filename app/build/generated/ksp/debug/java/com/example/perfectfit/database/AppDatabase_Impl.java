package com.example.perfectfit.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CustomerDao _customerDao;

  private volatile MeasurementDao _measurementDao;

  private volatile OrderDao _orderDao;

  private volatile WorkloadConfigDao _workloadConfigDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(9) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `customers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `firstName` TEXT NOT NULL, `lastName` TEXT NOT NULL, `address` TEXT NOT NULL, `mobile` TEXT NOT NULL, `alternateMobile` TEXT NOT NULL, `birthDate` TEXT NOT NULL, `serverId` TEXT, `lastModified` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_customers_firstName_lastName_mobile` ON `customers` (`firstName`, `lastName`, `mobile`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `measurements` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerId` INTEGER NOT NULL, `kurtiLength` TEXT NOT NULL, `fullShoulder` TEXT NOT NULL, `upperChestRound` TEXT NOT NULL, `chestRound` TEXT NOT NULL, `waistRound` TEXT NOT NULL, `shoulderToApex` TEXT NOT NULL, `apexToApex` TEXT NOT NULL, `shoulderToLowChestLength` TEXT NOT NULL, `skapLength` TEXT NOT NULL, `skapLengthRound` TEXT NOT NULL, `hipRound` TEXT NOT NULL, `frontNeckDeep` TEXT NOT NULL, `frontNeckWidth` TEXT NOT NULL, `backNeckDeep` TEXT NOT NULL, `readyShoulder` TEXT NOT NULL, `sleevesHeightShort` TEXT NOT NULL, `sleevesHeightElbow` TEXT NOT NULL, `sleevesHeightThreeQuarter` TEXT NOT NULL, `sleevesRound` TEXT NOT NULL, `pantWaist` TEXT NOT NULL, `pantLength` TEXT NOT NULL, `pantHip` TEXT NOT NULL, `pantBottom` TEXT NOT NULL, `blouseLength` TEXT NOT NULL, `blouseFullShoulder` TEXT NOT NULL, `blouseChest` TEXT NOT NULL, `blouseWaist` TEXT NOT NULL, `blouseShoulderToApex` TEXT NOT NULL, `blouseApexToApex` TEXT NOT NULL, `blouseBackLength` TEXT NOT NULL, `blouseFrontNeckDeep` TEXT NOT NULL, `blouseFrontNeckWidth` TEXT NOT NULL, `blouseBackNeckDeep` TEXT NOT NULL, `blouseReadyShoulder` TEXT NOT NULL, `blouseSleevesHeightShort` TEXT NOT NULL, `blouseSleevesHeightElbow` TEXT NOT NULL, `blouseSleevesHeightThreeQuarter` TEXT NOT NULL, `blouseSleevesRound` TEXT NOT NULL, `blouseHookOn` TEXT NOT NULL, `lastUpdated` INTEGER NOT NULL, `serverId` TEXT, `lastModified` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, FOREIGN KEY(`customerId`) REFERENCES `customers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_measurements_customerId` ON `measurements` (`customerId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `orders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerId` INTEGER NOT NULL, `customerName` TEXT NOT NULL, `orderDate` TEXT NOT NULL, `orderType` TEXT NOT NULL, `estimatedDeliveryDate` TEXT NOT NULL, `instructions` TEXT NOT NULL, `amount` REAL NOT NULL, `status` TEXT NOT NULL, `advancePayment` REAL NOT NULL, `balancePayment` REAL NOT NULL, `paymentStatus` TEXT NOT NULL, `paymentDate` TEXT, `serverId` TEXT, `lastModified` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, FOREIGN KEY(`customerId`) REFERENCES `customers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_orders_customerId` ON `orders` (`customerId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `workload_config` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timePerOrderHours` REAL NOT NULL, `mondayHours` REAL NOT NULL, `tuesdayHours` REAL NOT NULL, `wednesdayHours` REAL NOT NULL, `thursdayHours` REAL NOT NULL, `fridayHours` REAL NOT NULL, `saturdayHours` REAL NOT NULL, `sundayHours` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e2d80ee52b917c8e94e4feea9b399df3')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `customers`");
        db.execSQL("DROP TABLE IF EXISTS `measurements`");
        db.execSQL("DROP TABLE IF EXISTS `orders`");
        db.execSQL("DROP TABLE IF EXISTS `workload_config`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCustomers = new HashMap<String, TableInfo.Column>(10);
        _columnsCustomers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("firstName", new TableInfo.Column("firstName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("lastName", new TableInfo.Column("lastName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("mobile", new TableInfo.Column("mobile", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("alternateMobile", new TableInfo.Column("alternateMobile", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("birthDate", new TableInfo.Column("birthDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("serverId", new TableInfo.Column("serverId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("lastModified", new TableInfo.Column("lastModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomers.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCustomers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCustomers = new HashSet<TableInfo.Index>(1);
        _indicesCustomers.add(new TableInfo.Index("index_customers_firstName_lastName_mobile", true, Arrays.asList("firstName", "lastName", "mobile"), Arrays.asList("ASC", "ASC", "ASC")));
        final TableInfo _infoCustomers = new TableInfo("customers", _columnsCustomers, _foreignKeysCustomers, _indicesCustomers);
        final TableInfo _existingCustomers = TableInfo.read(db, "customers");
        if (!_infoCustomers.equals(_existingCustomers)) {
          return new RoomOpenHelper.ValidationResult(false, "customers(com.example.perfectfit.models.Customer).\n"
                  + " Expected:\n" + _infoCustomers + "\n"
                  + " Found:\n" + _existingCustomers);
        }
        final HashMap<String, TableInfo.Column> _columnsMeasurements = new HashMap<String, TableInfo.Column>(45);
        _columnsMeasurements.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("customerId", new TableInfo.Column("customerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("kurtiLength", new TableInfo.Column("kurtiLength", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("fullShoulder", new TableInfo.Column("fullShoulder", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("upperChestRound", new TableInfo.Column("upperChestRound", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("chestRound", new TableInfo.Column("chestRound", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("waistRound", new TableInfo.Column("waistRound", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("shoulderToApex", new TableInfo.Column("shoulderToApex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("apexToApex", new TableInfo.Column("apexToApex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("shoulderToLowChestLength", new TableInfo.Column("shoulderToLowChestLength", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("skapLength", new TableInfo.Column("skapLength", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("skapLengthRound", new TableInfo.Column("skapLengthRound", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("hipRound", new TableInfo.Column("hipRound", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("frontNeckDeep", new TableInfo.Column("frontNeckDeep", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("frontNeckWidth", new TableInfo.Column("frontNeckWidth", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("backNeckDeep", new TableInfo.Column("backNeckDeep", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("readyShoulder", new TableInfo.Column("readyShoulder", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("sleevesHeightShort", new TableInfo.Column("sleevesHeightShort", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("sleevesHeightElbow", new TableInfo.Column("sleevesHeightElbow", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("sleevesHeightThreeQuarter", new TableInfo.Column("sleevesHeightThreeQuarter", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("sleevesRound", new TableInfo.Column("sleevesRound", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("pantWaist", new TableInfo.Column("pantWaist", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("pantLength", new TableInfo.Column("pantLength", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("pantHip", new TableInfo.Column("pantHip", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("pantBottom", new TableInfo.Column("pantBottom", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseLength", new TableInfo.Column("blouseLength", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseFullShoulder", new TableInfo.Column("blouseFullShoulder", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseChest", new TableInfo.Column("blouseChest", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseWaist", new TableInfo.Column("blouseWaist", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseShoulderToApex", new TableInfo.Column("blouseShoulderToApex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseApexToApex", new TableInfo.Column("blouseApexToApex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseBackLength", new TableInfo.Column("blouseBackLength", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseFrontNeckDeep", new TableInfo.Column("blouseFrontNeckDeep", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseFrontNeckWidth", new TableInfo.Column("blouseFrontNeckWidth", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseBackNeckDeep", new TableInfo.Column("blouseBackNeckDeep", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseReadyShoulder", new TableInfo.Column("blouseReadyShoulder", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseSleevesHeightShort", new TableInfo.Column("blouseSleevesHeightShort", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseSleevesHeightElbow", new TableInfo.Column("blouseSleevesHeightElbow", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseSleevesHeightThreeQuarter", new TableInfo.Column("blouseSleevesHeightThreeQuarter", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseSleevesRound", new TableInfo.Column("blouseSleevesRound", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("blouseHookOn", new TableInfo.Column("blouseHookOn", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("serverId", new TableInfo.Column("serverId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("lastModified", new TableInfo.Column("lastModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMeasurements.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMeasurements = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMeasurements.add(new TableInfo.ForeignKey("customers", "CASCADE", "NO ACTION", Arrays.asList("customerId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMeasurements = new HashSet<TableInfo.Index>(1);
        _indicesMeasurements.add(new TableInfo.Index("index_measurements_customerId", false, Arrays.asList("customerId"), Arrays.asList("ASC")));
        final TableInfo _infoMeasurements = new TableInfo("measurements", _columnsMeasurements, _foreignKeysMeasurements, _indicesMeasurements);
        final TableInfo _existingMeasurements = TableInfo.read(db, "measurements");
        if (!_infoMeasurements.equals(_existingMeasurements)) {
          return new RoomOpenHelper.ValidationResult(false, "measurements(com.example.perfectfit.models.Measurement).\n"
                  + " Expected:\n" + _infoMeasurements + "\n"
                  + " Found:\n" + _existingMeasurements);
        }
        final HashMap<String, TableInfo.Column> _columnsOrders = new HashMap<String, TableInfo.Column>(16);
        _columnsOrders.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("customerId", new TableInfo.Column("customerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("customerName", new TableInfo.Column("customerName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("orderDate", new TableInfo.Column("orderDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("orderType", new TableInfo.Column("orderType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("estimatedDeliveryDate", new TableInfo.Column("estimatedDeliveryDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("instructions", new TableInfo.Column("instructions", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("advancePayment", new TableInfo.Column("advancePayment", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("balancePayment", new TableInfo.Column("balancePayment", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("paymentStatus", new TableInfo.Column("paymentStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("paymentDate", new TableInfo.Column("paymentDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("serverId", new TableInfo.Column("serverId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("lastModified", new TableInfo.Column("lastModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOrders = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysOrders.add(new TableInfo.ForeignKey("customers", "CASCADE", "NO ACTION", Arrays.asList("customerId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesOrders = new HashSet<TableInfo.Index>(1);
        _indicesOrders.add(new TableInfo.Index("index_orders_customerId", false, Arrays.asList("customerId"), Arrays.asList("ASC")));
        final TableInfo _infoOrders = new TableInfo("orders", _columnsOrders, _foreignKeysOrders, _indicesOrders);
        final TableInfo _existingOrders = TableInfo.read(db, "orders");
        if (!_infoOrders.equals(_existingOrders)) {
          return new RoomOpenHelper.ValidationResult(false, "orders(com.example.perfectfit.models.Order).\n"
                  + " Expected:\n" + _infoOrders + "\n"
                  + " Found:\n" + _existingOrders);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkloadConfig = new HashMap<String, TableInfo.Column>(9);
        _columnsWorkloadConfig.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkloadConfig.put("timePerOrderHours", new TableInfo.Column("timePerOrderHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkloadConfig.put("mondayHours", new TableInfo.Column("mondayHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkloadConfig.put("tuesdayHours", new TableInfo.Column("tuesdayHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkloadConfig.put("wednesdayHours", new TableInfo.Column("wednesdayHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkloadConfig.put("thursdayHours", new TableInfo.Column("thursdayHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkloadConfig.put("fridayHours", new TableInfo.Column("fridayHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkloadConfig.put("saturdayHours", new TableInfo.Column("saturdayHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkloadConfig.put("sundayHours", new TableInfo.Column("sundayHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkloadConfig = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWorkloadConfig = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWorkloadConfig = new TableInfo("workload_config", _columnsWorkloadConfig, _foreignKeysWorkloadConfig, _indicesWorkloadConfig);
        final TableInfo _existingWorkloadConfig = TableInfo.read(db, "workload_config");
        if (!_infoWorkloadConfig.equals(_existingWorkloadConfig)) {
          return new RoomOpenHelper.ValidationResult(false, "workload_config(com.example.perfectfit.models.WorkloadConfig).\n"
                  + " Expected:\n" + _infoWorkloadConfig + "\n"
                  + " Found:\n" + _existingWorkloadConfig);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e2d80ee52b917c8e94e4feea9b399df3", "ce6488b50792f30f6c04963f69b72384");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "customers","measurements","orders","workload_config");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `customers`");
      _db.execSQL("DELETE FROM `measurements`");
      _db.execSQL("DELETE FROM `orders`");
      _db.execSQL("DELETE FROM `workload_config`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CustomerDao.class, CustomerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MeasurementDao.class, MeasurementDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(OrderDao.class, OrderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkloadConfigDao.class, WorkloadConfigDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CustomerDao customerDao() {
    if (_customerDao != null) {
      return _customerDao;
    } else {
      synchronized(this) {
        if(_customerDao == null) {
          _customerDao = new CustomerDao_Impl(this);
        }
        return _customerDao;
      }
    }
  }

  @Override
  public MeasurementDao measurementDao() {
    if (_measurementDao != null) {
      return _measurementDao;
    } else {
      synchronized(this) {
        if(_measurementDao == null) {
          _measurementDao = new MeasurementDao_Impl(this);
        }
        return _measurementDao;
      }
    }
  }

  @Override
  public OrderDao orderDao() {
    if (_orderDao != null) {
      return _orderDao;
    } else {
      synchronized(this) {
        if(_orderDao == null) {
          _orderDao = new OrderDao_Impl(this);
        }
        return _orderDao;
      }
    }
  }

  @Override
  public WorkloadConfigDao workloadConfigDao() {
    if (_workloadConfigDao != null) {
      return _workloadConfigDao;
    } else {
      synchronized(this) {
        if(_workloadConfigDao == null) {
          _workloadConfigDao = new WorkloadConfigDao_Impl(this);
        }
        return _workloadConfigDao;
      }
    }
  }
}
