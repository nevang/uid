package gr.jkl.uid.benchmark

import com.google.caliper.{ Runner => CaliperRunner }

object Runner {

  def main(args: Array[String]) {

    CaliperRunner.main(classOf[Benchmark], args)
  }

}
