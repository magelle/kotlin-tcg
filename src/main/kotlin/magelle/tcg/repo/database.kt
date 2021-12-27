package magelle.tcg.repo

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

val dbConnect = {
    Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver"
    )
}

val buildSchema = {
    transaction {
        SchemaUtils.create(Players)
        SchemaUtils.create(Decks)
        SchemaUtils.create(Hands)
        SchemaUtils.create(Games)
    }
}

object Decks : Table() {
    val player = reference("player_id", Players.id)
    val card = integer("mana_cost")
}

object Hands : Table() {
    val player = reference("player_id", Players.id)
    val card = integer("mana_cost")
}

object Players : UUIDTable() {
    val health = integer("health")
    val manaSlots = integer("mana_slots")
    val mana = integer("mana")
}

object Games : UUIDTable() {
    val activePlayer = integer("active_player")
    val player1 = reference("player_1_id", Players.id)
    val player2 = reference("player_2_id", Players.id)
}