package com.example.expensetracker.controller;

import com.example.expensetracker.dto.BudgetSummaryResponse;
import com.example.expensetracker.dto.ExpenseDto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseDto.ExpenseResponse;
import com.example.expensetracker.service.AiExpenseParserService;
import com.example.expensetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")

public class ExpenseController {
    private final ExpenseService expenseService;
    private final AiExpenseParserService aiExpenseParserService;

    // create a manual expense
    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(Principal principal, @RequestBody ExpenseRequest request){
        String email = principal.getName();
        return ResponseEntity.ok(expenseService.createExpense(email, request));
    }

    // parse natural language text using AI and save it
    @PostMapping("/parse-ai")
    public ResponseEntity<ExpenseResponse> parseAndCreateExpense(Principal principal, @RequestBody Map<String, String> payload) {
        String email = principal.getName();
        String rawText = payload.get("text");

        ExpenseRequest request = aiExpenseParserService.parseExpenseFromText(rawText);
        return ResponseEntity.ok(expenseService.createExpense(email, request));
    }
    // Get all expenses for the logged-in user
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getUserExpenses(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(expenseService.getUserExpenses(email)); 
    }
    // Get monthly  budget summary and category breakdown

    @GetMapping("/summary")
    public ResponseEntity<BudgetSummaryResponse> getMonthlySummary(
        Principal principal,
        @RequestParam int year,
        @RequestParam int month){
            String email = principal.getName();
            return ResponseEntity.ok(expenseService.getMonthlyBudgetSummary(email, year, month));
        }
    //Update the users monthly budget
    @PutMapping("/budget")
    public ResponseEntity<Map<String, Object>> updateBudget(
        Principal principal,
        @RequestBody Map<String, BigDecimal> payload){
            String email = principal.getName();
            BigDecimal newBudget = payload.get("monthlyBudget");

            BigDecimal updated = expenseService.updateMonthlyBudget(email, newBudget);
            return ResponseEntity.ok(Map.of("message", "Monthly budget updated successfully", "monthlyBudget", updated

            ));
        }
    
}