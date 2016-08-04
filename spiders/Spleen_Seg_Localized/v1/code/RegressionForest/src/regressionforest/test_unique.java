package regressionforest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class test_unique {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Integer[] array = {1,5,6,1,5,6,2,5,7,2,5,4};
		
		Set<Integer> unique = new HashSet<Integer>(Arrays.asList(array));
		unique.remove(0);
		Object[] valueObject = unique.toArray();
		int[] value = new int [unique.size()];
		for (int i = 0; i < unique.size(); i++)
			value[i] = ((Integer) valueObject[i]).intValue();
		
		System.out.println(value.toString());
		
	}

}
