package magelle.tcg

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class TcgTest {

    @Test
    fun `Each player starts the game with 30 Health and 0 Mana slots`() {
        val game = createGame(aDeck(), aDeck())

        assertThat(game.player1.health).isEqualTo(30)
        assertThat(game.player2.health).isEqualTo(30)
    }

    @Test
    fun `Each player starts with a deck of 20 Damage cards`() {
        val game = createGame(aDeck(), aDeck())

        assertThat(player1DeckSize.get(game)).isEqualTo(20)
        assertThat(player2DeckSize.get(game)).isEqualTo(20)
    }

    @Test
    fun `From the deck each player receives 3 random cards has his initial hand`() {
        val game = createGame(aDeck(), aDeck()).let(::drawHands)

        assertThat(player1HandSize.get(game)).isEqualTo(3)
        assertThat(player2HandSize.get(game)).isEqualTo(3)
    }

    private fun aDeck() =
        Deck(listOf(0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 6, 6, 7, 8).map { Card(it) })

}