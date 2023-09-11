package io.keodash.databases

import io.keodash.user.UserPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UserDto(
    val name: String,
    val password: String,
    val email: String,
    val permission: UserPermission,
    val isActive: Boolean = true
)

@Serializable
data class UserDao(
    val id: Int,
    val name: String,
    val password: String,
    val email: String,
    val permission: UserPermission,
    val isActive: Boolean = true,
    val createdAt: Long,
    val salt: String
)

class UserService(
    private val database: Database
) {

    object Users : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 20)
        val password = varchar("password", length = 100)
        val email = varchar("email", length = 100)
        val permission = enumeration<UserPermission>("permission")
        val isActive = bool("is_active")
        val createdAt = long("createdAt")
        val salt = varchar("salt", length = 30)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(user: UserDto): Int = dbQuery {
        val decidedSalt = BCrypt.gensalt()

        Users.insert {
            it[name] = user.name
            it[password] = BCrypt.hashpw(user.password, decidedSalt)
            it[email] = user.email
            it[permission] = user.permission
            it[isActive] = user.isActive
            it[createdAt] = System.currentTimeMillis()
            it[salt] = decidedSalt
        }[Users.id]
    }

    suspend fun read(id: Int): UserDao? {
        return dbQuery {
            Users.select { Users.id eq id }
                .map { row ->
                    UserDao(
                        id = row[Users.id],
                        name = row[Users.name],
                        password = row[Users.password],
                        email = row[Users.email],
                        permission = row[Users.permission],
                        isActive = row[Users.isActive],
                        createdAt = row[Users.createdAt],
                        salt = row[Users.salt]
                    ) }
                .singleOrNull()
        }
    }

    suspend fun readByName(name: String): UserDao? {
        return dbQuery {
            Users.select { Users.name eq name }
                .map { row ->
                    UserDao(
                        id = row[Users.id],
                        name = row[Users.name],
                        password = row[Users.password],
                        email = row[Users.email],
                        permission = row[Users.permission],
                        isActive = row[Users.isActive],
                        createdAt = row[Users.createdAt],
                        salt = row[Users.salt]
                    ) }
                .singleOrNull()
        }
    }

    suspend fun readByEmail(email: String): UserDao? {
        return dbQuery {
            Users.select { Users.email eq email }
                .map { row ->
                    UserDao(
                        id = row[Users.id],
                        name = row[Users.name],
                        password = row[Users.password],
                        email = row[Users.email],
                        permission = row[Users.permission],
                        isActive = row[Users.isActive],
                        createdAt = row[Users.createdAt],
                        salt = row[Users.salt]
                    ) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: UserDto) {
        dbQuery {
            val decidedSalt = BCrypt.gensalt()

            Users.update({ Users.id eq id }) {
                it[name] = user.name
                it[password] = BCrypt.hashpw(user.password, decidedSalt)
                it[email] = user.email
                it[permission] = user.permission
                it[isActive] = user.isActive
                it[salt] = decidedSalt
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }

}