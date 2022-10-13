package com.bwagih.websocket.controller;

import java.util.List;

import com.bwagih.websocket.models.Menu;
import com.bwagih.websocket.models.Person;
import com.bwagih.websocket.models.ResponseModel;
import com.bwagih.websocket.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping( "/menu")
public class MenuController {

    @Autowired
    private MenuRepository menuRepo;

    @PostMapping( "/save")
    public Menu save(@RequestBody Menu menuDetails) {

        return menuRepo.save(menuDetails);
    }

    @PostMapping( "/saveAllPerson")
    public ResponseModel saveAllPerson(@RequestBody List<Person> tList) {

        List<Person> people = menuRepo.findAll0();

        people.forEach(person -> System.out.println(person.toString()));

        return menuRepo.saveAll(tList);
    }


    @GetMapping( "/findAllPerson")
    public List<Person> findAllPerson(HttpServletRequest request) {
        return menuRepo.findAll0();
    }

    @GetMapping("/list")
    public List<Menu> getMenus() {
        return menuRepo.findAll();
    }

    @GetMapping( "/getById/{id}")
    public Menu findMenuItemById(@PathVariable int id) {
        return menuRepo.findItemById(id);
    }

    @DeleteMapping( "/deleteById/{id}")
    public String deleteMenuById(@PathVariable int id)   {
        return menuRepo.deleteMenu(id);
    }

}
