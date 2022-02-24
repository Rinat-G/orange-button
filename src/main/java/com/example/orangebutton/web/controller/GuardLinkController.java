package com.example.orangebutton.web.controller;

import com.example.orangebutton.domain.service.GuardLinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class GuardLinkController {
    private final GuardLinkService service;

    public GuardLinkController(GuardLinkService service) {
        this.service = service;
    }

    @GetMapping("/{session}")
    public String getLinkPage(@PathVariable String session) {
        var links = service.getLinks(session);

        var sb = new StringBuilder();
        for (var link : links) {
            sb.append("<li>")
                    .append("<a href=\"")
                    .append(link.getSignedLink())
                    .append("\">")
                    .append(link.getSignedLink(), 0, 109)
                    .append("...</a>")
                    .append(" ")
                    .append(link.getSizeMb())
                    .append(" MB")
                    .append("</li>\n");

        }
        return "<html>\n" +
                "<header><title>Welcome</title></header>\n" +
                "<body>\n" +
                "<ul>" +
                sb.toString() +
                "</ul>" +
                "</body>\n" +
                "</html>";
//        return service.getLinks();
    }
}
