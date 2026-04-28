package com.example.exemplosimplesdecompose.util

fun String.toLocalizedDoubleOrNull(): Double? = replace(",", ".").toDoubleOrNull()
