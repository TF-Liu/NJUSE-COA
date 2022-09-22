package memory.cache.cacheReplacementStrategy;

import memory.cache.Cache;

/**
 * 最近不经常使用算法
 */
public class LFUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        // 增加该行的访问次数 visited
        Cache.getCache().addVisited(rowNO);
    }

    @Override
    public int replace(int start, int end, char[] addrTag, char[] input) {
        int minVisited = Integer.MAX_VALUE;     // visited最小值
        int minIndex = -1;       // visited最小值对应的下标

        for (int i = start; i <= end; i++) {
            int curVisited = Cache.getCache().getVisited(i);
            if (curVisited < minVisited) {
                minVisited = curVisited;
                minIndex = i;
            }
        }

        Cache.getCache().update(minIndex, addrTag, input);
        return minIndex;
    }
}
