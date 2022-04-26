package com.tene.utils;

public class GetBit {
	public static long getBit(long n, int k) {
	    return (n >> k) & 1;
	}
	
	public static void setBit(Long n, byte k, byte v) {
		
		
		 java.util.BitSet kkk = new java.util.BitSet(64);
		 
		 kkk.set(0, true);
		 kkk.set(16, true);
		 kkk.set(17, false);
		 
		 if (kkk.isEmpty()) {
			 System.out.println("0000");
		 } else {
			 for (byte kk = 0; kk<kkk.length(); kk++) {
				 if (kkk.get(kk)) {
					 System.out.println(kk);
				 }
				 
			 }
		 }
		 
		 System.out.println(kkk.toString());
		 
		 //kkk.set
		
		if (v == 1) {
			System.out.println(1);
			n |= 1 << k;
		} else {
			System.out.println(0);
			n &= ~(1 << k);
		}
		
		System.out.println(n);
	}
}
