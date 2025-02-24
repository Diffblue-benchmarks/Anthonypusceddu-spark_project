package sparkCore;


import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.*;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;


public class Query1 {


    public static JavaPairRDD<Integer, String> executeQuery(JavaPairRDD<Tuple4<Integer, Integer, Integer, String>, Double> values){

        /*

        .reduceByKey :
            Sum values with same day:
            In this way we calculate the number of "sky is clear" in a day

        .mapToPair :
             RDD<(year,month,day,city), count> to RDD<K,V> where:
                K : (year,month,city)
                V : 0 or 1
                    1 : count > N
                        N is the number of "sky is clear" needed to consider "clear" a day
                    0 : else
        .reduceByKey :
            Sum values with same month:
            In this way we calculate the number of clear days in a month

        .filter :
            RDD<(year,month,city), count>  on count: take the Month with at least 15 clear day

        .mapToPair :
            RDD<(year,month,city), count>  to RDD< K,V> where :
                K = year/month
                V = cities

        .groupByKey : group by year/month
        .sortByKey : order by year/month
         */

        //  List<Tuple2<Tuple4<Integer,Integer,Integer,String>,Integer>> list = new ArrayList<>();




        JavaPairRDD<Integer, String> citiesWithClearSky = values
                .reduceByKey(Double::sum)
                .mapToPair((PairFunction<Tuple2<Tuple4<Integer, Integer, Integer, String>, Double>, Tuple3<Integer, Integer, String>, Integer>) tuple -> {
                    if (tuple._2 >= 18.0){ //se almeno 12 ore in un giorno sono serene
                        return new Tuple2<>(new Tuple3<>(tuple._1._1(),tuple._1._2(),tuple._1._4()), 1);
                    }else {
                        return new Tuple2<>(new Tuple3<>(tuple._1._1(),tuple._1._2(),tuple._1._4()), 0);
                    }
                })
                .reduceByKey(Integer::sum)
                .filter((Function<Tuple2<Tuple3<Integer, Integer, String>, Integer>, Boolean>) tuple -> {
                    // città con più di 15 giorni al mese con tempo sereno
                    return tuple._2 >= 15;
                })
                .mapToPair((PairFunction<Tuple2<Tuple3<Integer, Integer, String>, Integer>, Tuple2<Integer,String>,Integer>) tuple -> {
                    return new Tuple2<>(new Tuple2<>(tuple._1()._1(),tuple._1._3()),1);
                })
                .reduceByKey(Integer::sum)
                .filter(v1 -> v1._2()>=3)
                .mapToPair(Tuple2::_1)
                .sortByKey()
                .cache();

       /* Map<Integer, Iterable<String>> map = citiesWithClearSky.collectAsMap();

        for( int x : map.keySet()) {
            System.out.println(x + "  " + map.get(x));
        }*/

       return citiesWithClearSky;
        /*JavaRDD<String> toJson = citiesWithClearSky
                .map(tuple -> new Gson().toJson(tuple));


        toJson.saveAsTextFile("results/query1");*/
        //toJson.saveAsTextFile("results/query1");


    }


}

