/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lonelystudios.palantir.util

import com.lonelystudios.palantir.vo.sources.Article
import com.lonelystudios.palantir.vo.sources.ArticleSource
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source

class TestUtil {
    companion object {
        fun createArticles(status: String, articles: List<Article>) =  Articles(articles, status)

        fun createArticle(publishedAt: String,
                          author: String,
                          urlToImage: String,
                          description: String,
                          title: String,
                          url: String,
                          source: ArticleSource) = Article(-1, publishedAt, author,
                urlToImage, description, title, url, source)

        fun createArticleSource(id: String, name: String) = ArticleSource(id, name)

        fun createSource(id: String) = Source(id)
    }
}
