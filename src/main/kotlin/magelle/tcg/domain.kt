package magelle.tcg

import arrow.optics.*
import arrow.optics.typeclasses.Cons

val firstCard = Getter(List<Card>::first)

data class Card(
    val manaCost: Int
)

val cardManaCost = Getter(Card::manaCost)

data class Deck(
    val cards: List<Card>
)

val deckCards = Lens<Deck, Deck, List<Card>, List<Card>>(
    get = Deck::cards,
    set = { _, cards -> Deck(cards) }
)
val deckSize = Getter(List<Card>::size)

data class Hand(
    val cards: List<Card>
)

val handCards = Lens<Hand, Hand, List<Card>, List<Card>>(
    get = Hand::cards,
    set = { _, cards -> Hand(cards) }
)

data class ManaSlot(
    val full: Boolean
)

val manaSlot = Lens<ManaSlot, ManaSlot, Boolean, Boolean>(
    get = ManaSlot::full,
    set = { _, full -> ManaSlot(full) }
)

data class ManaSlots(
    val slots: List<ManaSlot>
)

val manaSlots = Lens<ManaSlots, ManaSlots, List<ManaSlot>, List<ManaSlot>>(
    get = ManaSlots::slots,
    set = { _, slots -> ManaSlots(slots) }
)
val allSlots = Traversal.list<ManaSlot>()

data class Player(
    val health: Int,
    val manaSlots: ManaSlots,
    val deck: Deck,
    val hand: Hand,
)

val playerHealth = Lens<Player, Player, Int, Int>(
    get = Player::health,
    set = { player, health -> player.copy(health = health) }
)

val playerMana = Lens<Player, Player, ManaSlots, ManaSlots>(
    get = Player::manaSlots,
    set = { player, manaSlots -> player.copy(manaSlots = manaSlots) }
)

val playerDeck = Lens<Player, Player, Deck, Deck>(
    get = Player::deck,
    set = { player, deck -> player.copy(deck = deck) }
)

val playerHand = Lens<Player, Player, Hand, Hand>(
    get = Player::hand,
    set = { player, hand -> player.copy(hand = hand) }
)

data class Game(
    val player1: Player,
    val player2: Player
)

val player1 = Lens<Game, Game, Player, Player>(
    get = Game::player1,
    set = { game, player -> game.copy(player1 = player) }
)

val player2 = Lens<Game, Game, Player, Player>(
    get = Game::player2,
    set = { game, player -> game.copy(player2 = player) }
)

val playerDeckSize = playerDeck compose deckCards compose deckSize
val playerHandSize = playerHand compose handCards compose deckSize

val player1DeckSize = player1 compose playerDeckSize
val player2DeckSize = player2 compose playerDeckSize

val player1HandSize = player1 compose playerHandSize
val player2HandSize = player2 compose playerHandSize

val firstCardOfDeck = playerDeck compose deckCards compose firstCard
