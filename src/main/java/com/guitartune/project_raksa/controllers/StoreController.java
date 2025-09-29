package com.guitartune.project_raksa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guitartune.project_raksa.dto.store.RegisterStoreRequest;
import com.guitartune.project_raksa.services.store.StoreService;

@Controller
@RequestMapping("/stores")
public class StoreController {
    @Autowired
    private StoreService storeService;

    @PostMapping("/save-register-store")
    public String saveRegisterStore(RegisterStoreRequest registerStoreRequest, RedirectAttributes redirectAttributes,
            @RequestParam("file") MultipartFile file) {
        try {
            storeService.registerStore(registerStoreRequest, file);
            return "redirect:/stores/home-store";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/stores/register-store";
        }
    }

    @PostMapping("/withdraw")
    public String withdrawIncome(@RequestParam("jum") Long jum, RedirectAttributes redirectAttributes) {
        try {
            storeService.withdrawIncome(jum);
            redirectAttributes.addFlashAttribute("message", "Withdrawal successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error during withdrawal: " + e.getMessage());
        }
        return "redirect:/stores/home-store"; 
    }

}
