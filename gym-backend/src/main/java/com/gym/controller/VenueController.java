package com.gym.controller;

import com.gym.entity.Common;
import com.gym.entity.Venue;
import com.gym.service.VenueService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 场地 控制层
 *
 * @author: ShanZhu
 * @date: 2024-12-26
 */
@RestController
@RequestMapping("/api")
public class VenueController {

    @Resource
    private VenueService venueService;

    @RequestMapping(path = "/getAllVenue")
    public List<Venue> getAllVenue(int page, int size, String date) {
        return venueService.getAllVenue(page, size, date);
    }

    @RequestMapping(path = "/addVenue")
    public Map<String, Object> addVenue(Venue venue) {
        return venueService.addVenue(venue);
    }

    @RequestMapping(path = "/updateVenue")
    public Map<String, Object> updateVenue(Venue venue) {
        return venueService.updateVenue(venue);
    }

    @RequestMapping(path = "/deleteVenue")
    public Map<String, Object> deleteVenue(int venueNo) {
        return venueService.deleteVenue(venueNo);
    }

    @RequestMapping(path = "/totalVenue")
    public Common totalVenue() {
        return venueService.totalVenue();
    }

    @RequestMapping(path = "/getByKeywordVenue")
    public List<Venue> getByKeywordVenue(String keyWord, int page, int size) {
        return venueService.getByKeywordVenue(keyWord, page, size);
    }

    @RequestMapping(path = "/totalVenueFuzzy")
    public Common totalVenueFuzzy(String keyWord) {
        return venueService.totalVenueFuzzy(keyWord);
    }

}
