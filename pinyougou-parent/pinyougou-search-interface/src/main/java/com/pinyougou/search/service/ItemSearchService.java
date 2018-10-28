package com.pinyougou.search.service;

import java.util.Map;

/**
 * Created by 96300 on 2018/10/28.
 */
public interface ItemSearchService {

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);

}
