package gr.ntua.controller;

import gr.ntua.domain.Trend;
import gr.ntua.entities.Params;
import gr.ntua.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
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

            List<Date> dates = trendService.getDateFromTo();
            model.addAttribute("params", params);
            model.addAttribute("from", dates.get(0));
            model.addAttribute("to", dates.get(1));
        } catch (Exception e) {
            e.printStackTrace();
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
//        trendService.updateBursting(); // TODO: Move updateBursting to TrendCollector
        try {
            Double percentage = 0.0;
            if ((params.getPercent() != null) && (params.getPercent() > 0)) {
                percentage = params.getPercent();
            }

            Date fromDate = null;
            Date toDate = null;
            try {
                fromDate = params.convertFromToDate();
                toDate = params.convertUntilToDate();
            } catch (ParseException | NullPointerException ignored) {
            } // Null Pointer means no dates given. That's ok, we pass them as nulls

            //TODO: pass the fromDate, toDate as parameters (null is for all trends)
            model.addAttribute("trends", trendService.getBursting(percentage, fromDate, toDate));
            model.addAttribute("newtrend", new Trend());
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }
        return "bursting_topics";
    }

    @PostMapping(path = "/fake")
    public String fake(@ModelAttribute Trend newtrend, Model model, @RequestParam("trendId") Long id) {
        try {
            model.addAttribute("trendInfo", trendService.getTrendInfo(id));
        } catch (Exception e) {
            e.printStackTrace();
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
