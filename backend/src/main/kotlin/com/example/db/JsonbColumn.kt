package com.example.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.postgresql.util.PGobject

// Конвектор медлу DB и Kotlin (Exposed)
// Kotlin (string) <-> DB (JsonB)

class JsonbColumnType : ColumnType<String>() {
    override fun sqlType(): String = "JSONB"

    override fun valueFromDB(value: Any): String =
        when (value) {
            is PGobject -> value.value ?: "{}"
            is String -> value
            is ByteArray -> value.decodeToString()
            else -> error("Unexpected value for JSONB column: ${value::class.qualifiedName}")
        }

    override fun notNullValueToDB(value: String): Any =
        PGobject().apply {
            type = "jsonb"
            this.value = value
        }

    override fun nonNullValueToString(value: String): String =
        "'${value.replace("'", "''")}'::jsonb"
}

fun Table.jsonb(name: String): Column<String> = registerColumn(name, JsonbColumnType())
