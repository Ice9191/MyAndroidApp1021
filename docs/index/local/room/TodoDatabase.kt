package com.hxb.myandroidapp_1021.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// @Database → 数据库入口，管理所有表
@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
// 告诉Room要创建的数据库
abstract class TodoDatabase : RoomDatabase() {
    // 提供Dao的访问
    // 告诉Room去自动实现TodoDao接口的全部方法
    abstract fun todoDao(): TodoDao

    // 单例（伴生对象）：全局共享一个数据库实例
    companion object {
        // @Volatile：保证 INSTANCE 变量在多线程环境下的内存可见性。即：一个线程修改了 INSTANCE，其他线程立即可见，不会读到旧值。
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        /**
         * 这个方法的意思：
         *  当调用这个方法时，如果说有数据库实例INSTANCE存在，就返回INSTANCE
         *  如果没有数据库实例INSTANCE存在，就要创建一个数据库实例，创建的时候需要加一个同步锁，避免多线程去操作表
         */
        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo.db" // 数据库文件名
                ).build().also { INSTANCE = it }
            }
        }
    }
}