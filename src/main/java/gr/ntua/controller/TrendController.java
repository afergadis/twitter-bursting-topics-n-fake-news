package gr.ntua.controller;

import gr.ntua.domain.Trend;
import gr.ntua.entities.Params;
import gr.ntua.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class TrendController {
    private final TrendService trendService;

    @Autowired
    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

    @GetMapping(path = "/")
    public String index(Model model) {
        try {
            Params params = new Params();

            List fromList = params.getPossibleFrom();
            List toList = params.getPossibleTo();

            model.addAttribute("params", params);
            model.addAttribute("fromList", fromList);
            model.addAttribute("toList", toList);
        } catch (Exception e) {
            return "error";
        }

        return "index";
    }

    @GetMapping(path = "/contact")
    public String contact() {
        return "contact";
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

    @PostMapping(path = "/bursting")
    public String bursting(@ModelAttribute Params params, Model model) {
        Long fromL;
        try {
            fromL = params.convert2timespan(params.getFrom());
        } catch (Exception e) {
            fromL = null;
        }
        Long toL;
        try {
            toL = params.convert2timespan(params.getTo());
        } catch (Exception e) {
            toL = null;
        }

        try {
            Double percentage = 100.0;
            if ((params.getPercent() != null) && (params.getPercent() > 0)) {
                percentage = params.getPercent();
            }

            model.addAttribute("trends", trendService.getBursting(percentage, fromL, toL));
            List fromList = params.getPossibleFrom();
            List toList = params.getPossibleTo();

            model.addAttribute("fromList", fromList);
            model.addAttribute("toList", toList);
            model.addAttribute("newtrend", new Trend());
        } catch (Exception ex) {
            return "error";
        }
        return "bursting_topics";
    }

    @PostMapping(path = "/fake")
    public String fake(@ModelAttribute Trend newtrend, Model model, @RequestParam("trendId") Long id) {
        try {
            model.addAttribute("trendInfo", trendService.getTrendInfo(id));
        } catch (Exception e) {
            return "error";
        }
        return "topic_info";
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
