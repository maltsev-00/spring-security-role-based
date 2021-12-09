package net.javaguides.springboot.springsecurity.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javaguides.springboot.springsecurity.model.dto.CommentDto;
import net.javaguides.springboot.springsecurity.model.dto.SearchPathFileDto;
import net.javaguides.springboot.springsecurity.service.FileDownloadServiceImpl;
import net.javaguides.springboot.springsecurity.service.PathFileService;
import net.javaguides.springboot.springsecurity.service.StorageService;
import net.javaguides.springboot.springsecurity.service.UserLogService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final StorageService storageService;
    private final PathFileService pathFileService;
    private final FileDownloadServiceImpl fileDownloadServiceImpl;
    private final UserLogService userLogService;

    @GetMapping("/")
    public String root(Model model, Authentication authentication) {
        var username = authentication.getName();
        indexPage(username, model);
        model.addAttribute("pathList", pathFileService.getPathList(username));
        return "index";
    }

    @GetMapping("delete-file/{id}")
    public String deleteFile(@PathVariable("id") Long id, Authentication authentication) {
        pathFileService.deleteFile(id, authentication.getName());
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Authentication authentication) {
        var username = authentication.getName();
        userLogService.saveLog(file.getOriginalFilename(), username);
        storageService.store(file, username);
        return "redirect:/";
    }

    @GetMapping("download/{id}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long id, Authentication authentication) {
        File file = fileDownloadServiceImpl.getFile(id);
        log.info("Download file: {}", file.getName());
        userLogService.saveLog(id.toString(), authentication.getName());
        InputStreamResource isResource = fileDownloadServiceImpl.getInputStreamResource(file);
        var fileSystemResource = new FileSystemResource(file);
        String fileName = FilenameUtils.getName(file.getAbsolutePath());
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        HttpHeaders headers = getHeader(fileSystemResource, fileName);
        return new ResponseEntity<>(isResource, headers, HttpStatus.OK);
    }

    @GetMapping("privacy/{id}")
    public String changePrivacy(@PathVariable("id") Long id, Authentication authentication) {
        log.info("Change privacy with id: {}", id);
        userLogService.saveLog(id.toString(), authentication.getName());
        pathFileService.changePrivacy(id, authentication.getName());
        return "redirect:/";
    }

    @PostMapping("search")
    public String sortByName(@ModelAttribute("search") SearchPathFileDto searchPathFileDto, Model model, Authentication authentication) {
        String username = authentication.getName();
        indexPage(username, model);
        model.addAttribute("pathList", pathFileService.findByNameAndUsername(searchPathFileDto.getName(), username));
        return "index";
    }

    private HttpHeaders getHeader(FileSystemResource fileSystemResource, String fileName) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        try {
            headers.setContentLength(fileSystemResource.contentLength());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        headers.setContentDispositionFormData("attachment", fileName);
        return headers;
    }

    private void indexPage(String user, Model model) {
        userLogService.saveLog("Request is empty", user);
        model.addAttribute("comment", new CommentDto());
        model.addAttribute("search", new SearchPathFileDto());
    }
}
