package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌列表
 * Created by 96300 on 2018/8/18.
 */
public interface BrandService {

    public List<TbBrand> findAll();


    //返回分页列表
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbBrand brand);

    /**
     * 修改
     */
    public void update(TbBrand brand);

    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);


    /**
     * 批量删除
     * @param ids
     */
    public void delete(Long [] ids);

    /**
     * 品牌分页
     */
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize);

    /**
     * 品牌下拉框数据
     */
    List<Map> selectOptionList();

}
