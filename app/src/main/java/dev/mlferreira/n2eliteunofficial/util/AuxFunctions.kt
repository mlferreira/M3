package dev.mlferreira.n2eliteunofficial.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.math.BigInteger


fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

fun Long.toHex(): String = toString(16)

fun Int.toHex(): String = toString(16)

fun String.toHexBigInt(): BigInteger = BigInteger(this, 16)