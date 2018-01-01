import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import scala.Tuple5;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.util.LongAccumulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Voldemort {
	private static final String path = "/Users/akash/input/inp.*";

	public static Iterator<String> getWordsFromLine(String line) {
		line = line.toLowerCase().replaceAll("[^A-Za-z]", " ");
		line = line.replaceAll("( )+", " ");
		line = line.trim();
		List<String> wordList = Arrays.asList(line.split(" "));
		return wordList.iterator();
	}

	public static Iterator<Tuple2<String, String>> getWordpairsFromLine(String line) {
		line = line.toLowerCase().replaceAll("[^A-Za-z]", " ");
		line = line.replaceAll("( )+", " ");
		line = line.trim();
		List<String> wordList = Arrays.asList(line.split(" "));
		List<Tuple2<String, String>> wordpairList = new ArrayList<Tuple2<String, String>>();
		for(int i = 0; i < wordList.size() - 1; i++)
			wordpairList.add(new Tuple2<>(wordList.get(i),wordList.get(i+1)));
		return wordpairList.iterator();
	}

	public static void main(String[] args) {
	    SparkConf conf = new SparkConf().setAppName("Ministry of Magic");
	    JavaSparkContext sc = new JavaSparkContext(conf);
	    sc.hadoopConfiguration().set("textinputformat.record.delimiter",".");

	    JavaRDD<String> lines = sc.textFile(path).cache();
	    JavaRDD<String> words = lines.flatMap(s -> getWordsFromLine(s));
	    JavaPairRDD<String, Integer> wordone = words.mapToPair(s -> new Tuple2<>(s, 1));
	    JavaRDD<Tuple2<String, String>> wordpairs = lines.flatMap(s -> getWordpairsFromLine(s));
	    JavaPairRDD<Tuple2<String, String>, Integer> wordpairone = wordpairs.mapToPair(s -> new Tuple2<Tuple2<String, String>, Integer>(s, 1));
	    JavaPairRDD<String, Integer> wordcounts = wordone.reduceByKey((a, b) -> a + b);
	    JavaPairRDD<Tuple2<String, String>, Integer> wordpaircounts = wordpairone.reduceByKey((a, b) -> a + b);

	    LongAccumulator TotalCounter = sc.sc().longAccumulator();
	    wordcounts.foreach(t -> {TotalCounter.add(t._2);});
	    LongAccumulator pairTotalCounter = sc.sc().longAccumulator();
	    wordpaircounts.foreach(t -> {pairTotalCounter.add(t._2);});

	    Broadcast<Integer> TotalCnt = sc.broadcast(new Integer(TotalCounter.value().intValue()));
	    Broadcast<Integer> pairTotalCnt = sc.broadcast(new Integer(pairTotalCounter.value().intValue()));

	    JavaPairRDD<String,Tuple2<String, Integer>> firstIntermediate =
	    		wordpaircounts.mapToPair(s -> new Tuple2<>(s._1()._1(), new Tuple2<>(s._1()._2(), s._2())));
	    JavaPairRDD<String,Tuple2<Tuple2<String, Integer>, Integer>> firstJoin = firstIntermediate.join(wordcounts);
	    JavaPairRDD<String,Tuple2<Tuple2<String, Integer>, Integer>> secondIntermediate =
	    		firstJoin.mapToPair(s -> new Tuple2<>(s._2()._1()._1(), new Tuple2<>(new Tuple2<>(s._1(), s._2()._1()._2()), s._2()._2())));
	    JavaPairRDD<String,Tuple2<Tuple2<Tuple2<String, Integer>, Integer>, Integer>> secondJoin = secondIntermediate.join(wordcounts);
	    JavaRDD<Tuple5<String, String, Double, Double, Double>> finalTable =
	    		secondJoin.map(s -> new Tuple5<String, String, Double, Double, Double>(s._2()._1()._1()._1(), s._1(), s._2()._1()._1()._2()/pairTotalCnt.value().doubleValue(), s._2()._1()._2()/TotalCnt.value().doubleValue(), s._2()._2()/TotalCnt.value().doubleValue()));
	    JavaRDD<Tuple5<String, String, Double, Double, Double>> filteredTable = finalTable.filter(s -> s._3() > 10*s._4()*s._5());
	    JavaPairRDD<Double, Tuple5<String, String, Double, Double, Double>> sortingIntermediate =
	    		filteredTable.mapToPair(s -> new Tuple2<Double, Tuple5<String, String, Double, Double, Double>>(s._3()/(s._4()*s._5()), s));
	    JavaPairRDD<Double, Tuple5<String, String, Double, Double, Double>> sortedTable = sortingIntermediate.sortByKey(false);
	    JavaRDD<Tuple5<String, String, Double, Double, Double>> requiredTable = sortedTable.map(s -> s._2());

	    List<Tuple5<String, String, Double, Double, Double>> finallist = requiredTable.collect();
	    for(Tuple5<String, String, Double, Double, Double> t: finallist)
	    		System.out.println(t._1() + "," + t._2() + "," + t._3() + "," + t._4() + "," + t._5());

	    sc.close();
	}
}
