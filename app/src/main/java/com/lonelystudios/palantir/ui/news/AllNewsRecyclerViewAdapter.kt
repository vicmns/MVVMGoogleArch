package com.lonelystudios.palantir.ui.news

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lonelystudios.palantir.databinding.FragmentAllNewsItemBinding
import com.lonelystudios.palantir.vo.sources.Article

/**
 * [RecyclerView.Adapter] that can display a [Article] and makes a call to the
 * specified [OnNewsItemInteraction].
 * TODO: Replace the implementation with code for your data type.
 */
class AllNewsRecyclerViewAdapter(private val listener: OnNewsItemInteraction?) : RecyclerView.Adapter<AllNewsRecyclerViewAdapter.ViewHolder>() {
    private var articles: MutableList<Article> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val itemRowBinding = FragmentAllNewsItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(itemRowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindArticleItem(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    fun updateArticles(articles: MutableList<Article>) {
        this.articles = articles
    }

    inner class ViewHolder(val binding: FragmentAllNewsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.mainView.setOnClickListener({
                listener?.onListFragmentInteraction(articles[adapterPosition])
            })
        }

        fun bindArticleItem(article: Article) {
            binding.article = article
        }
    }
}
