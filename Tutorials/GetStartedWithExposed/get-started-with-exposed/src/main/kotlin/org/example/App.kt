package org.example

import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

fun main() {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

    transaction {
        // sql문을 std-out으로 출력
        addLogger(StdOutSqlLogger)

        // Tasks 테이블 생성
        SchemaUtils.create(Tasks)

        // Tasks 값 삽입 1
        // INSERT INTO TASKS (COMPLETED, DESCRIPTION, "name") VALUES (FALSE, 'Go through the Get started with Exposed tutorial', 'Learn Exposed')
        val taskId = Tasks.insert {
            it[title] = "Learn Exposed"
            it[description] = "Go through the Get started with Exposed"
        } get Tasks.id

        // Tasks 값 삽입 2
        // INSERT INTO TASKS (COMPLETED, DESCRIPTION, "name") VALUES (TRUE, 'Read the first two chapters of The Hobbit', 'Read The Hobbit')
        val secondTaskId = Tasks.insert {
            it[title] = "Read the Hobbit"
            it[description] = "Read the first two chapters of the Hobbit"
            it[isCompleted] = true
        } get Tasks.id

        println("Created new tasks with ids $taskId and $secondTaskId")

        // Tasks 테이블에 select 쿼리 실행
        // SELECT COUNT(TASKS.ID), TASKS.COMPLETED FROM TASKS GROUP BY TASKS.COMPLETED
        Tasks.select(Tasks.id.count(), Tasks.isCompleted)
            .groupBy(Tasks.isCompleted)
            .forEach { println("${it[Tasks.isCompleted]}: ${it[Tasks.id.count()]}") }

        println("Remaining tasks: ${Tasks.selectAll().toList()}")

        // Task 업데이트
        Tasks.update({ Tasks.id eq taskId }) {
            it[isCompleted] = true
        }

        val updatedTask = Tasks.select(Tasks.isCompleted)
            .where(Tasks.id eq taskId)
            .single()

        println("Updated task details: $updatedTask")

        // Task 삭제
        Tasks.deleteWhere { id eq secondTaskId }

        println("Remaining tasks: ${Tasks.selectAll().toList()}")
    }
}
