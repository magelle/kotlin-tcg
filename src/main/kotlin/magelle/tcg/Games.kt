package magelle.tcg

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun save(game: Game): UUID = transaction {
    val p1Id = insertPlayer(game.player1)
    val p2Id = insertPlayer(game.player2)
    insertGame(game, p1Id, p2Id).value
}

fun update(id: UUID, game: Game): Unit = transaction {
    Games.select(Games.id.eq(id))
        .firstOrNull()?.let {
            val p1Id = it[Games.player1].value
            val p2Id = it[Games.player2].value
            updatePlayer(p1Id, game.player1)
            updatePlayer(p2Id, game.player2)
            updateGame(id, game, p1Id, p2Id)
        }
}

fun findById(id: UUID): Game? = transaction {
    Games.select(Games.id.eq(id))
        .map { buildGame(it) }
        .firstOrNull()
}

private fun buildGame(resultRow: ResultRow): Game {
    val player1 = selectPlayer(resultRow[Games.player1].value)!!
    val player2 = selectPlayer(resultRow[Games.player2].value)!!
    return Game(
        activePlayer = resultRow[Games.activePlayer],
        player1 = player1,
        player2 = player2
    )
}

private fun selectPlayer(playerId: UUID): Player? {
    return Players.select(Players.id.eq(playerId))
        .map { buildPlayer(it) }
        .firstOrNull()
}

private fun buildPlayer(resultRow: ResultRow) = Player(
    health = resultRow[Players.health],
    manaSlots = ManaSlots(
        slots = resultRow[Players.manaSlots],
        mana = resultRow[Players.mana]
    ),
    deck = selectDeck(resultRow[Players.id].value),
    hand = selectHand(resultRow[Players.id].value)
)

private fun selectDeck(playerId: UUID) =
    Deck(
        cards = Decks
            .select { Decks.player.eq(playerId) }
            .map { Card(it[Decks.card]) }
            .toList()
    )

private fun selectHand(playerId: UUID) =
    Hand(
        cards = Hands
            .select { Hands.player.eq(playerId) }
            .map { Card(it[Hands.card]) }
            .toList()
    )

private fun insertPlayer(player: Player): UUID {
    val pId = player.let(insertPlayer).value
    insertHand(player.hand, pId)
    insertDeck(player.deck, pId)
    return pId
}

private val insertPlayer = { player: Player ->
    Players.insertAndGetId {
        it[health] = player.health
        it[manaSlots] = player.manaSlots.slots
        it[mana] = player.manaSlots.mana
    }
}


private fun updatePlayer(id: UUID, player: Player) {
    Players.update({ Players.id.eq(id) }) {
        it[health] = player.health
        it[manaSlots] = player.manaSlots.slots
        it[mana] = player.manaSlots.mana
    }
    replaceHand(id, player.hand)
    replaceDeck(id, player.deck)
}

private fun replaceDeck(pId: UUID, deck: Deck) {
    deleteDeck(pId)
    insertDeck(deck, pId)
}

private fun replaceHand(pId: UUID, hand: Hand) {
    deleteHand(pId)
    insertHand(hand, pId)
}

fun deleteDeck(pId: UUID) {
    Decks.deleteWhere { Decks.player.eq(pId) }
}

fun deleteHand(pId: UUID) {
    Hands.deleteWhere { Hands.player.eq(pId) }
}

fun updateGame(gId: UUID, game: Game, p1Id: UUID, p2Id: UUID) {
    Games.update ({ Games.id.eq(gId) }) {
        it[activePlayer] = game.activePlayer
        it[player1] = p1Id
        it[player2] = p2Id
    }
}

private val insertGame = { game: Game, p1Id: UUID, p2Id: UUID ->
    Games.insertAndGetId {
        it[activePlayer] = game.activePlayer
        it[player1] = p1Id
        it[player2] = p2Id
    }
}

private val insertHand = { hand: Hand, pId: UUID ->
    Hands.batchInsert(hand.cards.map(Card::manaCost)) {
        this[Hands.player] = pId
        this[Hands.card] = it
    }
}

private val insertDeck = { deck: Deck, pId: UUID ->
    Decks.batchInsert(deck.cards.map(Card::manaCost)) {
        this[Decks.player] = pId
        this[Decks.card] = it
    }
}