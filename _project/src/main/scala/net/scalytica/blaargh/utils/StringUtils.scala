/**
  * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
  */
package net.scalytica.blaargh.utils

object StringUtils {

  def asOption(str: String): Option[String] = if (str.isEmpty) None else Some(str)

}
