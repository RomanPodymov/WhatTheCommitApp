package com.romanpodymov.whatthecommitapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface CommitUiState {
    data object Loading : CommitUiState
    data class Success(val message: String) : CommitUiState
    data object Error : CommitUiState
}

class CommitViewModel : ViewModel() {
    private val mutableState = MutableStateFlow<CommitUiState>(CommitUiState.Loading)
    val state = mutableState.asStateFlow()
    private var loading = false

    init {
        refresh()
    }

    fun refresh() {
        if (loading) return
        loading = true
        mutableState.value = CommitUiState.Loading
        viewModelScope.launch {
            try {
                mutableState.value = CommitUiState.Success(fetchCommitMessage())
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: IOException) {
                mutableState.value = CommitUiState.Error
            } finally {
                loading = false
            }
        }
    }
}

private suspend fun fetchCommitMessage(): String = withContext(Dispatchers.IO) {
    val connection = URL("https://whatthecommit.com/index.txt").openConnection() as HttpURLConnection
    try {
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.useCaches = false
        connection.setRequestProperty("Accept", "text/plain")
        if (connection.responseCode !in 200..299) {
            throw IOException("HTTP ${connection.responseCode}")
        }
        connection.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
            reader.readText().trim().also {
                if (it.isBlank()) throw IOException("Empty commit message")
            }
        }
    } finally {
        connection.disconnect()
    }
}
