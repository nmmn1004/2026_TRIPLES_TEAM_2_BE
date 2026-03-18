package com.team2.fabackend.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    ACTIVE("활성"),
    LOCKED("잠김"),
    SLEEP("휴면"),
    DELETED("삭제");

    private final String description;
}
