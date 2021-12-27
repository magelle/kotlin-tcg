package magelle.tcg.http

import arrow.optics.Getter
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.runBlocking
import magelle.tcg.core.*
import magelle.tcg.repo.findById
import magelle.tcg.repo.save
import java.util.*

fun Application.registerGameRoutes() {
    routing {
        gameRouting()
    }
}

fun Route.gameRouting() {
    route("/game") {
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val game = getGame(id) ?: return@get call.respondText(
                "No game with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(game)
        }
        post {
            val gameId = newGame()
            call.respondText("Game created : $gameId", status = HttpStatusCode.Created)
        }
    }
}

val getGame = { id: String ->
    runFindById(UUID.fromString(id))
        ?.let(mapGameToDTO::get)
}

val newGame = {
    createGame(aDeck(), aDeck())
        .let(drawHands)
        .let(drawHandHandicapCard)
        .let(runSave)
}

val aDeck = { Deck(listOf(0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 6, 6, 7, 8).map(::Card).shuffled()) }

val mapGameToDTO = Getter { game: Game ->
    GameDTO(
        gameOver = isGameOver.get(game),
        activePlayer = activePlayerNumber.get(game),
        player1 = mapPlayerToDTO.get(game.player1),
        player2 = mapPlayerToDTO.get(game.player2)
    )
}

val mapPlayerToDTO = Getter { player: Player ->
    PlayerDTO(
        health = playerHealth.get(player),
        manaSlots = playerManaSlots.get(player),
        mana = playerManaCount.get(player),
        hand = playerHandCards.get(player).map(Card::manaCost)
    )
}

val runSave = { game: Game -> runBlocking { save(game) } }
val runFindById = { id: UUID -> runBlocking { findById(id) } }