package magelle.tcg.http

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import magelle.tcg.module
import magelle.tcg.repo.buildSchema
import magelle.tcg.repo.dbConnect
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.fail

internal class GameRouteTest {

    @BeforeTest
    fun beforeTest() = runBlocking {
        dbConnect()
        buildSchema()
    }

    @Test
    fun `should create a game`() {
        withTestApplication({ module(testing = true) }) {
            createAGame()
        }
    }

    @Test
    fun `should get a game`() {
        withTestApplication({ module(testing = true) }) {
            val id = createAGame()
            val game = getGame(id)
            assertThat(game.player1).isNotNull()
            assertThat(game.player2).isNotNull()
            assertThat(game.activePlayer).isEqualTo(1)
        }
    }

    @Test
    fun `play a game`() {
        withTestApplication({ module(testing = true) }) {
            val id = createAGame()
            val game = getGame(id)
            assertThat(game.player1).isNotNull()
            assertThat(game.player2).isNotNull()
            assertThat(game.activePlayer).isEqualTo(1)
        }
    }

    private fun TestApplicationEngine.createAGame() =
        handleRequest(HttpMethod.Post, "/game") {
            addHeader("Content-Type", "application/json")
        }.apply {
            assertEquals(HttpStatusCode.Created, response.status())
        }.response.content
            ?.let(UUID::fromString)
            ?: fail("Should return an id")

    private fun TestApplicationEngine.playCard(id: UUID, card: Int) =
        handleRequest(HttpMethod.Post, "/game/$id") {
            setBody("""{ "card":  }""")
            addHeader("Content-Type", "application/json")
        }.apply {
            assertEquals(HttpStatusCode.Created, response.status())
        }.response.content
            ?.let(UUID::fromString)
            ?: fail("Should return an id")

    private fun TestApplicationEngine.getGame(id: UUID) =
        handleRequest(HttpMethod.Get, "/game/$id") {
            addHeader("Content-Type", "application/json")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
        }.response.content
            ?.let { Json.decodeFromString<GameDTO>(it) }
            ?: fail("Should return a game")

}