package jetbrains.org.db.fake

import jetbrains.org.db.UsersRepository
import jetbrains.org.model.User

class FakeUsersRepository : UsersRepository {

    var storage = mutableListOf<User>()

    override fun findAll(): List<User> = storage

    override fun save(user: User): Boolean = storage.add(user)

    override fun find(id: Int): User? = storage.find { it.userId == id }

    override fun delete(user: User): Boolean = storage.remove(user)

}