// Code generated by Wire protocol buffer compiler, do not edit.
// Source: squareup.protos.kotlin.SomeResponse in service_kotlin.proto
package com.squareup.wire.protos.kotlin.services

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.Syntax.PROTO_2
import kotlin.Any
import kotlin.AssertionError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.Long
import kotlin.Nothing
import kotlin.String
import kotlin.jvm.JvmField
import okio.ByteString

class SomeResponse(
  unknownFields: ByteString = ByteString.EMPTY
) : Message<SomeResponse, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is SomeResponse) return false
    if (unknownFields != other.unknownFields) return false
    return true
  }

  override fun hashCode(): Int = unknownFields.hashCode()

  override fun toString(): String = "SomeResponse{}"

  fun copy(unknownFields: ByteString = this.unknownFields): SomeResponse =
      SomeResponse(unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<SomeResponse> = object : ProtoAdapter<SomeResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      SomeResponse::class, 
      "type.googleapis.com/squareup.protos.kotlin.SomeResponse", 
      PROTO_2, 
      null
    ) {
      override fun encodedSize(value: SomeResponse): Int {
        var size = value.unknownFields.size
        return size
      }

      override fun encode(writer: ProtoWriter, value: SomeResponse) {
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): SomeResponse {
        val unknownFields = reader.forEachTag(reader::readUnknownField)
        return SomeResponse(
          unknownFields = unknownFields
        )
      }

      override fun redact(value: SomeResponse): SomeResponse = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
