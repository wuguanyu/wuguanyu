package com.makenv.service.impl;

import com.makenv.mapper.CountyMapper;
import com.makenv.service.CountyService;
import com.makenv.vo.CountyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/12.
 */
@Service
public class CountyServiceImpl implements CountyService {

    @Autowired
    private CountyMapper countyMapper;


    public List<CountyVo> getAllCountyList(){


        return countyMapper.getAllCountyList();


    }
}
