package com.guitartune.project_raksa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guitartune.project_raksa.dto.RegisterRequest;
import com.guitartune.project_raksa.dto.UpdateUserRequest;
import com.guitartune.project_raksa.services.user.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/save-user")
    public String saveRegister(RegisterRequest registerRequest, RedirectAttributes redirectAttributes) {
        try {
            userService.register(registerRequest);
            redirectAttributes.addFlashAttribute("message", "User created successfully");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/save-update-profile/{id}")
    public String saveUpdateProfile(@PathVariable(value = "id") String id, UpdateUserRequest updateUserRequest,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(updateUserRequest);
            redirectAttributes.addFlashAttribute("message", "User updated successfully");
            return "redirect:/user/profile";
        } catch (Exception e) {
            System.out.println("gagal");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/user/update-profile/" + id;
        }
    }

    @PostMapping("/topup")
    public String tambahSaldo(@RequestParam(name = "saldo") Long saldo, RedirectAttributes redirectAttributes) {
        try {
            userService.tambahSaldo(saldo);
            redirectAttributes.addFlashAttribute("message", "Saldo berhasil ditambahkan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }
}
