package magelle.tcg.core

import arrow.core.Some
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import magelle.tcg.core.*
import kotlin.test.Test

class TcgTest {

    @Test
    fun `Each player starts the game with 30 Health and 0 Mana slots`() {
        val game = createGame(aDeck(), aDeck())

        assertThat(player1Health.get(game)).isEqualTo(30)
        assertThat(player2Health.get(game)).isEqualTo(30)
    }

    @Test
    fun `Each player starts with a deck of 20 Damage cards`() {
        val game = createGame(aDeck(), aDeck())

        assertThat(player1DeckSize.get(game)).isEqualTo(20)
        assertThat(player2DeckSize.get(game)).isEqualTo(20)
    }

    @Test
    fun `From the deck each player receives 3 random cards has his initial hand`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)

        assertThat(player1HandSize.get(game)).isEqualTo(3)
        assertThat(player2HandSize.get(game)).isEqualTo(3)
    }

    @Test
    fun `The second player draw a card to compensate handicap to be second`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)

        assertThat(player1HandSize.get(game)).isEqualTo(3)
        assertThat(player2HandSize.get(game)).isEqualTo(4)
    }

    @Test
    fun `The active player gain a mana slot a start of turn`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(startTurn)

        assertThat(player1ManaSlots.get(game)).isEqualTo(1)
        assertThat(player2ManaSlots.get(game)).isEqualTo(0)
    }

    @Test
    fun `The active player's empty Mana slots are refilled`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(startTurn)

        assertThat(player1Mana.get(game)).isEqualTo(1)
    }

    @Test
    fun `The active player draws a random card from his deck`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(startTurn)

        assertThat(player1HandSize.get(game)).isEqualTo(4)
    }

    @Test
    fun `Any played card empties Mana slots`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(startTurn)
            .let(playCard(Card(1)))

        assertThat(player1Mana.get(game)).isEqualTo(0)
    }

    @Test
    fun `deals immediate damage to the opponent player equal to its Mana cost`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(startTurn)
            .let(playCard(Card(1)))

        assertThat(player2Health.get(game)).isEqualTo(30 - 1)
    }

    @Test
    fun `end turn`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(endTurn)

        assertThat(activePlayer.get(game)).isEqualTo(player2.get(game))
    }

    @Test
    fun `next player start turn`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(endTurn)
            .let(startTurn)

        // Add mana slot
        assertThat(player2ManaSlots.get(game)).isEqualTo(1)
        // Fill mana slots
        assertThat(player2Mana.get(game)).isEqualTo(1)
        // Draw a card initial hand (3) + handicap card (1) + new card (1)
        assertThat(player2HandSize.get(game)).isEqualTo(3 + 1 + 1)
    }

    @Test
    fun `next player play card`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(endTurn)
            .let(startTurn)
            .let(playCard(Card(1)))

        // Reduce health
        assertThat(player1Health.get(game)).isEqualTo(29)
        // Reduce mana
        assertThat(player2Mana.get(game)).isEqualTo(1 - 1)
        // Discard card
        assertThat(player2HandSize.get(game)).isEqualTo(5 - 1)
    }

    @Test
    fun `next player end turn`() {
        val game = createGame(aDeck(), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(endTurn)
            .let(startTurn)
            .let(endTurn)

        assertThat(activePlayer.get(game)).isEqualTo(player1.get(game))
    }

    @Test
    fun `If the opponent player's Health drops to or below zero the active player wins the game`() {
        val game = createGame(aDeck(listOf(1, 2, 3, 4, 5, 6, 7, 2, 0, 0, 0)), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(startTurn).let(playCard(Card(1))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(2))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(3))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(4))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(5))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(6))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(7))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(2))).let(endTurn).let(endTurn)

        assertThat(isGameOver.get(game)).isTrue()
        assertThat(winner.get(game)).isEqualTo(Some(1))
    }

    @Test
    fun `second player can win the game too`() {
        val game = createGame(aDeck(), aDeck(listOf(1, 2, 3, 4, 5, 6, 7, 2, 0, 0, 0, 0)))
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(endTurn)
            .let(startTurn).let(playCard(Card(1))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(2))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(3))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(4))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(5))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(6))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(7))).let(endTurn).let(endTurn)
            .let(startTurn).let(playCard(Card(2))).let(endTurn).let(endTurn)

        assertThat(isGameOver.get(game)).isTrue()
        assertThat(winner.get(game)).isEqualTo(Some(2))
    }

    @Test
    fun `Bleeding Out - If a player's card deck is empty before the game is over he receives 1 damage instead of drawing a card when it's his turn`() {
        val game = createGame(aDeck(listOf(0, 0, 0)), aDeck())
            .let(drawHands)
            .let(drawHandHandicapCard)
            .let(startTurn)

        assertThat(player1Health.get(game)).isEqualTo(29)
    }

    private fun aDeck(cards: List<Int> = listOf(0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 6, 6, 7, 8)) =
        Deck(cards.map { Card(it) })

}