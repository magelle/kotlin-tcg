package magelle.tcg

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class TcgTest {

    @Test
    fun `Each player starts the game with 30 Health and 0 Mana slots`() {
        val game = startGame()

        assertThat(game.player1.health).isEqualTo(30)
        assertThat(game.player2.health).isEqualTo(30)
    }

    @Test
    fun `Each player starts with a deck of 20 Damage cards`() {

    }

    @Test
    fun `From the deck each player receives 3 random cards has his initial hand`() {

    }

}