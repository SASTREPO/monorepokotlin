// Code generated by Wire protocol buffer compiler, do not edit.
// Source: squareup.protos.kotlin.foreign.ForeignEnum in foreign.proto
package com.squareup.wire.protos.kotlin.foreign

import com.squareup.wire.EnumAdapter
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.Syntax.PROTO_2
import com.squareup.wire.WireEnum
import kotlin.Int
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

enum class ForeignEnum(
  override val value: Int
) : WireEnum {
  BAV(0),

  BAX(1);

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<ForeignEnum> = object : EnumAdapter<ForeignEnum>(
      ForeignEnum::class, 
      PROTO_2, 
      ForeignEnum.BAV
    ) {
      override fun fromValue(value: Int): ForeignEnum? = ForeignEnum.fromValue(value)
    }

    @JvmStatic
    fun fromValue(value: Int): ForeignEnum? = when (value) {
      0 -> BAV
      1 -> BAX
      else -> null
    }
  }
}