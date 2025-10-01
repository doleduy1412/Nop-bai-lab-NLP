package com.duyle.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.sql.functions._
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

  object Lab17_NLPPipeline {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Lab17 NLP Pipeline")
      .master("local[*]") // chạy local
      .config("spark.driver.extraJavaOptions", "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED")
      .config("spark.executor.extraJavaOptions", "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    // 1. Load Data
    val inputPath = "data/c4-train.00000-of-01024-30K.json.gz"
    val textCol = "text"
    val dfRaw = spark.read.json(inputPath)
    // Đảm bảo có cột text dạng String, thay null thành "", lọc bỏ dòng rỗng và giới hạn 2000
    val df = dfRaw
      .select(coalesce(col(textCol).cast("string"), lit("")).as(textCol))
      .filter(length(col(textCol)) > 0)
      .limit(2000)
      .cache()
    val rowCount = df.count()
    println(s"Loaded ${rowCount} rows")

    // Cột text chính trong C4 dataset

    // 2. Tokenization 
    val tokenizer = new RegexTokenizer()
      .setInputCol(textCol)
      .setOutputCol("tokens")
      .setPattern("\\W+") // tách theo ký tự không phải chữ bằng biểu thức chính quy

    // (Nếu muốn đổi sang Tokenizer thường, thay bằng:)
    // val tokenizer = new Tokenizer().setInputCol(textCol).setOutputCol("tokens")

    //  3. Stop Word Removal
    val remover = new StopWordsRemover()
      .setInputCol("tokens")
      .setOutputCol("filtered")

    // 4. TF-IDF Vectorization 
    val hashingTF = new HashingTF()
      .setInputCol("filtered")
      .setOutputCol("rawFeatures")
      .setNumFeatures(20000)

    val idf = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")

    // 5. Pipeline 
    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, remover, hashingTF, idf))

    val model = pipeline.fit(df)
    val result = model.transform(df)

    // 6. Save Results
    val outputPath = "results/lab17_pipeline_output.txt"
    val resultsDir = Paths.get("results")
    if (!Files.exists(resultsDir)) Files.createDirectories(resultsDir) // tạo file output nếu file chưa tồn tại

    val textOut = result.select("features").limit(20).collect().map(_.toString()).mkString("\n")
    Files.write(Paths.get(outputPath), textOut.getBytes(StandardCharsets.UTF_8))

    println(s"Pipeline completed. Results saved at $outputPath")

    // 7. Log 
    val logDir = Paths.get("log")
    if (!Files.exists(logDir)) Files.createDirectories(logDir) // tạo file log nếu file chưa tồn tại
    val logPath = logDir.resolve("lab17_log.txt")
    val logMsg = s"Bắt đầu: ${java.time.Instant.now}\n" +
      s"Processed rows: ${rowCount}\n" +
      s"Saved output to: $outputPath\n" +
      s"Hoàn thành: ${java.time.Instant.now}\n"
    Files.write(logPath, logMsg.getBytes(StandardCharsets.UTF_8))

    spark.stop()
  }
}


