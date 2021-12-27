package magelle.tcg.http

import kotlinx.serialization.Serializable

@Serializable
data class GameDTO(
    val gameOver: Boolean,
    val player1: PlayerDTO,
    val player2: PlayerDTO,
    val activePlayer: Int
)

@Serializable
data class PlayerDTO(
    val health: Int,
    val manaSlots: Int,
    val mana: Int,
    val hand: List<Int>
)
