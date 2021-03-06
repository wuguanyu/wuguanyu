package com.makenv.controller;

import com.makenv.condition.StationDetailCondition;
import com.makenv.resultAppend.RankResultByInterval;
import com.makenv.resultAppend.RankResultByIntervalInDate;
import com.makenv.resultAppend.RankResultByIntervalInMonth;
import com.makenv.service.*;
import com.makenv.util.DateUtils;
import com.makenv.util.RegionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by wgy on 2016/8/17.
 */
@RestController
public class RegionController extends BaseController {

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CountyService countyService;

    @Autowired
    private StationDetailService stationDetailService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private RankService rankService;

    @RequestMapping(value = "api/avgMonthInRe", method = RequestMethod.GET)
    public Map<String,Object> getMonthResult(@RequestParam("year") Integer year,@RequestParam("month") Integer month, @RequestParam("regionCode") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> resultMap = stationDetailService.getAvgMonthResultByRegionCode(year, month, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA, resultMap);

        return map;
    }


    @RequestMapping(value = "api/year", method = RequestMethod.GET)
    public Map<String,Object> getYearResult(@RequestParam("year") Integer year, @RequestParam("city") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> map1 =  stationDetailService.getAvgYearResultByRegionCode(year, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA, map1);

        return map;
    }

    @RequestMapping(value="api/lastInRe",method = RequestMethod.GET)
    public Map<String,Object> getLastTimeSpanResultData(@RequestParam(value = "unit",defaultValue = "h")String unit,String regionCode,Integer timeSpan){

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> resultData= stationDetailService.getLastTimeSpanResultData(regionCode, timeSpan, unit);

        map.put(RESULT,SUCCESS);

        map.put(DATA, resultData);

        return map;
    }

    @RequestMapping(value={"api/rank"},method = RequestMethod.GET)
    public Map<String,Object> getRankResult(StationDetailCondition stationDetailCondition){

        Map<String,Object> map = new HashMap<String,Object>();

        Map resultDataByArea= rankService.getRankResultDataByArea(stationDetailCondition);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultDataByArea);

