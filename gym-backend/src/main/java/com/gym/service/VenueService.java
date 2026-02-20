package com.gym.service;

import com.gym.entity.Common;
import com.gym.entity.Venue;
import com.gym.mapper.VenueMapper;
import com.gym.mapper.VenueBookingMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 场地 服务类
 *
 */
@Service
public class VenueService {

    @Resource
    private VenueMapper venueMapper;

    @Resource
    private VenueBookingMapper venueBookingMapper;

    public List<Venue> getAllVenue(int page, int size, String date) {
        String queryDate = (date == null || date.trim().isEmpty()) ? java.time.LocalDate.now().toString() : date.trim();

        List<Venue> venues = venueMapper.getAllVenue(page, size);
        if (venues == null || venues.isEmpty()) {
            return venues;
        }

        List<Map<String, Object>> rows = venueBookingMapper.getBookedSlotsByDate(queryDate);
        Map<Integer, Set<String>> bookedByVenueNo = new HashMap<>();
        if (rows != null) {
            for (Map<String, Object> r : rows) {
                if (r == null) {
                    continue;
                }
                Object v = r.get("venueNo");
                Object t = r.get("timeSlot");
                if (v == null || t == null) {
                    continue;
                }
                int venueNo = (v instanceof Number) ? ((Number) v).intValue() : Integer.parseInt(v.toString());
                String timeSlot = t.toString();
                if (timeSlot.trim().isEmpty()) {
                    continue;
                }
                bookedByVenueNo.computeIfAbsent(venueNo, k -> new HashSet<>()).add(timeSlot);
            }
        }

        for (Venue venue : venues) {
            Integer venueNo = venue.getVenueNo();
            if (venueNo == null) {
                venue.setBookedSlotsByTimeSlot(new HashMap<>());
                continue;
            }
            Set<String> slots = bookedByVenueNo.getOrDefault(venueNo, java.util.Collections.emptySet());
            Map<String, Boolean> slotMap = new HashMap<>();
            for (String slot : slots) {
                slotMap.put(slot, true);
            }
            venue.setBookedSlotsByTimeSlot(slotMap);
        }

        return venues;
    }

    public Map<String,Object> addVenue(Venue venue){
        Map<String,Object> resultMap = new HashMap<>();

        // 设置默认状态
        if (venue.getVenueState() == null || venue.getVenueState().isEmpty()) {
            venue.setVenueState("1"); // 默认可用
        }

        int result =  venueMapper.addVenue(venue);

        if(result>0){
            resultMap.put("code",200);
            resultMap.put("message","添加成功");
        }else{
            resultMap.put("code",400);
            resultMap.put("message","添加失败");
        }

        return resultMap;
    }

    public Map<String,Object>  updateVenue(Venue venue) {
        Map<String,Object> resultMap = new HashMap<>();
        int result =  venueMapper.updateVenue(venue);

        if(result>0){
            resultMap.put("code",200);
            resultMap.put("message","修改成功");
        }else{
            resultMap.put("code",400);
            resultMap.put("message","修改失败");
        }

        return resultMap;
    }

    public Map<String,Object>  deleteVenue(int venueNo) {
        Map<String,Object> resultMap = new HashMap<>();
        int result =  venueMapper.deleteVenue(venueNo);

        if(result>0){
            resultMap.put("code",200);
            resultMap.put("message","删除成功");
        }else{
            resultMap.put("code",400);
            resultMap.put("message","删除失败");
        }

        return resultMap;
    }

    public Common totalVenue() {
        return venueMapper.totalVenue();
    }

    public List<Venue> getByKeywordVenue(String keyWord,int page,int size) {
        return  venueMapper.getByKeywordVenue(keyWord,page,size);
    }

    public  Common totalVenueFuzzy(String keyWord){
        return venueMapper.totalVenueFuzzy(keyWord);
    }

}
