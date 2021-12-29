package magelle.tcg.core

import arrow.core.compose

val createGame = { player1Deck: Deck,
                   player2Deck: Deck ->
    Game(
        player1 = createPlayer(player1Deck),
        player2 = createPlayer(player2Deck),
        activePlayer = 1
    )
}

val drawHands = player1DrawHand compose player2DrawHand
val drawHandHandicapCard = player2DrawCard
val endTurn = nextPlayer
val startTurn = { game: Game ->
    activePlayerGainManaSlot(game)
        .let(fillActivePlayerManaSlots)
        .let(activePlayerDrawACard)
}
val playCard = activePlayerPlayCard

private fun createPlayer(player1Deck: Deck) = Player(
    health = 30,
    manaSlots = ManaSlots(slots = 0, mana = 0),
    deck = player1Deck,
    hand = Hand(cards = listOf())
)