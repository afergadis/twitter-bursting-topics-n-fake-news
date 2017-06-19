package gr.ntua.controller;

import gr.ntua.Params;
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
public class TrendController {
    private final TrendService trendService;

    @Autowired
    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

    @GetMapping(path = "/")
    public String index(Model model) {
        model.addAttribute("params", new Params());
        return "index";
    }

    @GetMapping(path = "/trend")
    public String trendForm(Model model) {
        model.addAttribute("trend", new Trend());
        return "trend";
    }

    @PostMapping(path = "/trend")
    public String trendSubmit(@ModelAttribute Trend trend) {
        return "result";
    }

    // TODO: path /bursting should return a form to input a value
    @PostMapping(path = "/bursting")
    public String bursting(@ModelAttribute Params params, Model model) {
        model.addAttribute("trends", trendService.getBursting(params.getPercent(), params.getFrom(), params.getTo()));
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
