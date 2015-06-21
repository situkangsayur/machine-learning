/*
 * Copyright (c) 2015, DeepWissen and/or its affiliates. All rights reserved.
 * DEEPWISSEN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.deepwissen.ml.opendata

import java.io.{File, FileOutputStream}

import com.deepwissen.ml.algorithm.{BasicClassification, BasicBackpropagation, BackpropragationParameter}
import com.deepwissen.ml.function.{SigmoidFunction, RangeThresholdFunction}
import com.deepwissen.ml.serialization.NetworkSerialization
import com.deepwissen.ml.validation.Validation
import org.scalatest.FunSuite

/**
 * @author Eko Khannedy
 * @since 2/28/15
 */
class OpenDataTest extends FunSuite {

  val csv =
    """
      |2004,1,3,6.4,10125796,0,331,1000.23,5012666667,3400666667,3.33
      |2004,2,3.33,6.4,10125796,0,331,1000.23,5599000000,3587000000,2.8
      |2004,3,2.8,6.4,10125796,0,331,1000.23,6533000000,4179000000,2.51
      |2004,4,2.51,6.4,10125796,0,331,1000.23,6717000000,4341333333,2.08
      |2005,1,2.08,17.11,11 156 821,12.75,336,1162.63,6626333333,4526666667,3.38
      |2005,2,3.38,17.11,11 156 821,12.75,336,1162.63,6956666667,4961000000,3.72
      |2005,3,3.72,17.11,11 156 821,12.75,336,1162.63,7317000000,5131666667,3.38
      |2005,4,3.38,17.11,11 156 821,12.75,336,1162.63,7653666667,4614666667,8.58
      |2006,1,8.58,6.6,10 469 558,9.75,344,1805.52,7484333333,4442333333,1.13
      |2006,2,1.13,6.6,10 469 558,9.75,344,1805.52,8155000000,5200666667,1.75
      |2006,3,1.75,6.6,10 469 558,9.75,344,1805.52,8878666667,5592000000,3.19
      |2006,4,3.19,6.6,10 469 558,9.75,344,1805.52,9081666667,5120000000,2.25
      |2007,1,2.25,6.59,9 531 965,8,383,2745.83,8527333333,5198000000,3.25
      |2007,2,3.25,6.59,9 531 965,8,383,2745.83,9426000000,6038000000,2
      |2007,3,2,6.59,9 531 965,8,383,2745.83,9717333333,6689666667,3.1
      |2007,4,3.1,6.59,9 531 965,8,383,2745.83,10361333333,6899333333,3.16
      |2008,1,3.16,11.06,9 154 326,9.25,396,1355.41,11250000000,9910333333,4.68
      |2008,2,4.68,11.06,9 154 326,9.25,396,1355.41,12216666667,11806666667,7
      |2008,3,7,11.06,9 154 326,9.25,396,1355.41,12426666667,12166666667,4.83
      |2008,4,4.83,11.06,9 154 326,9.25,396,1355.41,9784000000,9184333333,0.82
      |2009,1,0.82,2.78,8 754 736,6.5,398,2534.36,7676333333,6364666667,0.24
      |2009,2,0.24,2.78,8 754 736,6.5,398,2534.36,9014666667,7427666667,2.58
      |2009,3,2.58,2.78,8 754 736,6.5,398,2534.36,10022333333,8969000000,1.62
      |2009,4,1.62,2.78,8 754 736,6.5,398,2534.36,12123333333,9515000000,1.91
      |2010,1,1.91,6.96,8 254 426,6.5,420,3703.51,11846666667,9986000000,1.68
      |2010,2,1.68,6.96,8 254 426,6.5,420,3703.51,12330000000,10993333333,2.75
      |2010,3,2.75,6.96,8 254 426,6.5,420,3703.51,12800000000,11484666667,1.69
      |2010,4,1.69,6.96,8 254 426,6.5,420,3703.51,15620000000,12760000000,2.11
      |2011,1,2.11,3.79,8 681 392,6,440,3821.99,15133333333,12933333333,2.3
      |2011,2,2.3,3.79,8 681 392,6,440,3821.99,17743333333,14930000000,1.32
      |2011,3,1.32,3.79,8 681 392,6,440,3821.99,17870000000,15486666667,2.45
      |2011,4,2.45,3.79,8 681 392,6,440,3821.99,17093333333,15800000000,0.93
      |2012,1,0.93,4.3,7 344 866,5.75,459,4316.69,16173333333,15250000000,1.24
      |2012,2,1.24,4.3,7 344 866,5.75,459,4316.69,16146666667,16903333333,0.94
      |2012,3,0.94,4.3,7 344 866,5.75,459,4316.69,15346666667,15170000000,0.16
      |2012,4,0.16,4.3,7 344 866,5.75,459,4316.69,15676666667,16576666667,0.37
      |2013,1,0.37,8.38,7 410 931,7.5,483,4274.18,15140000000,15216666667,1
      |2013,2,1,8.38,7 410 931,7.5,483,4274.18,15216666667,16253333333,0.63
      |2013,3,0.63,8.38,7 410 931,7.5,483,4274.18,14293333333,15313333333,3.47
      |2013,4,3.47,8.38,7 410 931,7.5,483,4274.18,16203333333,15426666667,1.8
      |2014,1,1.8,8.36,7 244 905,7.75,501,5149.89,14470000000,14920000000,0.55
      |2014,2,0.55,8.36,7 244 905,7.75,501,5149.89,14470000000,14920000000,0.71
    """.stripMargin.trim

  val dataset = csv.split("\n").map { value =>
    value.split(",").map(value => value.replaceAll(" ", "").toDouble)
  }.toList

  val datasetClone = dataset.map { data => data.clone() }
  dataset.foreach(r => println(r.mkString(",")))

  (0 until dataset(0).length).foreach { i =>
    val min = dataset.foldLeft(dataset(0)(i)) { (value, current) =>
      if (value > current(i)) current(i) else value
    }
    val max = dataset.foldLeft(dataset(0)(i)) { (value, current) =>
      if (value > current(i)) value else current(i)
    }

    dataset.foreach { data =>
      data(i) = (data(i) - min) / (max - min)
    }
  }

  println("-----")
  dataset.foreach(r => println(r.mkString(",")))

  val parameter = BackpropragationParameter(
    hiddenLayerSize = 1,
    outputPerceptronSize = 1,
    targetClassPosition = -1,
    iteration = 70000,
    epsilon = 0.000000001,
    momentum = 0.75,
    learningRate = 0.5,
    inputPerceptronSize = dataset.head.length - 1
  )

  test("create model") {
    val network = BasicBackpropagation.train(dataset, parameter)

    val result = Validation.classification(network, BasicClassification, dataset, SigmoidFunction)
    val validate = Validation.validate(result, dataset, dataset.head.length - 1).map {
      case (key, value) => (math.round(key * 100) / 100.0) -> (math.round(value * 100) / 100.0)
    }
    validate.foreach(data => println(s" prediksi ${data._1} => data asli ${data._2}"))
    val accuration = Validation.accuration(validate)(RangeThresholdFunction(0.01))
    println("accuration => " + accuration)

    NetworkSerialization.save(network, new FileOutputStream(new File("target" + File.separator + "gdp.json")))
  }

}
