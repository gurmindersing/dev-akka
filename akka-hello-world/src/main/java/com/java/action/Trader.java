package com.java.action;

public class Trader {

	private final String name;
	private final String city;
	public Trader(String n, String c){
		this.name = n;
		this.city = c;
	}
	public String getName(){
		return this.name;
	}
	public String getCity(){
		return this.city;
	}
	public String toString(){
		return "Trader:"+this.name + " in " + this.city;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
