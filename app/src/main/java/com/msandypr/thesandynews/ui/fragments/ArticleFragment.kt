package com.msandypr.thesandynews.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.msandypr.thesandynews.R
import com.msandypr.thesandynews.databinding.FragmentArticleBinding
import com.msandypr.thesandynews.ui.NewsActivity
import com.msandypr.thesandynews.ui.NewsViewModel

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var newsViewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()
    lateinit var binding: FragmentArticleBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        newsViewModel = (activity as NewsActivity).newsViewModel
        val article = args.article

        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let {
                loadUrl(it)
            }
        }

        binding.fab?.setOnClickListener {
            val article = args.article
            if (article != null) {
                newsViewModel.addToBookmarks(article)
                Snackbar.make(view, "Added to Bookmark", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, "Article can't added to Bookmark", Snackbar.LENGTH_LONG).show()
            }
        }

    }

}