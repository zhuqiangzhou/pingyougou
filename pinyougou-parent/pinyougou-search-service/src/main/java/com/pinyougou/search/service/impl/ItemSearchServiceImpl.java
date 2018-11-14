package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 96300 on 2018/10/28.
 */
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Map<String, Object> search(Map searchMap) {

        Map<String,Object> map=new HashMap<>();
       // Query query = new SimpleQuery("*:*");
        //添加查询条件
      /*  Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);*/



        //map.put("rows", page.getContent());
        //1.查询列表
        map.putAll(searchList(searchMap));


        //2.分组查询商品分类列表
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //3.查询品牌和规格列表
        if (categoryList.size()>0) {
            map.putAll(searchBrandAndSpecList(categoryList.get(0)));
        }
        return map;
    }

    //查询列表
    private Map searchList(Map searchMap) {

        Map<String,Object> map=new HashMap<>();
        //高亮选项初始化
        HighlightQuery highlightQuery = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title"); //设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        highlightQuery.setHighlightOptions(highlightOptions);//设置高亮选项

        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        highlightQuery.addCriteria(criteria);

        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        //获取高亮集合的入口
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();

        for (HighlightEntry<TbItem> entry : entryList ) {
            //获取高亮列表（高亮域的个数）
            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();

            if(highlightList.size()>0 &&  highlightList.get(0).getSnipplets().size()>0 ) {
                TbItem item = entry.getEntity();
                item.setTitle(highlightList.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", page.getContent());
        return map;
    }

    /**
     * 查询分类列表
     * @param searchMap
     * @return
     */

    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<String>();
        Query query = new SimpleQuery("*:*");
        //根据关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));//where....
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category"); //group by
        query.setGroupOptions(groupOptions);
        //获取分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获得分组页结果对象
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //获取分页入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分页入口集合
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();

        for (GroupEntry<TbItem> entry:entryList) {
            list.add(entry.getGroupValue()); //将分组的结果添加到返回值中
        }
        return list;

    }

    /**
     * 根据商品分类名称查询品牌和规格列表
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        //1.根据商品分类名称得到模版ID
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

            if (templateId!=null) {
            //2.根据模版ID获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList",brandList);
            System.out.println("品牌列表条数："+brandList.size());
            //3.根据模版ID获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList",specList);
            System.out.println("规格列表条数："+specList.size());
        }
        return map;

    }
}
