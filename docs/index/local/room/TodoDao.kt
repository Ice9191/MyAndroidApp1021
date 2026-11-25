package com.hxb.myandroidapp_1021.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * @Dao：是Data Access Object的缩写，定义对表的CRUD操作
 * 所有方法都是suspend挂起函数，表示这些方法都是异步的，不会阻塞主线程
 */
@Dao
interface TodoDao {
    // 查询所有待办，按照id升序
    @Query(value = "SELECT * FROM todos ORDER BY id ASC")
    suspend fun getAll(): List<TodoEntity>

    // 插入一条待办
    @Insert
    suspend fun insert(todo: TodoEntity)

    // 更新一条待办
    @Update
    suspend fun update(todo: TodoEntity)

    // 删除一条待办
    @Delete
    suspend fun delete(todo: TodoEntity)
}