package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by 96300 on 2018/10/27.
 */

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper tbItemMapper;


    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 导入商品数据
     */
    public void importItemData() {
        TbItemExample itemExample = new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        criteria.andStatusEqualTo("1"); //导入已审核的商品
        List<TbItem> itemList = tbItemMapper.selectByExample(itemExample);
        System.out.println("===商品列表===");
        for (TbItem item : itemList) {
            Map specMap = JSON.parseObject(item.getSpec());//将spec字段中的json字符转化为map
            item.setSpecMap(specMap); //给带注解的字段赋值
            System.out.println(item.getTitle());
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("===结束===");
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil  solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");
        solrUtil.importItemData();
    }


}
