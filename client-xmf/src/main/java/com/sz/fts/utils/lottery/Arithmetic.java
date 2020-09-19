package com.sz.fts.utils.lottery;



import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;


public class Arithmetic {

	// 放大倍数
	private static final int mulriple = 1000000;


	public static Gem payPrize(List<FivePrize> prizes) {
		int lastScope = 0;
		// 洗牌，打乱奖品次序
		Collections.shuffle(prizes);
		Map<Integer, int[]> prizeScopes = new HashMap<Integer, int[]>();
		Map<Integer, Integer> prizeQuantity = new HashMap<Integer, Integer>();
		for (FivePrize prize : prizes) {
			int prizeId = prize.getRowid();
			// 划分区间
			int currentScope = lastScope + BigDecimal.valueOf(Double.parseDouble(prize.getPrizeprobability())/100).multiply(new BigDecimal(mulriple)).intValue();
			prizeScopes.put(prizeId, new int[] { lastScope + 1, currentScope });
			prizeQuantity.put(prizeId, prize.getPrizenowcount());
			lastScope = currentScope;
		}
		Gem gem = new Gem();
		// 获取1-1000000之间的一个随机数
		int luckyNumber = new Random().nextInt(mulriple);
		// 查找随机数所在的区间
		if ((null != prizeScopes) && !prizeScopes.isEmpty()) {
			Set<Entry<Integer, int[]>> entrySets = prizeScopes.entrySet();
			for (Entry<Integer, int[]> m : entrySets) {
				int key = m.getKey();
				if (luckyNumber >= m.getValue()[0] && luckyNumber <= m.getValue()[1] && prizeQuantity.get(key) > 0) {
					// 奖品主键
					for (FivePrize prize : prizes) {
						if (prize.getRowid().intValue() == key) {
							gem.setName(prize.getPrizename());
							gem.setRowid(key);
						}
					}
					break;
				}
			}
		}
		return gem;
	}
}