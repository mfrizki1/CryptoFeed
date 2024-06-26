package id.calocallo.cryptofeed

import app.cash.turbine.test
import id.calocallo.cryptofeed.api.BadRequest
import id.calocallo.cryptofeed.api.BadRequestException
import id.calocallo.cryptofeed.api.Connectivity
import id.calocallo.cryptofeed.api.ConnectivityException
import id.calocallo.cryptofeed.api.HttpClient
import id.calocallo.cryptofeed.api.InternalServerError
import id.calocallo.cryptofeed.api.InternalServerErrorException
import id.calocallo.cryptofeed.api.InvalidData
import id.calocallo.cryptofeed.api.InvalidDataException
import id.calocallo.cryptofeed.api.LoadCryptoFeedRemoteUseCase
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoadCryptoFeedRemoteUseCaseTest {
    private val client = spyk<HttpClient>()
    private lateinit var sut: LoadCryptoFeedRemoteUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        sut = LoadCryptoFeedRemoteUseCase(client)
    }

    @Test
    fun testInitDoesNotRequestData() {
        verify(exactly = 0) {
            client.get()
        }
        confirmVerified(client)
    }

    @Test
    fun testLoadRequestsData() =
        runBlocking {
            every {
                client.get()
            } returns flowOf()

            sut.load().test {
                awaitComplete()
            }

            verify(exactly = 1) {
                client.get()
            }
            confirmVerified(client)
        }

    @Test
    fun testLoadTwiceRequestsDataTwice() =
        runBlocking {
            every {
                client.get()
            } returns flowOf()

            sut.load().test {
                awaitComplete()
            }

            sut.load().test {
                awaitComplete()
            }

            verify(exactly = 2) {
                client.get()
            }

            confirmVerified(client)
        }

    @Test
    fun testLoadDeliversConnectivityErrorOnClientError() =
        runBlocking {
            expect(
                client = client,
                sut = sut,
                receivedHttpClientResult = ConnectivityException(),
                expectedResult = Connectivity(),
                exactly = 1,
                confirmVerified = client,
            )
        }

    @Test
    fun testLoadDeliversInvalidDataError() =
        runBlocking {
            expect(
                client = client,
                sut = sut,
                receivedHttpClientResult = InvalidDataException(),
                expectedResult = InvalidData(),
                exactly = 1,
                confirmVerified = client,
            )
        }

    @Test
    fun testLoadDeliversBadRequestError() =
        runBlocking {
            expect(
                client = client,
                sut = sut,
                receivedHttpClientResult = BadRequestException(),
                expectedResult = BadRequest(),
                exactly = 1,
                confirmVerified = client,
            )
        }

    @Test
    fun testLoadDeliversInternalServerError() =
        runBlocking {
            expect(
                client = client,
                sut = sut,
                receivedHttpClientResult = InternalServerErrorException(),
                expectedResult = InternalServerError(),
                exactly = 1,
                confirmVerified = client,
            )
        }

    private fun expect(
        client: HttpClient,
        sut: LoadCryptoFeedRemoteUseCase,
        receivedHttpClientResult: Exception,
        expectedResult: Any,
        exactly: Int = -1,
        confirmVerified: HttpClient,
    ) = runBlocking {
        every {
            client.get()
        } returns flowOf(receivedHttpClientResult)

        sut.load().test {
            assertEquals(expectedResult::class.java, awaitItem()::class.java)
            awaitComplete()
        }

        verify(exactly = exactly) {
            client.get()
        }

        confirmVerified(confirmVerified)
    }
}
