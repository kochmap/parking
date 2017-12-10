package pl.kochmap.parking

import java.time.Duration

object Util {
  implicit class LongToDurationConversion(time: Long) {
    def hour: Duration = hours

    def hours: Duration = Duration.ofHours(time)

    def seconds: Duration = Duration.ofSeconds(time)
  }

}
