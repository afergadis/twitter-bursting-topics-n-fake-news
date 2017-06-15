package gr.ntua.controller;

import gr.ntua.domain.Trend;
import gr.ntua.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by aris on 13/6/2017.
 */
@Controller
@RequestMapping(path = "/trends")
public class TrendController {
    private final TrendService trendService;

    @Autowired
    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

    @GetMapping(path = "/bursting")
    public @ResponseBody
    Iterable<Trend> getBursting() {
        trendService.updateBursting(50);
        return trendService.getBursting();
    }
}
