package com.hxb.myandroidapp_1021.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Entity用于根据下面的数据类映射出一张数据库表，后面的tableName属性用于指定表名
 */
@Entity(tableName = "todos")
data class TodoEntity(
    // 主键自增
    @PrimaryKey(autoGenerate = true)    // 主键自增1,2,3
    val id: Int = 0,
    val text: String,
    val isDone: Boolean = false
)