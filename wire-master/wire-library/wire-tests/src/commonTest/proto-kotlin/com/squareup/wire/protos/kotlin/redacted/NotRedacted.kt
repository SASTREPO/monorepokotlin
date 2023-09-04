// Code generated by Wire protocol buffer compiler, do not edit.
// Source: squareup.protos.kotlin.redacted_test.NotRedacted in redacted_test.proto
package com.squareup.wire.protos.kotlin.redacted

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.Syntax.PROTO_2
import com.squareup.wire.WireField
import com.squareup.wire.internal.sanitize
import kotlin.Any
import kotlin.AssertionError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.Long
import kotlin.Nothing
import kotlin.String
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class NotRedacted(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val a: String? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val b: String? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<NotRedacted, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is NotRedacted) return false
    if (unknownFields != other.unknownFields) return false
    if (a != other.a) return false
    if (b != other.b) return false
    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + a.hashCode()
      result = result * 37 + b.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (a != null) result += """a=${sanitize(a)}"""
    if (b != null) result += """b=${sanitize(b)}"""
    return result.joinToString(prefix = "NotRedacted{", separator = ", ", postfix = "}")
  }

  fun copy(
    a: String? = this.a,
    b: String? = this.b,
    unknownFields: ByteString = this.unknownFields
  ): NotRedacted = NotRedacted(a, b, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<NotRedacted> = object : ProtoAdapter<NotRedacted>(
      FieldEncoding.LENGTH_DELIMITED, 
      NotRedacted::class, 
      "type.googleapis.com/squareup.protos.kotlin.redacted_test.NotRedacted", 
      PROTO_2, 
      null
    ) {
      override fun encodedSize(value: NotRedacted): Int {
        var size = value.unknownFields.size
        size += ProtoAdapter.STRING.encodedSizeWithTag(1, value.a)
        size += ProtoAdapter.STRING.encodedSizeWithTag(2, value.b)
        return size
      }

      override fun encode(writer: ProtoWriter, value: NotRedacted) {
        ProtoAdapter.STRING.encodeWithTag(writer, 1, value.a)
        ProtoAdapter.STRING.encodeWithTag(writer, 2, value.b)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): NotRedacted {
        var a: String? = null
        var b: String? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> a = ProtoAdapter.STRING.decode(reader)
            2 -> b = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return NotRedacted(
          a = a,
          b = b,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: NotRedacted): NotRedacted = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}