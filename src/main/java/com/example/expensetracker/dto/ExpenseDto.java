package com.example.expensetracker.dto;
import com.example.expensetracker.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDto{

    //sent by user when creating a new expense

    public record ExpenseRequest(
        @NotBlank(message = "Title is required") String title,
        @NotNull @Positive(message = "Amount must be greater than 0") BigDecimal amount,
        @NotNull(message = "Category is required") Category category,
        String merchant,
        LocalDate expenseDate,
        String description
    ){}

     // Natural language text input from user for our AI parser to extract expense data

     public record AiParseRequest(
        @NotBlank(message = "Text input is required") String rawText
     ){}

     //clean JSON response sent back to the user after creating a new expense
     public record ExpenseResponse(
        Long id,
        String title,
        BigDecimal amount,
        Category category,
        String merchant,
        LocalDate expenseDate,
        String description,
        String budgetAlert

     ){}
    }