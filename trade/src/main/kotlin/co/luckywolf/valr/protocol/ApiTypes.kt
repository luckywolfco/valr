package co.luckywolf.valr.protocol

import arrow.core.*
import co.luckywolf.valr.exchange.Trade.toDecimalPlaces
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

  class PlaceLimitOrderApiRequest(
    val currentPair: DataTypes.CurrencyPair,
    val side: DataTypes.Side,
    val quantity: Double,
    val price: Double,
    val postOnly: Boolean,
    val customerOrderId: Option<String>,
    val timeInForce: DataTypes.TimeInForce
  ) {
    companion object {
      fun create(accountId: String, request: JsonObject): Either<Validators.ApiError, PlaceLimitOrderApiRequest> {

        val side = DataTypes.Side.valueOf(request.getString("side"))
        val quantity = request.getString("quantity").toDouble().toDecimalPlaces()
        val price = request.getString("price").toDouble().toDecimalPlaces()
        val pair = DataTypes.CurrencyPair.valueOf(request.getString("pair"))
        val tif =
          Option.fromNullable(request.getString("timeInForce")).map { DataTypes.TimeInForce.valueOf(it) }
            .getOrElse { DataTypes.TimeInForce.GTC }
        val customerOrderId = Option.fromNullable(request.getString("customerOrderId"))
        val postOnly = Option.fromNullable(request.getBoolean("postOnly")).getOrElse { false }

        return Either.Right(
          PlaceLimitOrderApiRequest(
            currentPair = pair,
            side,
            quantity,
            price,
            postOnly,
            customerOrderId,
            tif
          )
        )
      }
    }
  }

  class GetTradeHistoryApiRequest(val currencyPair: DataTypes.CurrencyPair, val skip: Int, val limit: Int) {
    companion object {
      fun create(
        path: Map<String, String>,
        request: MultiMap
      ): Either<Validators.ApiError, GetTradeHistoryApiRequest> {
        val currencyPair = DataTypes.CurrencyPair.valueOf(path.getValue("currencyPair"))
        val limit = request.get("limit").parseInt(0)
        val skip = request.get("skip").parseInt(100)
        return Either.Right(GetTradeHistoryApiRequest(currencyPair, skip, limit))
      }
    }
  }

  class GetOrderHistoryApiRequest(val currencyPair: DataTypes.CurrencyPair, val limit: Int = 40) {
    companion object {
      fun create(
        path: Map<String, String>,
        request: MultiMap
      ): Either<Validators.ApiError, GetOrderHistoryApiRequest> {
        val currencyPair = DataTypes.CurrencyPair.valueOf(path.getValue("currencyPair"))
        return Either.Right(GetOrderHistoryApiRequest(currencyPair))
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

  fun String.parseInt(default: Int): Int {
    return toIntOrNull() ?: default
  }

  object Validators {

    sealed class ApiError(val error: String) {
      data class DoesNotContain(val value: String) : ApiError("Missing $value value")
      data class MaxLength(val value: Int) : ApiError("Exceeded length of $value")
      data class NotAnEmail(val reasons: Nel<ApiError>) : ApiError("Not a valid email")
      data class InsufficientPermissions(val value: String) :
        ApiError("Insufficient permissions to perform this action - required $value")
    }
  }
}
