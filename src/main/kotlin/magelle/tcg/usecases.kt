package magelle.tcg

fun createGame(
    player1Deck: Deck,
    player2Deck: Deck
) = Game(
    player1 = createPlayer(player1Deck),
    player2 = createPlayer(player2Deck)
)

fun drawHands(game: Game) = game.copy()


private fun createPlayer(player1Deck: Deck) = Player(
    health = 30,
    manaSlots = ManaSlots(slots = listOf()),
    deck = player1Deck,
    hand = Hand(cards = listOf())
)