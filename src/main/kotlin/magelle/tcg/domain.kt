package magelle.tcg

import arrow.core.compose
import arrow.optics.*

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

data class ManaSlots(
    val slots: Int,
    val mana: Int
)

val mana = Lens<ManaSlots, ManaSlots, Int, Int>(
    get = ManaSlots::mana,
    set = { player, mana -> player.copy(mana = mana) }
)

val slots = Lens<ManaSlots, ManaSlots, Int, Int>(
    get = ManaSlots::slots,
    set = { player, slots -> player.copy(slots = slots) }
)

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

val playerDeckCards = playerDeck compose deckCards
val playerHandCards = playerHand compose handCards
val removeFirstCardOfDeck = playerDeckCards.lift { it.uncons()?.second ?: emptyList() }
val addCardToHand = { card: Card -> playerHandCards.lift { it + card } }

fun drawCard(player: Player): Player {
    val headTailsDeck = playerDeckCards.get(player).uncons()!!
    val draw = removeFirstCardOfDeck compose addCardToHand(headTailsDeck.first)
    return draw(player)
}

val playerManaSlots = playerMana compose slots
val playerManaCount = playerMana compose mana
val fillManaSlots = { player: Player ->
    playerManaCount.set(player, playerManaSlots.get(player))
}

val player1DrawCard = player1.lift(::drawCard)
val player2DrawCard = player2.lift(::drawCard)
val player1DrawHand = player1DrawCard compose player1DrawCard compose player1DrawCard
val player2DrawHand = player2DrawCard compose player2DrawCard compose player2DrawCard
val player1ManaSlots = player1 compose playerManaSlots
val player2ManaSlots = player2 compose playerManaSlots
val player1Mana = player1 compose playerManaCount
val player2Mana = player2 compose playerManaCount
val player1Health = player1 compose playerHealth
val player2Health = player2 compose playerHealth

val addManaSlot = player1ManaSlots.lift(Int::inc)
val fillActivePlayerManaSlots = player1.lift(fillManaSlots)
val activePlayerDrawCard = player1DrawCard

val reduceActivePLayerMana = { manaCost: Int -> player1Mana.lift { mana -> mana - manaCost } }
val reduceOpponentHealth = { manaCost: Int -> player2Health.lift { health -> health - manaCost } }

val activePlayerPlayCard = { card: Card ->
    reduceActivePLayerMana(card.manaCost) compose reduceOpponentHealth(card.manaCost)
}
