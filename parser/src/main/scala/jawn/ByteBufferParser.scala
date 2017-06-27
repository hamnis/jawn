package org.typelevel.jawn

import scala.annotation.{switch, tailrec}
import java.nio.ByteBuffer

/**
 * Basic ByteBuffer parser.
 *
 * This assumes that the provided ByteBuffer is ready to be read. The
 * user is responsible for any necessary flipping/resetting of the
 * ByteBuffer before parsing.
 *
 * The parser makes absolute calls to the ByteBuffer, which will not
 * update its own mutable position fields.
 */
final class ByteBufferParser[J](src: ByteBuffer) extends SyncParser[J] with ByteBasedParser[J] {
  private[this] final val start = src.position()
  private[this] final val limit = src.limit() - start

  private[this] var lineState = 0
  private[this] var offset = 0
  protected[this] def line(): Int = lineState

  protected[this] final def newline(i: Int): Unit = { lineState += 1; offset = i + 1 }
  protected[this] final def column(i: Int) = i - offset

  protected[this] final def close(): Unit = { src.position(src.limit) }
  protected[this] final def reset(i: Int): Int = i
  protected[this] final def checkpoint(state: Int, i: Int, context: RawFContext[J], stack: List[RawFContext[J]]): Unit = {}
  protected[this] final def byte(i: Int): Byte = src.get(i + start)
  protected[this] final def at(i: Int): Char = src.get(i + start).toChar

  protected[this] final def at(i: Int, k: Int): CharSequence = {
    val len = k - i
    val arr = new Array[Byte](len)
    src.position(i + start)
    src.get(arr, 0, len)
    src.position(start)
    new String(arr, utf8)
  }

  protected[this] final def atEof(i: Int) = i >= limit
}
