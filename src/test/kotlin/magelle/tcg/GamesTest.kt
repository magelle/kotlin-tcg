package magelle.tcg

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Disabled
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test

class GamesTest {

    @BeforeTest
    fun beforeTest() {
        dbConnect()
        buildSchema()
    }

    @Test
    fun `can save a new game`() {
        val game = aGame()

        val id = save(game)
        val savedGame = findById(id)

        assertThat(savedGame).isEqualTo(game)
    }

    @Disabled
    @Test
    fun `can update an existing game`() {

    }
}

fun aGame() = Game(
    activePlayer = anInt(1, 2),
    player1 = aPlayer(),
    player2 = aPlayer()
)

fun aPlayer(
    health: Int = anInt(0, 30),
    manaSlots: ManaSlots = aManaSlots(),
    deck: Deck = aDeck(),
    hand: Hand = aHand()
) = Player(
    health = health,
    manaSlots = manaSlots,
    deck = deck,
    hand = hand
)

fun aHand(): Hand = Hand(cards = listOfInt(genInt = aCard, minSize = 0, maxSize = 20))

fun aDeck(): Deck = Deck(cards = listOfInt(genInt = aCard, minSize = 0, maxSize = 20))

fun aManaSlots(): ManaSlots = anInt(0, 20)
    .let { slots -> ManaSlots(slots = slots, mana = anInt(0, slots)) }

val aCard = { Card(anInt(0, 9)) }

fun anInt(min: Int, max: Int): Int = Random.nextInt(min, max)
fun <T> listOfInt(genInt: () -> T, minSize: Int = 0, maxSize: Int = 10) =
    generateSequence(genInt)
        .take(anInt(minSize, maxSize))
        .toList()