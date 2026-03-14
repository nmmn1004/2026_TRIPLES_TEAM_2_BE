package com.team2.fabackend.domain.ledger;

import com.team2.fabackend.domain.user.User;
import jakarta.persistence.*;
import com.team2.fabackend.domain.ledger.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ledger {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;      
    private String category;  
    private String memo;      

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDate date; 
    private LocalTime time;

    private Long goalId;

    @Column(name = "user_id")
    private Long userId;

    /**
     * Updates the ledger entry with new information.
     *
     * @param amount   The new amount.
     * @param category The new category.
     * @param memo     The new memo.
     * @param type     The new transaction type.
     * @param date     The new date.
     * @param time     The new time.
     */
    public void update(Long amount, String category, String memo, com.team2.fabackend.domain.ledger.TransactionType type, LocalDate date, LocalTime time) {
        this.amount = amount;
        this.category = category;
        this.memo = memo;
        this.type = type;
        this.date = date;
        this.time = time;
    }
}
