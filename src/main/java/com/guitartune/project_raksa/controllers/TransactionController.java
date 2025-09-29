package com.guitartune.project_raksa.controllers;

import com.guitartune.project_raksa.dto.transaction.TransactionRequest;
import com.guitartune.project_raksa.services.transaction.TransactionServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/buy")
public class TransactionController {
    @Autowired
    private TransactionServiceImpl transactionService;

    @PostMapping("/save-transaction/{id}")
    public String buy(@PathVariable(value = "id") String id, 
                    TransactionRequest transactionRequest, 
                      RedirectAttributes redirectAttributes, Model model) {
        try {
            transactionService.buy(id, transactionRequest);
            redirectAttributes.addFlashAttribute("message", "Pembayaran Berhasil");
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/buy/transaction/" + id;
    }
}
