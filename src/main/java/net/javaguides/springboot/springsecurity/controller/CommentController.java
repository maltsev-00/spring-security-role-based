package net.javaguides.springboot.springsecurity.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javaguides.springboot.springsecurity.model.dto.CommentDto;
import net.javaguides.springboot.springsecurity.service.CommentService;
import net.javaguides.springboot.springsecurity.service.UserLogService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final UserLogService userLogService;

    @PostMapping("/new/{id}")
    public String saveComment(@ModelAttribute("comment") CommentDto comment, @PathVariable("id") Long id, Authentication authentication) {
        log.info("Save comment with message:{}", comment.getMessage());
        String email = authentication.getName();
        userLogService.saveLog(comment.getMessage(), email);
        commentService.save(comment, id, email);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String getComments(@PathVariable("id") Long id, Authentication authentication, Model model) {
        log.info("Get comments with id: {}", id.toString());
        String email = authentication.getName();
        userLogService.saveLog(id.toString(), email);
        model.addAttribute("comments", commentService.findById(id));
        return "comments";
    }
}
