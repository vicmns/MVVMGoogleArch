package com.lonelystudios.palantir.db

import android.support.test.runner.AndroidJUnit4
import com.lonelystudios.palantir.util.LiveDataTestUtil.getValue
import com.lonelystudios.palantir.util.TestUtil
import com.lonelystudios.palantir.vo.sources.Article
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by vicmns on 12/11/17.
 */
@RunWith(AndroidJUnit4::class)
class ArticlesDaoTest: DbTest() {

    @Test
    fun insertAndRead() {
        val article = TestUtil.createArticle("2017-12-04T22:23:00Z", "John Smith",
                "http://foo.bar/image.jpg", "Lorem Ipsum",
                "Foo bar", "http://foo.bar/",
                TestUtil.createArticleSource("foo", "bar"))
        db.articlesDao().insert(article)
        val loaded: List<Article> = getValue(db.articlesDao().getArticlesBySource("foo"))
        assertThat(loaded, notNullValue())
        assertThat(loaded, hasSize(1))
        assertThat(loaded[0].publishedAt, `is`("2017-12-04T22:23:00Z"))
        assertThat(loaded[0].author, `is`("John Smith"))
        assertThat(loaded[0].urlToImage, `is`("http://foo.bar/image.jpg"))
        assertThat(loaded[0].url, `is`("http://foo.bar/"))
        assertThat(loaded[0].source.sourceId, `is`("foo"))
    }

    @Test
    fun deleteAll() {
        val article = TestUtil.createArticle("2017-12-04T22:23:00Z", "John Smith",
                "http://foo.bar/image.jpg", "Lorem Ipsum",
                "Foo bar", "http://foo.bar/",
                TestUtil.createArticleSource("foo", "bar"))
        db.articlesDao().insert(article)
        val loaded: List<Article> = getValue(db.articlesDao().getArticlesBySource("foo"))
        assertThat(loaded, notNullValue())
        assertThat(loaded, hasSize(1))
        db.articlesDao().deleteAllArticles()
        val loadedAfterDelete: List<Article> = getValue(db.articlesDao().getArticlesBySource("foo"))
        assertThat(loadedAfterDelete, notNullValue())
        assertThat(loadedAfterDelete, hasSize(0))
    }
}