package memory.cache.cacheReplacementStrategy;

public interface ReplacementStrategy {


    /**
     * 结合具体的替换策略，进行命中后进行相关操作
     * @param rowNO 行号
     */
    void hit(int rowNO);

    /**
     * 结合具体的映射策略，在给定范围内对cache中的数据进行替换
     * @param start 起始行
     * @param end 结束行 闭区间
     * @param addrTag tag
     * @param input  数据
     */
    int replace(int start, int end, char[] addrTag, char[] input);

}
