package by.tukai.flats.controllers;

import by.tukai.flats.forms.flatform;
import by.tukai.flats.models.Flat;
import by.tukai.flats.repos.FlatRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;

@Slf4j
@Controller
public class mainController {
    @Autowired
    private FlatRep flatsR;

    @GetMapping(value = {"/", "/index"})
    public ModelAndView main(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        log.info("/main was called");
        return modelAndView;
    }
    @GetMapping("/list")
    public ModelAndView list(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("list");
        Iterable<Flat> flats = flatsR.findAll();
        model.addAttribute("flats", flats);
        log.info("/list was called");
        return modelAndView;
    }

    @GetMapping("/addflat")
    public ModelAndView addGet(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("addflat");
        flatform fl = new flatform();
        model.addAttribute("flat", fl);
        log.info("/addflat was called");
        return modelAndView;
    }

    @PostMapping("/addflat")
    public ModelAndView addPost(Model model, @ModelAttribute("flat") flatform fl) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("list");
        String address = fl.getAddress();
        String square = fl.getSquare();
        String rooms = fl.getRooms();
        String price = fl.getPrice();

        Flat fls = flatsR.findByAddress(address);
        if (fls != null){
            model.addAttribute("errorMessage", "Already exists");
            modelAndView.setViewName("addflat");
            log.error("Add: Already exists");
            return modelAndView;
        }
        if(address == null || address.length() == 0
                || square == null || square.length() == 0
                || rooms == null || rooms.length() == 0
                || price == null || price.length() == 0){
            model.addAttribute("errorMessage", "Enter all fields");
            modelAndView.setViewName("addflat");
            log.error("Add: Enter all fields");
            return modelAndView;
        }

        int s, r, p;
        try {
             s = Integer.parseInt(square);
             r = Integer.parseInt(rooms);
             p = Integer.parseInt(price);
        }
        catch(NumberFormatException e) {
            model.addAttribute("errorMessage", "Invalid values");
            modelAndView.setViewName("addflat");
            log.error("Add: Invalid values");
            return modelAndView;
        }

        Flat flat = new Flat(address, s, r, p);
        flatsR.save(flat);
        Iterable<Flat> flats = flatsR.findAll();
        model.addAttribute("flats", flats);
        log.info("New flat added");
        return modelAndView;
    }

    @Transactional
    @GetMapping("/delflat/{address}")
    public ModelAndView del(Model model, @PathVariable(value = "address") String address ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("list");
        flatsR.deleteByAddress(address);
        Iterable<Flat> flats = flatsR.findAll();
        model.addAttribute("flats", flats);
        log.info("Flat deleted");
        return modelAndView;
    }

    @GetMapping("/upflat/{address}")
    public String upGet(Model model, @PathVariable(value = "address") String address ) {
        Flat fl = flatsR.findByAddress(address);
        flatform flr = new flatform(fl.getAddress(), Integer.toString(fl.getSquare()), Integer.toString(fl.getRooms()), Integer.toString(fl.getPrice()));
        model.addAttribute("flat", flr);
        log.info("/update was called");
        return "update";
    }

    @Transactional
    @PostMapping("/upflat/{address}")
    public String upPost(Model model, @ModelAttribute("flat") flatform flr, @PathVariable(value = "address") String address ) {
        Iterable<Flat> flats = flatsR.findAll();
        model.addAttribute("flats", flats);
        if(flr.getPrice() == null || flr.getPrice().length() == 0){
            model.addAttribute("errorMessage", "Update error: enter field");
            log.error("Update: enter field");
            return "list";
        }

        int p;
        try {
            p = Integer.parseInt(flr.getPrice());
        }
        catch(NumberFormatException e) {
            model.addAttribute("errorMessage", "Update error: invalid values");
            log.error("Update: Update error: invalid values");
            return "list";
        }

        Flat fl = flatsR.findByAddress(flr.getAddress());
        fl.setPrice(p);
        flatsR.save(fl);
        log.info("Flat updated");
        return "list";
    }


}