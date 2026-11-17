package com.hxb.myandroidapp_1021.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase

/**
 * 仅存储查询响应快照；账号与服务器共同组成作用域，缓存不代表提交成功。
 */
@Entity(tableName = "query_cache", primaryKeys = ["scope", "resource"])
data class QueryCacheEntry(
    val scope: String,
    val resource: String,
    val payload: String,
    val savedAt: Long
)

@Dao
interface QueryCacheDao {
    @Query("SELECT * FROM query_cache WHERE scope = :scope AND resource = :resource")
    suspend fun get(scope: String, resource: String): QueryCacheEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entry: QueryCacheEntry)

    @Query("DELETE FROM query_cache WHERE scope = :scope")
    suspend fun clear(scope: String)
}

@Database(entities = [QueryCacheEntry::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun queryCacheDao(): QueryCacheDao
}

