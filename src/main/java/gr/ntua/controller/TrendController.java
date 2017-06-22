package gr.ntua.controller;

import gr.ntua.entities.Params;
import gr.ntua.domain.Trend;
import gr.ntua.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        Params params = new Params();

        List fromList = params.getPossibleFrom();
        List toList = params.getPossibleTo();

        model.addAttribute("params", params);
        model.addAttribute("fromList", fromList);
        model.addAttribute("toList", toList);

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

    @PostMapping(path = "/bursting")
    public String bursting(@ModelAttribute Params params, Model model) {
        Long fromL = null;
        try {
            fromL = params.convert2timespan(params.getFrom());
        } catch (Exception e) {
            fromL = null;
        }
        Long toL = null;
        try {
            toL = params.convert2timespan(params.getTo());
        } catch (Exception e) {
            toL = null;
        }

        Double percentage = 100.0;
        if ((params.getPercent() != null) && (params.getPercent() > 0)) {
            percentage = new Double(params.getPercent());
        }

        System.out.println("from: "+fromL+" to: "+toL);

        model.addAttribute("trends", trendService.getBursting(percentage, fromL, toL));
        List fromList = params.getPossibleFrom();
        List toList = params.getPossibleTo();

        model.addAttribute("fromList", fromList);
        model.addAttribute("toList", toList);
        return "bursting_topics";
    }

    @PostMapping(path = "/fake")
    public String fake(@ModelAttribute Trend newtrend, Model model, @RequestParam("trendId") Long id) {
        System.out.println("the id it gets is ...  "+id);

        model.addAttribute("trendInfo", trendService.getTrendInfo(id));
        return "topic_info";
    }

    @GetMapping(path = "/bursting/{percent}")
    public @ResponseBody
    Iterable<Trend> getBursting(@PathVariable Double percent,
                                @RequestParam(required = false) String from,
                                @RequestParam(required = false) String to) {
        trendService.updateBursting();

        Long fromL = Long.parseLong(from);
        Long toL = Long.parseLong(to);
        return trendService.getBursting(percent, fromL, toL);
    }

    @GetMapping(path = "/name/{trend_name}")
    public @ResponseBody
    Iterable<Trend> getTrendName(@PathVariable String trend_name) {
        return trendService.getTrendName(trend_name);
    }
}
