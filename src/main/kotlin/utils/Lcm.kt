package utils

import java.math.BigInteger

fun BigInteger.lcm(other: BigInteger): BigInteger {
    val gcd: BigInteger = this.gcd(other)
    val absProduct: BigInteger = this.multiply(other).abs()
    return absProduct.divide(gcd)
}

fun Long.lcm(other: Long): BigInteger = this.toBigInteger().lcm(other)
fun BigInteger.lcm(other: Long) : BigInteger = this.lcm(other.toBigInteger())