package com.example.expensetracker.service;

import com.example.expensetracker.dto.BudgetSummaryResponse;
import com.example.expensetracker.dto.ExpenseDto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseDto.ExpenseResponse;
import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    // 1. Create a new expense and return response with budget alert
    public ExpenseResponse createExpense(String userEmail, ExpenseRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        LocalDate expenseDate = request.expenseDate() != null ? request.expenseDate() : LocalDate.now();

        Expense expense = Expense.builder()
                .title(request.title())
                .amount(request.amount())
                .category(request.category())
                .merchant(request.merchant())
                .expenseDate(expenseDate)
                .description(request.description())
                .user(user)
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense, user);
    }

    // 2. Fetch all expenses for the logged-in user (newest first)
    public List<ExpenseResponse> getUserExpenses(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        return expenseRepository.findByUserIdOrderByExpenseDateDesc(user.getId())
                .stream()
                .map(exp -> mapToResponse(exp, user))
                .toList();
    }

    // 3. Compute monthly budget summary and category breakdown
    public BudgetSummaryResponse getMonthlyBudgetSummary(String userEmail, int year, int month) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal totalSpent = expenseRepository.sumExpensesByUserAndDateRange(user.getId(), startDate, endDate);
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }

        BigDecimal budget = user.getMonthlyBudget() != null ? user.getMonthlyBudget() : BigDecimal.ZERO;
        BigDecimal remaining = budget.subtract(totalSpent);

        double percentageUsed = budget.compareTo(BigDecimal.ZERO) > 0
                ? totalSpent.multiply(new BigDecimal("100")).divide(budget, 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        String alertStatus = percentageUsed >= 100.0
                ? "EXCEEDED: 100% of monthly budget reached!"
                : (percentageUsed >= 80.0 ? "WARNING: 80% of monthly budget used!" : "SAFE");

        Map<Category, BigDecimal> categoryBreakdown = new HashMap<>();
        for (Object[] row : expenseRepository.sumExpensesByCategoryAndDateRange(user.getId(), startDate, endDate)) {
            categoryBreakdown.put((Category) row[0], (BigDecimal) row[1]);
        }

        return new BudgetSummaryResponse(
                year + "-" + String.format("%02d", month),
                budget,
                totalSpent,
                remaining,
                percentageUsed,
                alertStatus,
                categoryBreakdown
        );
    }

    // 4. Helper method to map Expense entity to ExpenseResponse DTO with live budget status
    private ExpenseResponse mapToResponse(Expense expense, User user) {
        LocalDate now = expense.getExpenseDate();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<Expense> monthlyExpenses = expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
                user.getId(), startOfMonth, endOfMonth
        );

        BigDecimal totalSpent = monthlyExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String alertStatus = "SAFE";
        if (user.getMonthlyBudget() != null && user.getMonthlyBudget().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = totalSpent.multiply(new BigDecimal("100"))
                    .divide(user.getMonthlyBudget(), 2, RoundingMode.HALF_UP);
            if (percentage.compareTo(new BigDecimal("100")) >= 0) {
                alertStatus = "EXCEEDED: 100% of monthly budget reached!";
            } else if (percentage.compareTo(new BigDecimal("80")) >= 0) {
                alertStatus = "WARNING: 80% of monthly budget used!";
            }
        }

        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getMerchant(),
                expense.getExpenseDate(),
                expense.getDescription(),
                alertStatus
        );
    }
    //5. Update the users mothly budget

    public BigDecimal updateMonthlyBudget(String userEmail, BigDecimal newBudget){
        if(newBudget == null || newBudget.compareTo(BigDecimal.ZERO)<0){
                throw new IllegalArgumentException("Monthly budget must be 0 or greater");
        }
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("user not found: " + userEmail));

        user.setMonthlyBudget(newBudget);
        User updatedUser = userRepository.save(user);

        return updatedUser.getMonthlyBudget();
    }
}