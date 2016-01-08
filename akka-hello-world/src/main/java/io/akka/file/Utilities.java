/**
 * 
 */
package io.akka.file;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gurmi
 *
 */
public class Utilities {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String,Integer> hashMap = new HashMap<String,Integer>();
		hashMap.put("abc", 4);
		hashMap.put("pqr", 10);
		hashMap.put("xyz", 17);
		
		Integer ctr = hashMap.get("abc");
		ctr = ctr + 10;
		System.out.println("result:"+hashMap.get("abc"));

	}

}
