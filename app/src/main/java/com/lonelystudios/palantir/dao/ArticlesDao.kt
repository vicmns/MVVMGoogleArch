package com.lonelystudios.palantir.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.lonelystudios.palantir.vo.sources.Article

/**
 * Created by vicmns on 10/25/17.
 */
@Dao
abstract class ArticlesDao {

    @Query("SELECT * FROM Article")
    abstract fun getArticles(): LiveData<List<Article>>

    @Query("SELECT * FROM Article WHERE source_id = :sourceId")
    abstract fun getArticlesBySource(sourceId: String): LiveData<List<Article>>

    @Query("DELETE FROM Article")
    abstract fun deleteAllArticles()

    @Query("DELETE FROM Article")
    abstract fun deleteAllArticlesItems()

    @Query("DELETE FROM Article WHERE source_id = :sourceId")
    abstract fun deleteArticlesBySource(sourceId: String)

    @Query("DELETE FROM Article WHERE id = :id")
    abstract fun deleteAllArticlesItemsById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertArticlesItem(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertArticleListItems(listOfArticles: List<Article>)

    @Update
    abstract fun updateArticlesItem(article: Article)

    @Update
    abstract fun updateArticleListItems(listOfArticles: List<Article>)

}