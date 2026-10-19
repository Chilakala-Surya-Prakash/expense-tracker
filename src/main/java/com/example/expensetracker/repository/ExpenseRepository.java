package com.example.expensetracker.repository;

import com.example.expensetracker.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;



public interface ExpenseRepository extends JpaRepository<Expense, Long>{
    // Get all expenses for a user (newest first)
    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);
    // Filter user's expenses by category
    List<Expense> findByUserIdAndCategoryOrderByExpenseDateDesc(Long userId, Category category);
    // Filter user's expenses between two dates(eg:1st to 31st of a month)
    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    // calculate total spending for a user in a date range

    @Query("SELECT COALESCE(SUM(e.amount),0) FROM Expense e " + "WHERE e.user.id = :userId AND e.expenseDate BETWEEN :startDate AND :endDate ")
    BigDecimal sumExpensesByUserAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    // Group spending by category(return rows of Category , Biigdecimal Sum)

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e " +
    "WHERE e.user.id = :userId AND e.expenseDate BETWEEN :startDate AND :endDate " + 
    "GROUP BY e.category ")

    List<Object[]> sumExpensesByCategoryAndDateRange(
       @Param("userId") Long userId,
       @Param("startDate") LocalDate startDate,
       @Param("endDate") LocalDate endDate);
}