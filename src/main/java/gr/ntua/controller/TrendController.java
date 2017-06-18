package gr.ntua.controller;

import gr.ntua.domain.Trend;
import gr.ntua.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(path = "/")
    public String index() {
        return "index";
    }

    // TODO: path /bursting should return a form to input a value
    @GetMapping(path = "/bursting")
    public String bursting(Model model) {
        model.addAttribute("trends", trendService.getBursting(200.0, null, null));
        return "bursting_topics";
    }

    @GetMapping(path = "/bursting/{percent}")
    public @ResponseBody
    Iterable<Trend> getBursting(@PathVariable Double percent,
                                @RequestParam(required = false) Long from,
                                @RequestParam(required = false) Long to) {
        trendService.updateBursting();
        return trendService.getBursting(percent, from, to);
    }

    @GetMapping(path = "/name/{trend_name}")
    public @ResponseBody
    Iterable<Trend> getTrendName(@PathVariable String trend_name) {
        return trendService.getTrendName(trend_name);
    }
}
