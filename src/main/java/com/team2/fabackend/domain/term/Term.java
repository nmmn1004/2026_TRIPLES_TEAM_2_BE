package com.team2.fabackend.domain.term;

import com.team2.fabackend.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "terms")
public class Term extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Lob
    private String content;
    @Column(nullable = false)
    private String version;
    @Column(nullable = false)
    private boolean required;

    @Builder
    protected Term(
            String title,
            String content,
            String version,
            boolean required
    ) {
        this.title = title;
        this.content = content;
        this.version = version;
        this.required = required;
    }

    public void updateTerm(
            String title,
            String content,
            String version,
            boolean required
    ) {
        this.title = title;
        this.content = content;
        this.version = version;
        this.required = required;
    }

}
