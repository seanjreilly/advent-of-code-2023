package utils

import java.math.BigInteger

internal fun BigInteger.lcm(other: BigInteger): BigInteger {
    val gcd: BigInteger = this.gcd(other)
    val absProduct: BigInteger = this.multiply(other).abs()
    return absProduct.divide(gcd)
}