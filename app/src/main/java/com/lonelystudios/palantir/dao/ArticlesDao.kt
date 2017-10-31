package com.lonelystudios.palantir.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.lonelystudios.palantir.vo.sources.Article
import com.lonelystudios.palantir.vo.sources.Articles

/**
 * Created by vicmns on 10/25/17.
 */
@Dao
abstract class ArticlesDao {

    @Query("SELECT * FROM Articles")
    abstract fun getArticles(): LiveData<List<Articles>>

    @Query("SELECT * FROM Articles WHERE source = :source")
    abstract fun getArticlesBySource(source: String): LiveData<Articles>

    @Query("DELETE FROM Articles")
    abstract fun deleteAllArticles()

    @Query("DELETE FROM Article")
    abstract fun deleteAllArticlesItems()

    @Query("DELETE FROM Articles WHERE source = :source")
    abstract fun deleteArticlesBySource(source: String)

    @Query("DELETE FROM Article WHERE articlesId = :articlesId")
    abstract fun deleteAllArticlesItemsById(articlesId: Long)

    @Insert
    abstract fun insertArticlesItem(articles: Articles)

    @Insert
    abstract fun insertArticleListItems(listOfArticles: List<Article>)

    @Update
    abstract fun updateArticlesItem(articles: Articles)

    @Update
    abstract fun updateArticleListItems(listOfArticles: List<Article>)

}