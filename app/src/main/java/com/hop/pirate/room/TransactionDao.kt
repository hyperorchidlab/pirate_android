package com.hop.pirate.room

import androidx.room.*
import com.hop.pirate.model.bean.TransactionBean
@Dao
interface TransactionDao {
    @Query("select * from transactions order by id DESC")
    fun getTransactions(): List<TransactionBean>

    @Query("select * from transactions  where transactionType = :t order by id DESC limit 1")
    fun getLastTransactionByType(t: Int): TransactionBean?

    @Query("delete from transactions")
    fun deleteTransaction()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTransaction(transactionBean: TransactionBean)

    @Query("update transactions set status = :transactioStatus where hash == :transactioHash")
    fun updateTransaction(transactioStatus: Int, transactioHash: String)
}