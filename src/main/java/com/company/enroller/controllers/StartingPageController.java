package com.company.enroller.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartingPageController {

    @RequestMapping(value = "/")
    @ResponseBody
    public String mainPage() {
        String simpleInfo =
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Zadanie</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "\n" +
                        "Michał R - zadanie:<br>\n" +
                        "Na ocenę 4.0:<br>\n" +
                        "\n" +
                        "    <br>Pobieranie listy wszystkich spotkań\n" +
                        "    <br>Pobieranie listy pojedyncznego spotkania\n" +
                        "    <br>Dodawanie spotkań\n" +
                        "    <br>Dodawanie uczestnika do spotkania\n" +
                        "    <br>Pobieranie uczestników spotkania" +
                        "<br><br><a href=\"https://github.com/michalrys/agh-mwo-enroller\" target=new>https://github.com/michalrys/agh-mwo-enroller</a>" +
                        "</body>\n" +
                        "</html>";

        return "index";
    }
}
