package com.java.action;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Transaction {
	private final Trader trader;
	private final int year;
	private final int value;
	public Transaction(Trader trader, int year, int value){
		this.trader = trader;
		this.year = year;
		this.value = value;
	}
	public Trader getTrader(){
		return this.trader;
	}
	public int getYear(){
		return this.year;
	}
	public int getValue(){
		return this.value;
	}
	public String toString(){
		return "{" + this.trader + ", " +
				"year: "+this.year+", " +
				"value:" + this.value +"}";
	}

	public static void main(String[] args) {
		Trader raoul = new Trader("Raoul", "Cambridge");
		Trader mario = new Trader("Mario","Milan");
		Trader alan = new Trader("Alan","Cambridge");
		Trader brian = new Trader("Brian","Cambridge");
		List<Transaction> transactions = Arrays.asList(
				new Transaction(brian, 2011, 300),
				new Transaction(raoul, 2012, 1000),
				new Transaction(raoul, 2011, 400),
				new Transaction(mario, 2012, 710),
				new Transaction(mario, 2012, 700),
				new Transaction(alan, 2012, 950)
				);
		
		
		transactions.stream().filter(t->t.getYear()==2011).sorted(Comparator.comparing(Transaction::getValue)).forEach(System.out::println);
//		transactions.stream().map(t->t.getTrader().getCity()).collect();
		
//		transactions.stream().filter(t->t.getTrader().getCity().equals("Cambridge")).map(t->t.getTrader().getName()).sorted(Trader::getName);
		
		System.out.println(transactions.stream().map(t->t.getTrader().getName()).distinct().reduce((x,y)->x+y).get());
		
		transactions.stream().anyMatch(t->t.getTrader().getCity().equals("Milan")) ;
//		ifPresent(System.out::println);;
		
		transactions.stream().filter(t->t.getTrader().getCity().equals("Cambridge")).map(t->t.getValue()).forEach(System.out::println);
		
		System.out.println(transactions.stream().mapToInt(Transaction::getValue).max().orElse(5));
		System.out.println(transactions.stream().min((x,y)->(x.getValue()-y.getValue())).get());
		
		System.out.println(IntStream.rangeClosed(0,100).filter(n->n%2==0).count());
		
//		IntStream.rangeClosed(0, 100).filter(b -> Math.sqrt(a*a + b*b) % 1 == 0).boxed().map(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});
		
		IntStream.rangeClosed(0, 100).boxed().flatMap(a->IntStream.rangeClosed(a,100).filter(b->Math.sqrt(a*a+b*b)%1==0)
				.mapToObj(b->new int[]{a,b,(int)Math.sqrt(a * a + b * b)}));
		
		
//		filter(b -> Math.sqrt(a*a + b*b) % 1 == 0).boxed().map(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});
		
		
		Stream.of("Java8","Lambadas","In","Action");
		
		
		
		
	}

}
