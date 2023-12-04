package com.example.roomlogin

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE email LIKE :first AND " +
            "password LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT COUNT(*) FROM user WHERE email = :email")
    fun checkExistEmail(email: String): Int

    @Query("SELECT COUNT(*) FROM user WHERE email = :email AND password = :password")
    fun checkExistAccount(email: String, password: String): Int
}