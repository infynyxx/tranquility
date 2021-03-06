/*
 * Tranquility.
 * Copyright 2013, 2014, 2015  Metamarkets Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.metamx.tranquility.typeclass

import com.fasterxml.jackson.core.{JsonFactory, JsonGenerator}
import com.metamx.common.scala.Predef._
import java.io.ByteArrayOutputStream

abstract class JsonWriter[A] extends ObjectWriter[A]
{
  @transient private lazy val _jsonFactory = new JsonFactory

  override def asBytes(a: A): Array[Byte] = {
    val out = new ByteArrayOutputStream
    _jsonFactory.createGenerator(out).withFinally(_.close) {
      jg =>
        viaJsonGenerator(a, jg)
    }
    out.toByteArray
  }

  override def batchAsBytes(as: TraversableOnce[A]): Array[Byte] = {
    val out = new ByteArrayOutputStream
    _jsonFactory.createGenerator(out).withFinally(_.close) {
      jg =>
        jg.writeStartArray()
        as foreach (viaJsonGenerator(_, jg))
        jg.writeEndArray()
    }
    out.toByteArray
  }

  protected def viaJsonGenerator(a: A, jg: JsonGenerator)
}
