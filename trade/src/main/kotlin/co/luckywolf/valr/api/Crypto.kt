package co.luckywolf.valr.api

import arrow.core.Option
import arrow.core.none
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


object Crypto {

  class MD5 private constructor(private val provider: Optional<String>) {
    companion object {

      fun default(): MD5 {
        return MD5(Optional.empty())
      }
    }

    fun hash(data: String): ByteArray {
      val md = provider.map { MessageDigest.getInstance("MD5", it) }
        .orElse(MessageDigest.getInstance("MD5"))

      md.update(data.toByteArray())
      return md.digest()
    }
  }

  class Hmac512 private constructor(private val provider: Optional<String>) {

    private val ALGORITHM = "HmacSHA512"

    companion object {

      fun default(): Hmac512 {
        return Hmac512(Optional.empty())
      }
    }

    fun sign(
      apiKeySecret: ByteArray,
      timestamp: String,
      verb: String,
      path: String,
      body: Option<String> = none()
    ): ByteArray {
      val hmac = provider.map { Mac.getInstance(ALGORITHM, it) }
        .orElse(Mac.getInstance(ALGORITHM))

      with(hmac) {
        init(SecretKeySpec(apiKeySecret, ALGORITHM))
        update(timestamp.toByteArray())
        update(verb.toByteArray())
        update(path.toByteArray())
        body.map { update(it.toByteArray()) }
      }

      return hmac.doFinal()
    }

  }

}
