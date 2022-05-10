package co.luckywolf.valr.protocol

import arrow.core.*
import com.google.gson.annotations.SerializedName
import io.vertx.core.MultiMap
import io.vertx.core.json.JsonObject

object ApiTypes {

  const val ApiKey = "X-VALR-API-KEY"
  const val ApiSignature = "X-VALR-SIGNATURE"
  const val ApiTimestamp = "X-VALR-TIMESTAMP"

  class ApiRequestKey(
    val key: String,
    val signature: String,
    val timestamp: String
  ) {
    companion object {
      fun create(headers: MultiMap):
        ValidatedNel<Validators.ApiError, ApiRequestKey> = when {
        !headers.contains(ApiKey) -> Validators.ApiError.DoesNotContain(ApiKey).invalidNel()
        !headers.contains(ApiSignature) -> Validators.ApiError.DoesNotContain(ApiSignature)
          .invalidNel()
        !headers.contains(ApiTimestamp) -> Validators.ApiError.DoesNotContain(ApiTimestamp)
          .invalidNel()
        else -> Valid(
          ApiRequestKey(
            headers.get(ApiKey),
            headers.get(ApiSignature),
            headers.get(ApiTimestamp)
          )
        )
      }
    }
  }

  val pattern = "a-zA-Z0-9".toRegex()

  class PlaceLimitOrderApiRequest(val currentPair: DataTypes.CurrencyPair) {
    companion object {
      fun create(accountId: String, request: JsonObject): Either<Validators.ApiError, PlaceLimitOrderApiRequest> {
        val side = request.getString("side")
        val quantity = request.getString("quantity")
        val pair = request.getString("pair")
        val customerOrderId = request.getString("customerOrderId")
        return Either.Right(PlaceLimitOrderApiRequest(DataTypes.CurrencyPair.valueOf(pair)))
      }
    }
  }

  enum class ApiPermission(val permission: String) {
    View("view"),
    Trade("trade"),
    Withdraw("withdraw"),
    Public("public")
  }

  //access - view balances, orders, and other details of the account.
//trade - place buy and sell orders
//withdraw - withdraw a currency to an address or a bank account
  enum class ApiPermissionRule(
    val apiPermission: ApiPermission,
    val pathMatchingRules: List<String>
  ) {
    View(ApiPermission.View, listOf("v1/marketdata/")),
    Trade(ApiPermission.Trade, listOf("")),
    Withdraw(ApiPermission.Withdraw, listOf("")),
    Public(ApiPermission.Public, listOf("")),
  }

  data class SubmitLimitOrderApiRequest(
    @SerializedName("side") val side: String = "sell",
  )

  data class AskApiResponse(
    @SerializedName("side") val side: String = "sell",
    @SerializedName("quantity") val quantity: String,
    @SerializedName("price") val price: String,
    @SerializedName("currencyPair") val currencyPair: String,
    @SerializedName("orderCount") val orderCount: Int
  )

  data class BidApiResponse(
    @SerializedName("side") val side: String = "buy",
    @SerializedName("quantity") val quantity: String,
    @SerializedName("price") val price: String,
    @SerializedName("currencyPair") val currencyPair: String,
    @SerializedName("orderCount") val orderCount: Int
  )


  data class OrderBookApiResponse(
    @SerializedName("Asks") val Asks: ArrayList<AskApiResponse> = arrayListOf(),
    @SerializedName("Bids") val Bids: ArrayList<BidApiResponse> = arrayListOf(),
    @SerializedName("LastChange") val LastChange: String,
    @SerializedName("SequenceNumber") val SequenceNumber: Long
  )



  object Validators {

    sealed class ApiError(val msg: String) {
      data class DoesNotContain(val value: String) : ApiError("Missing $value value")
      data class MaxLength(val value: Int) : ApiError("Exceeded length of $value")
      data class NotAnEmail(val reasons: Nel<ApiError>) : ApiError("Not a valid email")
    }
  }
}
