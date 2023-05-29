package com.app.core.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.core.data.ARTICLE_COLLECTION
import com.app.core.data.Result
import com.app.core.data.models.Article
import com.google.firebase.firestore.FirebaseFirestore

class ArticleRepository {

    private val articleRef = FirebaseFirestore.getInstance().collection(ARTICLE_COLLECTION)

    fun getArticles(limit: Long): LiveData<Result<List<Article>>> {
        val state = MutableLiveData<Result<List<Article>>>()
        state.value = Result.Loading
        articleRef
            .limit(limit)
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Article>()
                for(doc in docs) {
                    try {
                        val article = doc.toObject(Article::class.java)
                        list.add(article)
                    } catch (e: Exception) {
                        Log.e("ArticleRepository", "getArticles: $e")
                    }
                }
                state.value = Result.Success(list)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
        return state
    }

    fun getArticles(): LiveData<Result<List<Article>>> {
        val state = MutableLiveData<Result<List<Article>>>()
        state.value = Result.Loading
        articleRef
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Article>()
                for(doc in docs) {
                    val article = doc.toObject(Article::class.java)
                    list.add(article)
                }
                state.value = Result.Success(list)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
        return state
    }

}