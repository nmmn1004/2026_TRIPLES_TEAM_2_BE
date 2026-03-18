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
     * 지정된 세부 정보로 새로운 Term을 생성합니다.
     *
     * @param title    약관 제목입니다.
     * @param content  약관의 상세 내용입니다.
     * @param version  약관의 버전 문자열입니다.
     * @param required 약관이 필수인지 여부입니다.
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
     * 기존 약관을 새로운 세부 정보로 업데이트합니다.
     *
     * @param title    새로운 제목입니다.
     * @param content  새로운 내용입니다.
     * @param version  새로운 버전입니다.
     * @param required 새로운 필수 여부 상태입니다.
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
