package sparkSQL;


import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;
import scala.Tuple3;


import java.util.ArrayList;
import java.util.List;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.stddev_pop;

public class SQLQuery2 {

    public static Dataset<Row> executeQuery(SparkSession spark,JavaPairRDD<Tuple3<Integer, Integer, String>, Tuple2<Double,Double> > values) {


        //Dataset df = sparkCore.read().format("csv").option("header", "true").load(inputPath2);

        //creo lo schema
        List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField("year", DataTypes.IntegerType, true));
        fields.add(DataTypes.createStructField("month", DataTypes.IntegerType, true));
        fields.add(DataTypes.createStructField("country", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("value", DataTypes.DoubleType, true));
        fields.add(DataTypes.createStructField("count", DataTypes.DoubleType, true));

        StructType schema = DataTypes.createStructType(fields);



        JavaRDD<Row> rows = values.map(new Function<Tuple2<Tuple3<Integer, Integer, String>, Tuple2<Double,Double>>, Row>() {
            @Override
            public Row call(Tuple2<Tuple3<Integer, Integer, String>, Tuple2<Double,Double>> tuple) throws Exception {
                return RowFactory.create(tuple._1()._1(), tuple._1()._2(), tuple._1()._3(), tuple._2()._1(), tuple._2()._2);
            }
        });

        Dataset<Row> df = spark.createDataFrame(rows, schema);



        // Register the DataFrame as a SQL temporary view
        df.createOrReplaceTempView("statistics");
        //conteggio del cielo sereno per ogni città, per ogni giorno, per ogni mese
        return spark.sql(
                "SELECT country,year, month, MEAN(value) AS mean ,MIN(value) as min, MAX(value) as max, STDDEV_SAMP(value) as stddev " +
                        "FROM statistics  " +
                        "GROUP BY country,year,month");



    }
}
