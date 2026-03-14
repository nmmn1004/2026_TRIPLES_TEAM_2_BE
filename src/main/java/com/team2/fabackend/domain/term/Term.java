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

    /**
     * Constructs a new Term with the specified details.
     *
     * @param title    The title of the term.
     * @param content  The detailed content of the term.
     * @param version  The version string of the term.
     * @param required Whether the term is mandatory.
     */
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

    /**
     * Updates the existing term with new details.
     *
     * @param title    The new title.
     * @param content  The new content.
     * @param version  The new version.
     * @param required The new requirement status.
     */
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