        return map;
    }

    @RequestMapping(value={"api/rankByRe"},params = {"rankType=DATE"},method = RequestMethod.GET)
    public Map<String,Object> getRankDayResult(@RequestParam("year")Integer year,@RequestParam("month") Integer month,@RequestParam("date") Integer date,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultMap = rankService.getRankResultDataByRegionCodes(year, month, date, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }

    @RequestMapping(value={"api/rankByRe"},params = {"rankType=MONTH"},method = RequestMethod.GET)
    public Map<String,Object> getRankMonthResult(@RequestParam("year")Integer year,@RequestParam("month") Integer month,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultMap = rankService.getRankResultDataByRegionCodes(year, month, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }

    @RequestMapping(value={"api/rankByRe"},params = {"rankType=HOUR"},method = RequestMethod.GET)
    public Map<String,Object> getRankHourResult(@RequestParam("year")Integer year,@RequestParam("month") Integer month,@RequestParam("date") Integer date,@RequestParam ("hour") Integer hour,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultMap = rankService.getRankResultDataByRegionCodes(year,month,date,hour,regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }

    @RequestMapping(value={"api/rankByRe"},params = {"rankType=YEAR"},method = RequestMethod.GET)
    public Map<String,Object> getRankYearResult(@RequestParam("year")Integer year,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultMap = rankService.getRankResultDataByRegionCodes(year, regionCode);

        //Map resultDataByArea= stationDetailService.getRankResultDataByArea(stationDetailCondition);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }

    @RequestMapping(value={"api/rankByRe"},method = RequestMethod.GET)
    public Map<String,Object> getRankYearResult(@RequestParam("startTime")LocalDateTime startTime,@RequestParam("endTime")LocalDateTime endTime,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultMap = rankService.getRankResultDataByRegionCodes(startTime, endTime, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }

    @RequestMapping(value={"api/rankByInterval1"},method = RequestMethod.POST)
    public Map<String,Object> getRankResultByIntervalInMonth(@RequestParam("startTime")@DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,@RequestParam("endTime")@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime,@RequestParam(value = "regionCode[]")TreeSet<String> regionCode,String rankType){

        Map<String,Object> map = new HashMap<String,Object>();

        LocalDateTime startTime1 = DateUtils.convertDateToLocaleDateTime(startTime);

        LocalDateTime endTime1 = DateUtils.convertDateToLocaleDateTime(endTime);

        if(startTime1.isAfter(endTime1) || endTime1.isAfter(LocalDateTime.now()) || endTime1.isEqual(LocalDateTime.now())) {

            throw new RuntimeException("时间参数异常,请检查");

        }

        Set regionCode1 = RegionUtils.convertRegionCode(regionCode);

        List list = null;

        if(StringUtils.isEmpty(rankType)) {

            list = rankService.getRankResultDataByRegionCodes(startTime1, endTime1, regionCode1);

            map.put(RESULT,SUCCESS);

            map.put(DATA,new RankResultByInterval(list).getResult());

        }

        else if("MONTH".equals(rankType)) {

           list = rankService.getRankResultDataByRegionCodesInMonth(startTime1, endTime1, regionCode1);

           map.put(RESULT,SUCCESS);

           map.put(DATA, new RankResultByIntervalInMonth(list).getResult());
        }

        else if("DATE".equals(rankType)) {

            list = rankService.getRankResultDataByRegionCodesInDate(startTime1,endTime1 , regionCode1);

            map.put(RESULT,SUCCESS);

            map.put(DATA, new RankResultByIntervalInDate(list).getResult());

        }

        return map;
    }


    @RequestMapping(value={"api/rankByInterval"},method = RequestMethod.POST)
    public Map<String,Object> getRankResultByIntervalInMonth1(@RequestParam("startTime")@DateTimeFormat(pattern = "yyyy-MM-dd")Date startTime,@RequestParam("endTime")@DateTimeFormat(pattern = "yyyy-MM-dd")Date endTime,String rankType,String groupId,HttpServletRequest request){

        String regionCode[] = request.getParameterValues("regionCode[]");

        Map<String,Object> map = new HashMap<String,Object>();

        LocalDateTime startTime1 = DateUtils.convertDateToLocaleDateTime(startTime);

        LocalDateTime endTime1 = DateUtils.convertDateToLocaleDateTime(endTime);

        if(startTime1.isAfter(endTime1) || endTime1.isAfter(LocalDateTime.now()) || endTime1.isEqual(LocalDateTime.now())) {

            throw new RuntimeException("时间参数异常,请检查");

        }

        List list = null;

        if(StringUtils.isEmpty(rankType) && !StringUtils.isEmpty(groupId)) {

            list = rankService.getRankResultDataByRegionCodes1(startTime1, endTime1, groupId);

            map.put(RESULT,SUCCESS);

            map.put(DATA,new RankResultByInterval(list).getResult());

        }

        else if(!StringUtils.isEmpty(rankType) && regionCode.length != 0) {

            Set regionCode1 = RegionUtils.convertRegionCode(new TreeSet(Arrays.asList(regionCode)));

            if("MONTH".equals(rankType) && regionCode1.size() != 0) {

                list = rankService.getRankResultDataByRegionCodesInMonth(startTime1, endTime1, regionCode1);

                map.put(RESULT,SUCCESS);

                map.put(DATA, new RankResultByIntervalInMonth(list).getResult());
            }

            else if("DATE".equals(rankType) && regionCode1.size() != 0) {

                list = rankService.getRankResultDataByRegionCodesInDate(startTime1,endTime1 , regionCode1);

                map.put(RESULT,SUCCESS);

                map.put(DATA, new RankResultByIntervalInDate(list).getResult());

            }
        }

        return map;
    }


    @RequestMapping(value={"api/rankNow"},method = RequestMethod.POST)
    public Map<String,Object> getRankNow1(@RequestParam(value = "groupId")String groupId){

        Map<String,Object> map = new HashMap<String,Object>();

        List list = rankService.getRankResultDataNowByGroupBy(groupId);

        map.put(RESULT,SUCCESS);

        map.put(DATA, new RankResultByInterval(list).getResult());

        return map;
    }

    @RequestMapping(value={"api/rankLast24"},method = RequestMethod.POST)
    public Map<String,Object> getRankLast24(@RequestParam(value = "regionCode[]")TreeSet<String> regionCodes){

        Map<String,Object> map = new HashMap<String,Object>();

        List list = rankService.getRankResultDataLast24(regionCodes);

        map.put(RESULT,SUCCESS);

        map.put(DATA, new RankResultByIntervalInDate(list).getResult());

        return map;
    }


    @RequestMapping(value="api/rank-all",method = RequestMethod.GET)
    public Map<String,Object> getRankALLResult(){

        Map<String,Object> map = new HashMap<String,Object>();

        Map resultDataByArea= stationDetailService.getRankALLResultDataByArea();

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultDataByArea);

        return map;
    }

    @RequestMapping(value = "api/baseYear", method = RequestMethod.GET)
    public Map<String,Object> getBaseYearResult(@RequestParam("area") String area,@RequestParam("year") Integer year){

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> map1 =  stationDetailService.getAvgYearResultByRegionCode(year, area);

        map.put(RESULT,SUCCESS);

        map.put(DATA, map1);

        return map;
    }

   /* @RequestMapping(value = "api/rankByRe",method= RequestMethod.GET)
    public Map<String,Object> getRankResultByRe(@RequestParam("year")Integer year,@RequestParam("month")Integer month,@RequestParam(value="date",required = false)Integer date,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List<RankData> list = stationDetailService.getRankResultDataByRes(year, month, date, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,list);

        return map;
    }*/

}
