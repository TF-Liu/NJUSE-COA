package memory.cache.cacheReplacementStrategy;

import memory.cache.Cache;

/**
 * 最近最少用算法
 */
public class LRUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        // 重置该行时间戳
        Cache.getCache().setTimeStamp(rowNO);

    }

    @Override
    public int replace(int start, int end, char[] addrTag, char[] input) {
        long minTime = Long.MAX_VALUE;
        int minIndex = -1;
        for (int i = start; i <= end; i++) {
            long curTime = Cache.getCache().getTimeStamp(i);
            if (curTime < minTime) {
                minTime = curTime;
                minIndex = i;
            }
        }
        Cache.getCache().update(minIndex, addrTag, input);
        return minIndex;
    }

}





























