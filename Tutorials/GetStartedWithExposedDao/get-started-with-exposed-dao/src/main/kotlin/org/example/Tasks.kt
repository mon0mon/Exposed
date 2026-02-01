package org.example

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object Tasks : IntIdTable("tasks") {
    val title = varchar("name", 128)
    val description = varchar("description", 128)
    val isCompleted = bool("completed").default(false)
}
