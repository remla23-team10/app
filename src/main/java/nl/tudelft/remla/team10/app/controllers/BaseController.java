package nl.tudelft.remla.team10.app.controllers;

import lombok.extern.slf4j.Slf4j;
import nl.tudelft.remla.team10.lib.util.VersionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class BaseController {

    @GetMapping("/")
    public String index(){

        log.info("LIBRARY VERSION: " + VersionUtil.getVersion());

        return "index.html";
    }

    @GetMapping("/history")
    public String getHistoryPage(){
        return "history.html";
    }
}
