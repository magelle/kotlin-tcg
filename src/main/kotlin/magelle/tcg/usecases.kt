package magelle.tcg

fun startGame() = Game(
    player1 = Player(
        health = 30,
        manaSlots = ManaSlots(slots = listOf()),
        deck = Deck(cards = listOf()),
        hand = Hand(cards = listOf())
    ),
    player2 = Player(
        health = 30,
        manaSlots = ManaSlots(slots = listOf()),
        deck = Deck(cards = listOf()),
        hand = Hand(cards = listOf())
    )
)