package nl.tudelft.remla.team10.app.controllers;

import lombok.extern.slf4j.Slf4j;
import nl.tudelft.remla.team10.app.services.ModelService;
import nl.tudelft.remla.team10.lib.util.VersionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class BaseController {

    @Autowired
    private ModelService modelService;

    @GetMapping("/")
    public String index(){

        log.info("LIBRARY VERSION: " + VersionUtil.getVersion());

        return "index.html";
    }

    @GetMapping("/history")
    public String history(){
        return "history.html";
    }

    @GetMapping("/incrementalTraining")
    public String IncrementalTraining(){
        try {
            modelService.incrementalTrain();
        } catch (Exception e) {
            log.error("Error while calling model service: ", e);
        }

        return "redirect:/history";
    }
}
