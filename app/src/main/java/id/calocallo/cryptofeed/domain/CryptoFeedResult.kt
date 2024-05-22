package id.calocallo.cryptofeed.domain

import kotlinx.coroutines.flow.Flow

sealed class CryptoFeedResult {
    data class Success(val cryptoFeed: List<CryptoFeed>) : CryptoFeedResult()

    data class Error(val exception: Exception) : CryptoFeedResult()
}

interface CryptoFeedUseCase {
    fun load(): Flow<CryptoFeedResult>
}
