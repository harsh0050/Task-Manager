package com.example.taskmanager.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskmanager.models.Client

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients_data ORDER BY client_name ASC")
    fun getAllClientsLiveData() : LiveData<List<Client>>

    @Query("SELECT * FROM clients_data ORDER BY client_name ASC")
    fun getAllClients() : List<Client>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addClient(client: Client)

    @Update
    suspend fun updateClient(client: Client)

}