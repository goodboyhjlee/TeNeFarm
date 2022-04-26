package com.tene.controller.house.actgrp.correction;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.tene.Interfaces.ISensor;

public class ActGroupCorectionMap  {
	private Map<Byte,ActGroupCorection> mapCorection = new HashMap<Byte,ActGroupCorection>();
	private float corVar = 0;
	
	private void calculate() {		
		corVar = 0;
		for (Entry<Byte, ActGroupCorection> entCorection : mapCorection.entrySet()) {
			ActGroupCorection cor = entCorection.getValue();
			if (cor.isUse()) {
				float value = cor.getSensorValue();
				byte corValue = cor.getCorVal();
				int corMin = cor.getCorMin();
				int corMax = cor.getCorMax();
				
				if ( (value>corMin) && (value<corMax)  ) {
					float rate = (value-corMin) / (corMax-corMin);
					corVar = corVar + (rate * corValue);
				}
			}
		}
		
		//System.out.println("corVar : " + corVar);
	}
	
	public void put(byte corId,byte corVal, int corMin, int corMax, String sk, boolean isuse, ISensor sensor ) {
		mapCorection.put(corId, new ActGroupCorection(corId,corVal,corMin,corMax,sk,isuse,sensor));
		calculate();
	}
	
	public void update(byte corId,byte corVal, int corMin, int corMax, byte isuse) {
		mapCorection.get(corId).setCorVal(corVal);
		mapCorection.get(corId).setCorMin(corMin);
		mapCorection.get(corId).setCorMax(corMax);
		mapCorection.get(corId).setUse((isuse==1));
		calculate();
	}

	public float getCorVar() {
		return corVar;
	}

}
