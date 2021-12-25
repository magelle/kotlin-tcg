package magelle.tcg

import assertk.assertThat
import assertk.assertions.isEqualTo
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
    fun `The active player’s empty Mana slots are refilled`() {
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

    private fun aDeck() =
        Deck(listOf(0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 6, 6, 7, 8).map { Card(it) })

}