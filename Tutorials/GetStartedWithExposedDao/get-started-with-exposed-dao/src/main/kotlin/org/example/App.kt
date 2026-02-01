package org.example

import org.jetbrains.exposed.v1.jdbc.Database

fun main() {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
}
