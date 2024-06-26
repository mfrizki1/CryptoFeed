package id.calocallo.cryptofeed.domain

import kotlinx.coroutines.flow.Flow

sealed class LoadCryptoFeedResult {
    data class Success(val cryptoFeed: List<CryptoFeed>) : LoadCryptoFeedResult()

    data class Error(val exception: Exception) : LoadCryptoFeedResult()
}

interface LoadCryptoFeedUseCase {
    fun load(): Flow<LoadCryptoFeedResult>
}
