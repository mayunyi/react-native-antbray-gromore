package com.antbraygromore.config

// 定义接口返回的数据类型结果
interface EventResult {
  val type: String
  val status: EventResultStatus
  val message: String
  val data: String? // JSON 数据作为字符串
}

class EventResultImpl(
  override val status: EventResultStatus,
  override val type: String,
  override val message: String,

  override val data: String? // JSON 数据作为字符串
) : EventResult
