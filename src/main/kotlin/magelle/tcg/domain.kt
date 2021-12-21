package magelle.tcg

data class Card(
    val manaCost: Int
)

data class Deck(
    val cards: List<Card>
)

data class Hand(
    val cards: List<Card>
)

data class ManaSlot(
    val full: Boolean
)

data class ManaSlots(
    val slots: List<ManaSlot>
)

data class Player(
    val health: Int,
    val manaSlots: ManaSlots,
    val deck: Deck,
    val hand: Hand,
)

data class Game(
    val player1: Player,
    val player2: Player
)
