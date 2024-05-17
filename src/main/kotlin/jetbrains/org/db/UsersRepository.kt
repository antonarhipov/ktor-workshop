package jetbrains.org.db

import jetbrains.org.model.User

interface UsersRepository {
    fun findAll(): List<User>
    fun save(user: User): Boolean
    fun find(id: Int): User?
    fun delete(user: User): Boolean
}