package com.example.expensetracker.dto;

import com.example.expensetracker.entity.Category;
import java.math.BigDecimal;

import java.util.Map;

public record BudgetSummaryResponse(
    String month,
    BigDecimal monthlyBudget,
    BigDecimal totalSpent,
    BigDecimal remainingBudget,
    double percentageUsed,
    String alertStatus,
    Map<Category, BigDecimal> categoryBreakdown
){}
