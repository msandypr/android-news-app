package com.msandypr.thesandynews.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.msandypr.thesandynews.models.Article
import com.msandypr.thesandynews.models.NewsResponse
import com.msandypr.thesandynews.repository.NewsRepository
import com.msandypr.thesandynews.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.Locale.IsoCountryCode

class NewsViewModel(app: Application, val newsRepository: NewsRepository): AndroidViewModel(app) {

    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    init {
        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }
    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                headlinesPage++
                if (headlinesResponse == null){
                    headlinesResponse = resultResponse
                } else {
                    val oldArticles = headlinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headlinesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                if (searchNewsResponse == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun addToBookmarks(article: Article) = viewModelScope.launch {
        val author = article.author ?: "Unknown Author"
        val defaultImageUrl = "https://img.freepik.com/premium-vector/default-image-icon-vector-missing-picture-page-website-design-mobile-app-no-photo-available_87543-11093.jpg"

        if (article.title != null && article.description != null && article.url != null &&
            article.publishedAt != null && article.content != null) {

            // Check if urlToImage is null, use defaultImageUrl instead
            val imageUrl = article.urlToImage ?: defaultImageUrl

            val articleWithAuthor = article.copy(author = author, urlToImage = imageUrl)

            // Log the article details before upsert
            Log.d("NewsViewModel", "Adding to bookmarks: ${articleWithAuthor.title}")
            Log.d("NewsViewModel", "Details: " +
                    "Title: ${articleWithAuthor.title}, " +
                    "Description: ${articleWithAuthor.description}, " +
                    "URL: ${articleWithAuthor.url}, " +
                    "Author: ${articleWithAuthor.author}, " +
                    "PublishedAt: ${articleWithAuthor.publishedAt}, " +
                    "Content: ${articleWithAuthor.content}, " +
                    "URLToImage: ${articleWithAuthor.urlToImage}"
            )

            newsRepository.upsert(articleWithAuthor)

            // Log success after upsert
            Log.d("NewsViewModel", "Article added to bookmarks: ${articleWithAuthor.title}")
        } else {
            Log.w("NewsViewModel", "Skipping null article: ${article.title}")
            Log.w("NewsViewModel", "Details: " +
                    "Title: ${article.title}, " +
                    "Description: ${article.description}, " +
                    "URL: ${article.url}, " +
                    "Author: ${article.author}, " +
                    "PublishedAt: ${article.publishedAt}, " +
                    "Content: ${article.content}, " +
                    "URLToImage: ${article.urlToImage}"
            )

            // Show a toast indicating that the article cannot be added to bookmarks
            showToast("Error: Bookmark cannot be saved for this article")
        }
    }


    private fun showToast(message: String) {

    }

    fun getBookmarkNews() = newsRepository.getBookmarkNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun internetConnection(context: Context) : Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    private suspend fun headlinesInternet(countryCode: String) {
        headlines.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.getHeadlines(countryCode, headlinesPage)
                headlines.postValue(handleHeadlinesResponse(response))
            } else {
                headlines.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> headlines.postValue(Resource.Error("Unable to Connect the Internet"))
                else -> headlines.postValue(Resource.Error("No Signal"))
            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery: String) {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Unable to Connect the Internet"))
                else -> searchNews.postValue(Resource.Error("No Signal"))
            }
        }
    }
}